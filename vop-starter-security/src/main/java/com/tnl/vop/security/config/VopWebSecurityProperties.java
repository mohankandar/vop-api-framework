package com.tnl.vop.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * URL-level web security settings. Apps can add to 'permitPaths' in application.yml.
 *
 * Example:
 * vop.security.web.permit-paths:
 *   - /public/**
 *   - /files/download/**
 */
@ConfigurationProperties(prefix = "vop.security.web")
public class VopWebSecurityProperties {

    /**
     * Additional application-specific paths to permit without auth,
     * merged with VopDefaultSecurityPaths.DEFAULT_PERMIT_ALL.
     */
    private List<String> permitPaths = new ArrayList<>();

    public List<String> getPermitPaths() {
        return permitPaths;
    }

    public void setPermitPaths(List<String> permitPaths) {
        this.permitPaths = permitPaths;
    }
}
