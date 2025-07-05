package com.zyuer.imagecloud.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.zyuer.imagecloud.config.CosClientConfig;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.File;

/**
 * @author zyuer
 * @date 2024/7/29 10:11
 */
@Component
public class CosManager {
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key  唯一键(对于oss文件操作来说，这里的唯一键一般指的就是我们文件上传的路径————路径是唯一的，包含文件名)
     *             路径一般形式为 userId/picture/DateUtil.formatDate(new Date()),
     *                                        uuid,
     *                                        FileUtil.getSuffix(multipartFile.getOriginalFilename();)
     * @param file 文件
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     *            比较容易理解，直接传入我们的唯一键即可，唯一键一般指的就是我们文件上传的路径————路径是唯一的，包含文件名
     *            利用此方法中转对象下载时（即用户像服务端发请求，我们服务端向oss发请求，也就是调用此方法，将返回的CosObject转化为文件流写入response返回给用户）
     *            除此以外，用户还可以直接访问oss的文件下载地址，直接绕开我们的服务器中转，减少了服务器开销，但是这样会暴露我们oss服务的访问地址，不安全，所以我们一般采用这种方式
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 上传对象（附带图片信息）
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        // 构造处理参数
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }


}

