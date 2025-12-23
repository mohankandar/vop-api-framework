package com.tnl.vop.reference.api;

import com.tnl.vop.reference.generated.api.PartnerApi;
import com.tnl.vop.reference.generated.model.PartnerEchoPayload;
import com.tnl.vop.reference.generated.model.PartnerEchoResponse;
import com.tnl.vop.reference.generated.model.PartnerStatusPayload;
import com.tnl.vop.reference.generated.model.PartnerStatusResponse;
import com.tnl.vop.core.api.ErrorDetail;
import com.tnl.vop.reference.service.PartnerEchoClient;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PartnerApiController implements PartnerApi {

  private final PartnerEchoClient echoClient;

  public PartnerApiController(PartnerEchoClient echoClient) {
    this.echoClient = echoClient;
  }

  @Override
  @PreAuthorize("hasRole('API')")
  public ResponseEntity<PartnerStatusResponse> getPartnerStatus() {
    var auth = SecurityContextHolder.getContext().getAuthentication();

    var payload = new PartnerStatusPayload()
        .clientId(String.valueOf(auth.getPrincipal()))
        .authorities(auth.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));

    return ResponseEntity.ok(okStatus(payload));
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<PartnerEchoResponse> partnerEcho() {
    var resp = echoClient.callEcho();
    int status = (resp != null ? resp.getStatusCode().value() : 0);
    String body = (resp != null ? resp.getBody() : null);

    var payload = new PartnerEchoPayload()
        .upstreamStatus(status)
        .body(body);

    return ResponseEntity.ok(okEcho(payload));
  }

  private static PartnerStatusResponse okStatus(PartnerStatusPayload payload) {
    return new PartnerStatusResponse()
        .status("ok")
        .data(payload)
        .error(null)
        .correlationId(MDC.get("correlationId"))
        .timestamp(OffsetDateTime.now());
  }

  private static PartnerEchoResponse okEcho(PartnerEchoPayload payload) {
    return new PartnerEchoResponse()
        .status("ok")
        .data(payload)
        .error(null)
        .correlationId(MDC.get("correlationId"))
        .timestamp(OffsetDateTime.now());
  }

  @SuppressWarnings("unused")
  private static PartnerEchoResponse errorEcho(String code, String message) {
    return new PartnerEchoResponse()
        .status("error")
        .data(null)
        .error(ErrorDetail.of(code, message))
        .correlationId(MDC.get("correlationId"))
        .timestamp(OffsetDateTime.now());
  }
}
