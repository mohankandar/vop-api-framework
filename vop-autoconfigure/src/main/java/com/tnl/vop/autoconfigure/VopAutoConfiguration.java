package com.tnl.vop.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

/**
 * Minimal, always-safe configuration that other starters can rely on.
 * - Provides a system {@link Clock} bean
 * - Binds VopProperties (app name, logging, feature flags)
 */
@AutoConfiguration
@EnableConfigurationProperties(VopProperties.class)
public class VopAutoConfiguration {

    @Bean
    public Clock vopSystemClock() {
        return Clock.systemUTC();
    }
}
