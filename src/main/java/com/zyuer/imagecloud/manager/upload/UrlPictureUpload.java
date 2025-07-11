package com.zyuer.imagecloud.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.zyuer.imagecloud.exception.BusinessException;
import com.zyuer.imagecloud.exception.ErrorCode;
import com.zyuer.imagecloud.exception.ThrowUtils;
import org.springframework.stereotype.Component;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Component
public class UrlPictureUpload extends PictureUploadTemplate {
    @Override
    protected void validPicture(Object inputSource) {
        String fileUrl = (String) inputSource;
        ThrowUtils.throwIf(
                StrUtil.isBlank(fileUrl),
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
                    final long FIVE_MB = 5 * 1024 * 1024L;
                    ThrowUtils.throwIf(
                            contentLength > FIVE_MB,
                            ErrorCode.PARAMS_ERROR,
                            "文件大小不能超过 5M");
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

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        String fileUrl = (String) inputSource;
        // 下载文件到临时目录
        HttpUtil.downloadFile(fileUrl, file);
    }

    //图片没有后缀
    @Override
    protected String getOriginFilename(Object inputSource) {
        String fileUrl = (String) inputSource;
        // 从 URL 中提取文件名
        // return FileUtil.mainName(fileUrl);
        if (!hasValidSuffix(fileUrl)) {
        // 如果没有有效后缀，添加.png后缀
            fileUrl = fileUrl + ".png";
        }
        // 返回处理后的文件名
        return fileUrl;
        // return FileUtil.getName(fileUrl);
    }


    private boolean hasValidSuffix(String filename) {
        // 获取文件后缀
        String suffix = FileUtil.getSuffix(filename);
        // 检查后缀是否为空或是否是常见的图片后缀
        return StrUtil.isNotBlank(suffix) && isCommonImageSuffix(suffix);
    }

    private boolean isCommonImageSuffix(String suffix) {
        // 定义常见的图片后缀
        String[] commonSuffixes = {"png", "jpg", "jpeg", "gif", "bmp", "webp"};
        for (String commonSuffix : commonSuffixes) {
            if (StrUtil.equalsIgnoreCase(suffix, commonSuffix)) {
                return true;
            }
        }
        return false;
    }
}
