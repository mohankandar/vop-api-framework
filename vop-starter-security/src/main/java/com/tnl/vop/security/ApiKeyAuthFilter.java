package com.tnl.vop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final SecurityProperties props;

    public ApiKeyAuthFilter(SecurityProperties props) {
        this.props = props;
        setCheckForPrincipalChanges(false);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        if (!props.getApiKey().isEnabled()) return null;
        String apiKey = request.getHeader(props.getApiKey().getHeader());
        if (apiKey == null || apiKey.isBlank()) return null;
        if (!apiKey.equals(props.getApiKey().getValue())) return null; // reject mismatched key

        // Build principal from headers (best-effort)
        String appName = request.getHeader(props.getApiKey().getApplicationNameHeader());
        String appToken = request.getHeader(props.getApiKey().getAppTokenHeader());
        String firstName = request.getHeader(props.getApiKey().getFirstNameHeader());
        String lastName = request.getHeader(props.getApiKey().getLastNameHeader());
        String networkId = request.getHeader(props.getApiKey().getNetworkIdHeader());

        VopPrincipal principal = new VopPrincipal(
                d(appName), d(appToken), d(firstName), d(lastName), d(networkId),
                Set.of("ROLE_APIKEY"), Set.of(), java.util.Map.of()
        );

        return new PreAuthToken(principal);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) { return "N/A"; }

    private static String d(String s) { return s == null ? "" : s; }

    /** Simple Authentication wrapper */
    static class PreAuthToken extends AbstractAuthenticationToken {
        private final VopPrincipal principal;
        PreAuthToken(VopPrincipal principal) {
            super(Set.of(new SimpleGrantedAuthority("ROLE_APIKEY")));
            this.principal = principal;
            setAuthenticated(true);
        }
        @Override public Object getCredentials() { return "N/A"; }
        @Override public Object getPrincipal() { return principal; }
        @Override public String getName() { return principal.networkId(); }
    }
}
