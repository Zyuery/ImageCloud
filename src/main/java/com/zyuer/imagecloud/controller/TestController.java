package com.zyuer.imagecloud.controller;

import com.zyuer.imagecloud.common.BaseResponse;
import com.zyuer.imagecloud.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public BaseResponse test() {
        return ResultUtils.success("test");
    }
}
