package com.tnl.vop.demo.controller;

import com.tnl.vop.core.api.ApiResponse;
import com.tnl.vop.demo.service.DownstreamEchoClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DownstreamController {
    private final DownstreamEchoClient client;
    public DownstreamController(DownstreamEchoClient client) { this.client = client; }

    @GetMapping("/api/downstream/ping")
    public ApiResponse<Object> downstream() {
        return ApiResponse.ok(client.callEcho());
    }
}
