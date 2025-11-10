package com.tnl.vop.data.redis;

import com.tnl.vop.data.VopDataProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Starts an embedded Redis server when:
 *  - vop.data.redis.embedded.enabled=true
 *  - embedded-redis lib is on the classpath
 *  - (optional) active profile contains "local"
 */
@AutoConfiguration
@ConditionalOnClass(name = "redis.embedded.RedisServer")
@ConditionalOnProperty(prefix = "vop.data.redis.embedded", name = "enabled", havingValue = "true")
public class EmbeddedRedisAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedRedisAutoConfiguration.class);

    @Bean
    public SmartLifecycle embeddedRedisLifecycle(VopDataProperties props, Environment env) {
        return new SmartLifecycle() {
            private volatile boolean running = false;
            private redis.embedded.RedisServer server;

            @Override
            public void start() {
                if (running) return;

                boolean isLocalOk = true;
                if (props.getRedisEmbedded().isOnlyWhenLocalProfile()) {
                    var profiles = String.join(",", env.getActiveProfiles());
                    isLocalOk = profiles.contains("local");
                }
                if (!isLocalOk) {
                    log.info("Embedded Redis: skipped (not in 'local' profile).");
                    return;
                }

                int port = props.getRedisEmbedded().getPort();
                try {
                    server = new redis.embedded.RedisServer(port);
                    server.start();
                    running = true;
                    log.info("Embedded Redis: started on port {}", port);
                } catch (Exception e) {
                    log.warn("Embedded Redis failed to start on port {}: {}", port, e.toString());
                }
            }

            @Override public void stop() {
                if (server != null) {
                    try { server.stop(); } catch (Exception ignored) {}
                }
                running = false;
                log.info("Embedded Redis: stopped.");
            }
            @Override public boolean isRunning() { return running; }
            @Override public int getPhase() { return Integer.MIN_VALUE; } // start early
            @Override public boolean isAutoStartup() { return true; }
            @Override public void stop(Runnable callback) { stop(); callback.run(); }
        };
    }
}
