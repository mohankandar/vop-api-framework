package com.tnl.vop.demo.controller;

import com.tnl.vop.core.api.ApiResponse;
import com.tnl.vop.core.api.ErrorDetail;
import com.tnl.vop.identity.VopIdentity;
import com.tnl.vop.identity.VopIdentityClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

    private final VopIdentityClient identityClient;

    public MeController(VopIdentityClient identityClient) {
        this.identityClient = identityClient;
    }

    @GetMapping("/api/me")
    public ResponseEntity<ApiResponse<VopIdentity>> me() {
        return identityClient.current()
                .map(i -> ResponseEntity.ok(ApiResponse.ok(i)))
                .orElseGet(() -> ResponseEntity.status(401).body(ApiResponse.error(ErrorDetail.of("UNAUTHENTICATED", "No user context", null))));
    }
}
