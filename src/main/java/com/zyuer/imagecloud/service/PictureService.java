package com.zyuer.imagecloud.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyuer.imagecloud.domain.dto.picture.PictureQueryRequest;
import com.zyuer.imagecloud.domain.dto.picture.PictureUploadRequest;
import com.zyuer.imagecloud.domain.pojo.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyuer.imagecloud.domain.pojo.User;
import com.zyuer.imagecloud.domain.vo.picture.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author zengyue
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-07-03 19:41:04
*/
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    PictureVO getPictureVO(Picture picture);
    Page<PictureVO> getPictureVOList(Page<Picture> picturePage);
    void validPicture(Picture picture);
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage);
}
