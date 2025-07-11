package com.zyuer.imagecloud.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.zyuer.imagecloud.config.CosClientConfig;
import com.zyuer.imagecloud.domain.dto.file.UploadPictureResult;
import com.zyuer.imagecloud.exception.BusinessException;
import com.zyuer.imagecloud.exception.ErrorCode;
import com.zyuer.imagecloud.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 文件服务
 * @deprecated 已废弃，改为使用 upload 包的模板方法优化
 */
@Deprecated
@Component
@Slf4j
public class FileManager {
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private CosManager cosManager;

    public UploadPictureResult uploadPicture(MultipartFile multipartFile,String uploadPathPrefix) {
        //检验图片
        validatePicture(multipartFile);
        //生成图片上传地址
        String uuid = RandomUtil.randomString(16);
        //注意：文件名是包含后缀的。此外，一些老版本浏览器可能会带上文件的路径信息，我们不需要这些路径信息（需要用FileUtil,mainName(originalFileName)取出最小有效名）
        //但在现在这一步无所谓，因为我们只需要从originalFileName中取出suffix后缀即可
        String originalFileName = multipartFile.getOriginalFilename();
        //上传文件名（不包含文件名！）————时间戳_随机数.文件后缀
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()),uuid,FileUtil.getSuffix(originalFileName));
        //上传路径（不包含文件名！）————指定的uploadPathPrefix（一般为userId/picture）/时间戳_随机数.文件后缀
        String uploadPath = String.format("%s/%s", uploadPathPrefix,uploadFileName);
        File file = null;
        try{
            // 创建临时文件（在服务器系统临时目录生成唯一文件，前缀使用uploadPath）
            file = File.createTempFile(uploadPath,null);
            // - 将 Spring 的 MultipartFile 内容写入磁盘（写入那个临时文件，最后这个服务器上的临时文件会在调用oss方法上传后会被delete）
            // - 对于大文件，Spring 会先将上传文件存储为临时文件，该方法本质是文件重命名操作（非网络操作）
            multipartFile.transferTo(file);
            //上传图片
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath,file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            //封装返回结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth*1.0/picHeight,2).doubleValue();
            //FileUtil.mainName(originalFileName) 提取原始文件名的主名称（不含扩展名），用于展示或存储元数据。比较屌
            uploadPictureResult.setPicName(FileUtil.mainName(originalFileName));
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setUrl(cosClientConfig.getHost()+"/"+uploadPath);
            return uploadPictureResult;

        } catch (IOException e) {
            log.info("oss对象上传失败",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文件上传失败");
        }finally {
            this.deleteTempFile(file);
        }
    }

    public UploadPictureResult uploadPictureByUrl(String url,String uploadPathPrefix) {
        validatePicture(url);
        String uuid = RandomUtil.randomString(16);
        String originFileName = FileUtil.mainName(url);
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()),uuid,FileUtil.getSuffix(originFileName));
        String uploadPath = String.format("%s/%s", uploadPathPrefix,uploadFileName);
        File file = null;
        try{
            file = File.createTempFile(uploadPath,null);
            HttpUtil.downloadFile(url,file);
            //上传图片
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath,file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            //封装返回结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth*1.0/picHeight,2).doubleValue();
            //FileUtil.mainName(originalFileName) 提取原始文件名的主名称（不含扩展名），用于展示或存储元数据。比较屌
            uploadPictureResult.setPicName(FileUtil.mainName(originFileName));
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setUrl(cosClientConfig.getHost()+"/"+uploadPath);
            return uploadPictureResult;
        } catch (IOException e) {
            log.info("url对象上传失败",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文件上传失败");
        }finally {
            this.deleteTempFile(file);
        }
    }

    public void validatePicture(String fileUrl){
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl),
                ErrorCode.PARAMS_ERROR,
                "文件地址不能为空");
        // 1. 验证 URL 格式
        try{
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"url格式错误");
        }
        // 2. 校验 URL 协议
        ThrowUtils.throwIf(!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://")),
                ErrorCode.PARAMS_ERROR,
                "仅支持 HTTP 或 HTTPS 协议的文件地址");
        // 3. 发送 HEAD 请求以验证文件是否存在
        HttpResponse response = null;
        try {
            response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute();
            // 未正常返回，无需执行其他判断
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                return;
            }
            // 4. 校验文件类型
            String contentType = response.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
                // 允许的图片类型
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                        ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
            // 5. 校验文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    // 限制文件大小为 5MB
                    final long TWO_MB = 5 * 1024 * 1024L;
                    ThrowUtils.throwIf(
                            contentLength > TWO_MB,
                            ErrorCode.PARAMS_ERROR,
                            "文件大小不能超过 2M");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误");
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void validatePicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR,"文件为空");
        //1.校验文件大小
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024L;
        ThrowUtils.throwIf(fileSize > 50 * ONE_M, ErrorCode.PARAMS_ERROR,"文件过大");
        //2.检验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        //允许上传的文件后缀
        final List<String> ALLOW_FORMAT_SUFFIX = Arrays.asList("jpg", "jpeg", "png", "webp","JPG", "JPEG", "PNG","WEBP");
        ThrowUtils.throwIf(!ALLOW_FORMAT_SUFFIX.contains(fileSuffix), ErrorCode.PARAMS_ERROR,"文件类型不支持");
    }

    public void deleteTempFile(File file) {
        if(file==null){
            return;
        }
        boolean delete = file.delete();
        if(!delete){
            log.info("删除临时文件失败，{}",file.getAbsolutePath());
        }
    }
}
