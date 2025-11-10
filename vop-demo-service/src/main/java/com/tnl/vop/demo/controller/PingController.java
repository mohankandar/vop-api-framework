package com.tnl.vop.demo.controller;

import com.tnl.vop.core.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    @GetMapping("/api/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("pong");
    }
}
