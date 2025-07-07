package com.zyuer.imagecloud.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyuer.imagecloud.annotation.AuthCheck;
import com.zyuer.imagecloud.common.UserConstant;
import com.zyuer.imagecloud.domain.dto.normal.DeleteRequest;
import com.zyuer.imagecloud.domain.dto.picture.*;
import com.zyuer.imagecloud.domain.pojo.Picture;
import com.zyuer.imagecloud.domain.pojo.User;
import com.zyuer.imagecloud.domain.vo.picture.PictureTagCategory;
import com.zyuer.imagecloud.domain.vo.picture.PictureVO;
import com.zyuer.imagecloud.domain.vo.result.BaseResponse;
import com.zyuer.imagecloud.domain.vo.result.ResultUtils;
import com.zyuer.imagecloud.service.PictureService;
import com.zyuer.imagecloud.exception.ErrorCode;
import com.zyuer.imagecloud.exception.ThrowUtils;
import com.zyuer.imagecloud.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class PictureController {

    @Resource
    private PictureService pictureService;
    @Resource
    private UserService userService;

    @PostMapping("/upload")
    public BaseResponse<PictureVO> uploadPicture(
            @RequestParam("file") MultipartFile multipartFile,
            //@ModelAttribute("PictureUploadRequest") 注解自动的装配form-data里的数据都一个对象里
            @ModelAttribute("PictureUploadRequest") PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request
    ){
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest,HttpServletRequest request){
        ThrowUtils.throwIf(deleteRequest==null||deleteRequest.getId()<=0,
                ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        //判断图片是否存在
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture==null,ErrorCode.NOT_FOUND_ERROR);
        //只用用户自己上传的图片或者管理员能删除
        ThrowUtils.throwIf(!oldPicture.getUserId().equals(loginUser.getId())
                && !userService.isAdmin(loginUser),
                ErrorCode.NO_AUTH_ERROR);
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }


    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
                                               HttpServletRequest request) {
        ThrowUtils.throwIf(pictureUpdateRequest == null
                        || pictureUpdateRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        // 将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 数据校验
        pictureService.validPicture(picture);
        // 判断是否存在
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);

        User loginUser = userService.getLoginUser(request);
        pictureService.fillReviewParams(picture, loginUser);

        // 操作数据库
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(picture);
    }

    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(pictureService.getPictureVO(picture));
    }

    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPicturePage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getPageNow();
        long size = pictureQueryRequest.getPageSize();
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(picturePage);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getPageNow();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        // 获取封装类
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage));
    }

    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest,
                                             HttpServletRequest request) {
        ThrowUtils.throwIf(pictureEditRequest == null
                        || pictureEditRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        picture.setEditTime(new Date());
        pictureService.validPicture(picture);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        pictureService.fillReviewParams(picture, loginUser);
        // 仅本人或管理员可编辑
        ThrowUtils.throwIf(!oldPicture.getUserId().equals(loginUser.getId())
                        && !userService.isAdmin(loginUser),
                ErrorCode.NO_AUTH_ERROR);
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }


    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest,
                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.doPictureReview(pictureReviewRequest, loginUser);
        return ResultUtils.success(true);
    }


}
