package com.tnl.vop.security.config;

public final class VopDefaultSecurityPaths {

  private VopDefaultSecurityPaths() {
  }

  public static final String[] DEFAULT_PERMIT_ALL = {
      "/swagger-ui/**",
      "/swagger-ui.html",
      "/v3/api-docs/**",
      "/actuator/**",
      "/error",
      "/webjars/**",
      "/favicon.ico"
  };
}
