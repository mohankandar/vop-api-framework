package com.tnl.vop.demo.controller;

import com.tnl.vop.core.api.ApiResponse;
import com.tnl.vop.core.api.ErrorCode;
import com.tnl.vop.core.api.ErrorDetail;
import com.tnl.vop.identity.VopIdentity;
import com.tnl.vop.identity.VopIdentityClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Returns the current identity wrapped in the standard VOP ApiResponse envelope.
 * If no identity is present in the context, returns 401 with ErrorCode.UNAUTHORIZED.
 */
@RestController
public class MeController {

    private final VopIdentityClient identityClient;

    public MeController(VopIdentityClient identityClient) {
        this.identityClient = identityClient;
    }

    @GetMapping("/api/me")
    public ResponseEntity<ApiResponse<VopIdentity>> me() {
        return identityClient.current()
            .map(identity -> ResponseEntity.ok(ApiResponse.ok(identity)))
            .orElseGet(() -> {
                ErrorDetail error = ErrorDetail.of(
                    ErrorCode.UNAUTHORIZED,
                    "No authenticated user is available in the current context.",
                    null
                );
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(error));
            });
    }
}
