package com.tnl.vop.platform.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "vopAsyncExecutor")
  public Executor vopAsyncExecutor() {
    ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
    ex.setThreadNamePrefix("vop-async-");
    ex.setCorePoolSize(4);
    ex.setMaxPoolSize(16);
    ex.setTaskDecorator(new MdcTaskDecorator());
    ex.initialize();
    return ex;
  }
}
