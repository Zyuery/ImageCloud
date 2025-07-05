package com.zyuer.imagecloud.controller;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import com.zyuer.imagecloud.annotation.AuthCheck;
import com.zyuer.imagecloud.common.UserConstant;
import com.zyuer.imagecloud.domain.vo.result.BaseResponse;
import com.zyuer.imagecloud.domain.vo.result.ResultUtils;
import com.zyuer.imagecloud.exception.BusinessException;
import com.zyuer.imagecloud.exception.ErrorCode;
import com.zyuer.imagecloud.manager.CosManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
public class FileController {

    @Resource
    private CosManager cosManager;

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestParam MultipartFile  mutipartFile) {
        //文件目录
        String fileName = mutipartFile.getOriginalFilename();
        String filePath = String.format("/test/%s", fileName);
        File file =null;
        try{
            file = File.createTempFile(filePath,null);
            mutipartFile.transferTo(file);
            cosManager.putObject(filePath,file);
            return  ResultUtils.success(filePath);
        } catch (Exception e) {
            log.info("文件上传失败"+filePath,e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"上传失败");
        }finally {
            if(file!=null){
                boolean delete = file.delete();
                if(!delete){
                    log.info("删除临时文件失败"+filePath);
                }
            }
        }
    }

    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }

}
