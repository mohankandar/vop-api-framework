package com.tnl.vop.platform.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingFilterConfig {

  /** Correlation MUST run first (lower number = earlier). */
  @Bean
  public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter(
      @Value("${vop.logging.app-name:vop-demo-service}") String appName) {
    FilterRegistrationBean<CorrelationIdFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(new CorrelationIdFilter(appName));
    reg.setOrder(Integer.MIN_VALUE); // earlier than security & any other filters
    reg.addUrlPatterns("/*");
    return reg;
  }

  /** If you use a header-logging filter, run it AFTER correlation is set. */
  @Bean
  public FilterRegistrationBean<LogHeadersFilter> logHeadersFilter() {
    FilterRegistrationBean<LogHeadersFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(new LogHeadersFilter());
    reg.setOrder(Integer.MIN_VALUE + 100); // still early, but after correlation
    reg.addUrlPatterns("/*");
    return reg;
  }
}
