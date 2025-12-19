package com.tnl.vop.security;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
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

    var apiKey = props.getApiKey();
    if (apiKey == null || !apiKey.isEnabled()) {
      return null;
    }

    String headerName = StringUtils.hasText(apiKey.getHeader()) ? apiKey.getHeader() : "X-API-Key";
    String presented = request.getHeader(headerName);
    if (!StringUtils.hasText(presented)) {
      return null;
    }

    // Preferred: multi-client keys
    Map<String, SecurityProperties.ApiKey.Client> clients = apiKey.getClients();
    if (clients != null && !clients.isEmpty()) {
      for (var entry : clients.entrySet()) {
        String clientId = entry.getKey();
        List<String> keys = (entry.getValue() != null ? entry.getValue().getKeys() : null);
        if (keys == null) continue;

        for (String expected : keys) {
          if (secureEquals(expected, presented)) {
            // Identity is the clientId
            return clientId;
          }
        }
      }
      return null;
    }

    // Backward compatibility: single value
    String expected = apiKey.getValue();
    if (!StringUtils.hasText(expected)) {
      return null;
    }
    return secureEquals(expected, presented) ? "default" : null;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

  static boolean secureEquals(String expected, String presented) {
    if (!StringUtils.hasText(expected) || !StringUtils.hasText(presented)) return false;
    return MessageDigest.isEqual(
        expected.getBytes(StandardCharsets.UTF_8),
        presented.getBytes(StandardCharsets.UTF_8)
    );
  }
}
