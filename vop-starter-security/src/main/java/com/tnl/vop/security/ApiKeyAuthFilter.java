package com.tnl.vop.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.StringUtils;

public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

  private final SecurityProperties props;

  public ApiKeyAuthFilter(SecurityProperties props) {
    this.props = props;
    setCheckForPrincipalChanges(false);
    setContinueFilterChainOnUnsuccessfulAuthentication(true);
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    // If a Bearer JWT is present, let the Resource Server handle it (JWT > API key).
    String authz = request.getHeader("Authorization");
    if (authz != null && authz.startsWith("Bearer ")) {
      return null;
    }

    if (!props.getApiKey().isEnabled()) {
      return null;
    }

    String headerName = props.getApiKey().getHeader();
    String presented = request.getHeader(headerName);
    if (!StringUtils.hasText(presented)) {
      return null;
    }

    String expected = props.getApiKey().getValue();
    if (!StringUtils.hasText(expected)) {
      return null;
    }

    if (expected.equals(presented)) {
      // Any non-null principal marks the request as authenticated by API key.
      return "api-key";
    }
    return null;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }
}
