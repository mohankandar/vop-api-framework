package com.tnl.vop.reference.api;

import com.tnl.vop.reference.generated.api.ContractsApi;
import com.tnl.vop.reference.generated.model.ContractPayload;
import com.tnl.vop.reference.generated.model.ContractResponse;
import com.tnl.vop.reference.generated.model.CreateContractRequest;
import com.tnl.vop.core.api.ErrorDetail;
import com.tnl.vop.reference.service.ContractService;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContractsApiController implements ContractsApi {

  private final ContractService svc;

  public ContractsApiController(ContractService svc) {
    this.svc = svc;
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<ContractResponse> createContract(@Valid CreateContractRequest req) {
    try {
      var rec = svc.create(req.getExternalRef(), req.getTitle());
      return ResponseEntity.ok(ok(toPayload(rec)));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(error("BAD_REQUEST", e.getMessage()));
    }
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<ContractResponse> getContractById(UUID id) {
    var rec = svc.get(id);
    if (rec == null) {
      return ResponseEntity.status(404).body(error("NOT_FOUND", "Contract not found: " + id));
    }
    return ResponseEntity.ok(ok(toPayload(rec)));
  }

  private static ContractPayload toPayload(com.tnl.vop.reference.domain.ContractRecord rec) {
    return new ContractPayload()
        .id(rec.getId())
        .externalRef(rec.getExternalRef())
        .title(rec.getTitle())
        .createdAt(OffsetDateTime.ofInstant(rec.getCreatedAt(), ZoneOffset.UTC));
  }

  private static ContractResponse ok(ContractPayload payload) {
    return new ContractResponse()
        .status("ok")
        .data(payload)
        .error(null)
        .correlationId(MDC.get("correlationId"))
        .timestamp(OffsetDateTime.now());
  }

  private static ContractResponse error(String code, String message) {
    return new ContractResponse()
        .status("error")
        .data(null)
        .error(ErrorDetail.of(code, message))
        .correlationId(MDC.get("correlationId"))
        .timestamp(OffsetDateTime.now());
  }
}
