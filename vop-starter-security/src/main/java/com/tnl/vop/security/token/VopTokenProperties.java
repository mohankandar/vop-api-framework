package com.tnl.vop.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Extra token configuration (app-level defaults). */
@ConfigurationProperties(prefix = "vop.security.token")
public class VopTokenProperties {

    /** Extra claims merged into every token (reserved keys are ignored). */
    private Map<String, Object> claimsDefaults = new HashMap<>();

    /** Optional allowlist for request-time extras (reserved for future use). */
    private List<String> claimsAllowlist = new ArrayList<>();

    public Map<String, Object> getClaimsDefaults() { return claimsDefaults; }
    public void setClaimsDefaults(Map<String, Object> claimsDefaults) { this.claimsDefaults = claimsDefaults; }

    public List<String> getClaimsAllowlist() { return claimsAllowlist; }
    public void setClaimsAllowlist(List<String> claimsAllowlist) { this.claimsAllowlist = claimsAllowlist; }
}
