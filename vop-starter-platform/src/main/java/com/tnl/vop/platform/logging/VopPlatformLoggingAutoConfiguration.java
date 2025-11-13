package com.tnl.vop.platform.logging;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;

@AutoConfiguration
public class VopPlatformLoggingAutoConfiguration {

  @Bean(name = "vopMdcUserEnricherFilter")
  @ConditionalOnMissingBean(name = "vopMdcUserEnricherFilter")
  public OncePerRequestFilter vopMdcUserEnricherFilter() {
    return new MdcUserEnricherFilter();
  }
}