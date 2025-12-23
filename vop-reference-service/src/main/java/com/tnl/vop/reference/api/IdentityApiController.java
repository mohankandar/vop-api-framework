package com.tnl.vop.reference.api;

import com.tnl.vop.reference.generated.api.IdentityApi;
import com.tnl.vop.reference.generated.model.MePayload;
import com.tnl.vop.reference.generated.model.MeResponse;
import com.tnl.vop.core.api.ErrorDetail;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdentityApiController implements IdentityApi {

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<MeResponse> getMe() {
    var auth = SecurityContextHolder.getContext().getAuthentication();

    var payload = new MePayload()
        .authType(auth instanceof JwtAuthenticationToken ? "JWT" : "UNKNOWN")
        .principal(String.valueOf(auth.getPrincipal()))
        .authorities(auth.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()))
        .claims(new LinkedHashMap<>());

    if (auth instanceof JwtAuthenticationToken jwt) {
      payload.setClaims(new LinkedHashMap<>(jwt.getToken().getClaims()));
    }

    return ResponseEntity.ok(ok(payload));
  }

  private static MeResponse ok(MePayload payload) {
    return new MeResponse()
        .status("ok")
        .data(payload)
        .error(null)
        .correlationId(MDC.get("correlationId"))
        .timestamp(OffsetDateTime.now());
  }

  @SuppressWarnings("unused")
  private static MeResponse error(String code, String message) {
    return new MeResponse()
        .status("error")
        .data(null)
        .error(ErrorDetail.of(code, message))
        .correlationId(MDC.get("correlationId"))
        .timestamp(OffsetDateTime.now());
  }
}
