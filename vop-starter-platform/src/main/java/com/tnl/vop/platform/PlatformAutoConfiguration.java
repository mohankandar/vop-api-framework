package com.tnl.vop.platform;

import com.tnl.vop.platform.logging.CorrelationIdFilter;
import com.tnl.vop.platform.logging.MdcUserEnricherFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@AutoConfiguration
public class PlatformAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter(
        @Value("${vop.logging.app-name:vop-demo-service}") String appName) {
        var bean = new FilterRegistrationBean<>(new CorrelationIdFilter(appName));
        // Run BEFORE Spring Security (-100) so all downstream logs have MDC
        bean.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER - 10); // e.g., -110
        bean.addUrlPatterns("/*");
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<MdcUserEnricherFilter> mdcUserEnricherFilter() {
        var bean = new FilterRegistrationBean<>(new MdcUserEnricherFilter());
        // Run AFTER Spring Security so Authentication is available
        bean.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER + 10); // e.g., -90
        bean.addUrlPatterns("/*");
        return bean;
    }

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

    @Bean(name = "vopMdcUserEnricherFilter")
    @ConditionalOnMissingBean(name = "vopMdcUserEnricherFilter")
    public OncePerRequestFilter vopMdcUserEnricherFilter() {
        return new MdcUserEnricherFilter();
    }
}
