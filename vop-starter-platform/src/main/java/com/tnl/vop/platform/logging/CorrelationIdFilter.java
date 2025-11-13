package com.tnl.vop.platform.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class CorrelationIdFilter extends OncePerRequestFilter {
    public static final String HDR = "X-Correlation-Id";

    private final String appName;

    public CorrelationIdFilter(String appName) {
        this.appName = appName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws ServletException, IOException {
        String cid = req.getHeader(HDR);
        if (cid == null || cid.isBlank()) cid = UUID.randomUUID().toString();
        res.setHeader(HDR, cid);

        MDC.put("correlationId", cid);
        MDC.put("app", appName);

        try {
            chain.doFilter(req, res);
        } finally {
            MDC.clear();
        }
    }
    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false; // run on ERROR dispatch too
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false; // (optional) run on ASYNC dispatches as well
    }
}
