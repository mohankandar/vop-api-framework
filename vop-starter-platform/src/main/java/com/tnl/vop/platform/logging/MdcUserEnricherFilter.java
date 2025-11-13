package com.tnl.vop.platform.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class MdcUserEnricherFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest req,
      HttpServletResponse res,
      FilterChain chain)
      throws ServletException, IOException {

    Authentication auth = SecurityContextHolder.getContext() != null
        ? SecurityContextHolder.getContext().getAuthentication()
        : null;

    if (auth != null && auth.isAuthenticated()) {
      String user = null;

      // 1) JWT-based auth (your Bearer tokens)
      if (auth instanceof JwtAuthenticationToken jwtAuth) {
        Jwt jwt = (Jwt) jwtAuth.getPrincipal();

        // Prefer custom claims, then subject, then common name-ish claims
        user = jwt.getClaimAsString("userID");
        if (user == null) user = jwt.getClaimAsString("userId");
        if (user == null) user = jwt.getSubject();
        if (user == null) user = jwt.getClaimAsString("preferred_username");
        if (user == null) user = jwt.getClaimAsString("name");
      }

      // 2) Fallback: “normal” auth (API key, username/password, etc.)
      if (user == null && auth.getName() != null) {
        user = auth.getName();
      }

      if (user != null) {
        MDC.put("user", user);
      }
    }

    try {
      chain.doFilter(req, res);
    } finally {
      // Do NOT clear here; CorrelationIdFilter will do request-scope cleanup
    }
  }
}
