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

    private static final String MDC_CORRELATION_ID = "correlationId";
    private static final String MDC_APP = "app";

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

        // Save existing MDC values (important for tracing keys set by other filters)
        final String prevCid = MDC.get(MDC_CORRELATION_ID);
        final String prevApp = MDC.get(MDC_APP);

        MDC.put(MDC_CORRELATION_ID, cid);
        MDC.put(MDC_APP, appName);

        try {
            chain.doFilter(req, res);
        } finally {
            // Restore only what we changed (do NOT MDC.clear())
            if (prevCid == null) MDC.remove(MDC_CORRELATION_ID);
            else MDC.put(MDC_CORRELATION_ID, prevCid);

            if (prevApp == null) MDC.remove(MDC_APP);
            else MDC.put(MDC_APP, prevApp);
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
