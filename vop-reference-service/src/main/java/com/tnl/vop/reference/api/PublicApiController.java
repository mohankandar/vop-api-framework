package com.tnl.vop.reference.api;

import com.tnl.vop.reference.generated.api.PublicApi;
import com.tnl.vop.reference.generated.model.PingPayload;
import com.tnl.vop.reference.generated.model.PingResponse;
import com.tnl.vop.core.api.ErrorDetail;
import java.time.OffsetDateTime;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicApiController implements PublicApi {

  @Override
  public ResponseEntity<PingResponse> ping() {
    var payload = new PingPayload()
        .message("pong")
        .service("vop-reference-service");
    return ResponseEntity.ok(ok(payload));
  }

  private static PingResponse ok(PingPayload payload) {
    return new PingResponse()
        .status("ok")
        .data(payload)
        .error(null)
        .correlationId(MDC.get("correlationId"))
        .timestamp(OffsetDateTime.now());
  }

  @SuppressWarnings("unused")
  private static PingResponse error(String code, String message) {
    return new PingResponse()
        .status("error")
        .data(null)
        .error(ErrorDetail.of(code, message))
        .correlationId(MDC.get("correlationId"))
        .timestamp(OffsetDateTime.now());
  }
}
