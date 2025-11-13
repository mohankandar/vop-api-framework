package com.tnl.vop.platform.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Collections;

public class LogHeadersFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(LogHeadersFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;

    // Example: include correlation already present in MDC
    String cid = MDC.get("correlationId");
    StringBuilder sb = new StringBuilder("Request ")
        .append(req.getMethod()).append(" ").append(req.getRequestURI()).append(" — headers:");

    Collections.list(req.getHeaderNames()).forEach(name ->
        sb.append("\n  ").append(name).append(": ").append(req.getHeader(name)));

    log.debug(sb.toString());
    chain.doFilter(request, response);
  }
}
