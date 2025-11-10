package com.tnl.vop.demo.controller;

import com.tnl.vop.core.api.ApiResponse;
import com.tnl.vop.demo.domain.Customer;
import com.tnl.vop.demo.dto.CustomerDto;
import com.tnl.vop.demo.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) { this.service = service; }

    @GetMapping
    public ApiResponse<Page<Customer>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String emailContains,
            @RequestParam(required = false) Instant createdAfter,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "createdDate,desc") String sort) {
        return ApiResponse.ok(service.search(name, emailContains, createdAfter, page, size, sort));
    }

    @PostMapping
    public ApiResponse<Customer> create(@RequestBody @Valid CustomerDto dto) {
        return ApiResponse.ok(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> get(@PathVariable UUID id) {
        return service.findById(id)
                .map(c -> ResponseEntity.ok(ApiResponse.ok(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ApiResponse<Customer> update(@PathVariable UUID id, @RequestBody @Valid CustomerDto dto) {
        return ApiResponse.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ApiResponse.ok(null);
    }
}
