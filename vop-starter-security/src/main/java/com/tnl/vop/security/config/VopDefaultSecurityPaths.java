package com.tnl.vop.security.config;

import java.util.Locale;

public final class VopDefaultSecurityPaths {

    private VopDefaultSecurityPaths() {
    }

    public static final String[] DEFAULT_PERMIT_ALL = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/actuator/**",
            "/api/ping",
            "/error",
            "/webjars/**",
            "/favicon.ico"
    };
}
