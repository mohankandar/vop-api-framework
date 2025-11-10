package com.tnl.vop.data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vop.data")
public class VopDataProperties {

    private final RedisEmbedded redisEmbedded = new RedisEmbedded();

    public RedisEmbedded getRedisEmbedded() { return redisEmbedded; }

    public static class RedisEmbedded {
        /** Start an embedded Redis (for local/dev only). */
        private boolean enabled = false;
        /** Port to bind embedded Redis to. */
        private int port = 6379;
        /** Auto-start only when active profile contains "local". */
        private boolean onlyWhenLocalProfile = true;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        public boolean isOnlyWhenLocalProfile() { return onlyWhenLocalProfile; }
        public void setOnlyWhenLocalProfile(boolean onlyWhenLocalProfile) { this.onlyWhenLocalProfile = onlyWhenLocalProfile; }
    }
}
