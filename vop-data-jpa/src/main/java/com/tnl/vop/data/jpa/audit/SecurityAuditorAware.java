// vop-data-jpa/src/main/java/com/tnl/vop/data/jpa/audit/SecurityAuditorAware.java
package com.tnl.vop.data.jpa.audit;

import org.springframework.data.domain.AuditorAware;

import java.security.Principal;
import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Try Spring Security if available
        try {
            Class<?> ctxHolderCls = Class.forName("org.springframework.security.core.context.SecurityContextHolder");
            Object ctx = ctxHolderCls.getMethod("getContext").invoke(null);
            if (ctx != null) {
                Object auth = ctx.getClass().getMethod("getAuthentication").invoke(ctx);
                if (auth != null) {
                    Object principal = auth.getClass().getMethod("getPrincipal").invoke(auth);
                    String user = extractUser(principal);
                    if (user != null && !user.isBlank()) return Optional.of(user);
                }
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // spring-security not on classpath – fall through to default
        } catch (Exception ignore) {
            // any reflection issue – fall through to default
        }
        return Optional.of("system");
    }

    private String extractUser(Object principal) {
        if (principal == null) return "";
        // If it’s our VopPrincipal (without compile-time dep)
        try {
            var m = principal.getClass().getMethod("networkId");
            Object v = m.invoke(principal);
            if (v != null) return String.valueOf(v);
        } catch (Exception ignore) { /* not our type */ }

        // If it implements java.security.Principal
        if (principal instanceof Principal p) return p.getName();

        // Fallback
        return String.valueOf(principal);
    }
}
