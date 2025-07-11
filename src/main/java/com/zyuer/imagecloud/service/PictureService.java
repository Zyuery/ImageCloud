package com.zyuer.imagecloud.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyuer.imagecloud.domain.dto.picture.PictureQueryRequest;
import com.zyuer.imagecloud.domain.dto.picture.PictureUploadByBatchRequest;
import com.zyuer.imagecloud.domain.dto.picture.PictureUploadRequest;
import com.zyuer.imagecloud.domain.pojo.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyuer.imagecloud.domain.pojo.User;
import com.zyuer.imagecloud.domain.vo.picture.PictureVO;
import org.springframework.web.multipart.MultipartFile;
import com.zyuer.imagecloud.domain.dto.picture.PictureReviewRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.List;

/**
* @author zengyue
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-07-03 19:41:04
*/
public interface PictureService extends IService<Picture> {

    PictureVO uploadPicture(Object object,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);
    PictureVO getPictureVO(Picture picture);
    Page<PictureVO> getPictureVOList(Page<Picture> picturePage);
    void validPicture(Picture picture);
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage);
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);
    public void fillReviewParams(Picture picture ,User loginUser);

    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser);
    public void clearPictureFile(Picture oldPicture) throws MalformedURLException;

}
