package com.tnl.vop.platform;

import com.tnl.vop.platform.filters.CorrelationIdFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@AutoConfiguration
public class PlatformAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {
        var bean = new FilterRegistrationBean<>(new CorrelationIdFilter());
        bean.setOrder(0); // first filter
        bean.addUrlPatterns("/*");
        return bean;
    }

    /**
     * Very lightweight request logging; apps can override/remove by defining their own bean.
     * We keep payload logging OFF; headers masked by downstream logging config.
     */
    @Bean
    @ConditionalOnMissingBean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        var f = new CommonsRequestLoggingFilter();
        f.setIncludeClientInfo(true);
        f.setIncludeQueryString(true);
        f.setIncludePayload(false);
        f.setIncludeHeaders(false);
        f.setMaxPayloadLength(0);
        return f;
    }
}
