package com.tnl.vop.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email String email
) {}