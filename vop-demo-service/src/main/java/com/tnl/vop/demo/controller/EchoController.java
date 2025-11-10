package com.tnl.vop.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EchoController {
    @GetMapping("/internal/echo")
    public Map<String, String> echo(@RequestHeader HttpHeaders headers) {
        return Map.of(
                "authorization", String.valueOf(headers.getFirst(HttpHeaders.AUTHORIZATION)),
                "x-api-key", String.valueOf(headers.getFirst("X-API-Key"))
        );
    }
}
