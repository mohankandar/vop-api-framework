package com.tnl.vop.platform.http;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Global HTTP client configuration for VOP.
 * <p>
 * Provides opinionated defaults for connect/read timeouts and allows
 * optional per-client overrides.
 */
@ConfigurationProperties(prefix = "vop.http")
public class VopHttpProperties {

  /**
   * Global timeout settings applied to all HTTP clients by default.
   */
  private TimeoutProperties timeout = new TimeoutProperties();

  /**
   * Optional per-client overrides. The key is a logical client name,
   * e.g. "billing-service" or "partner-api".
   *
   * For Feign, we recommend using the Feign client name here.
   */
  private Map<String, ClientProperties> clients = new HashMap<>();

  public TimeoutProperties getTimeout() {
    return timeout;
  }

  public void setTimeout(TimeoutProperties timeout) {
    this.timeout = timeout;
  }

  public Map<String, ClientProperties> getClients() {
    return clients;
  }

  public void setClients(Map<String, ClientProperties> clients) {
    this.clients = clients;
  }

  /**
   * Resolve the effective timeout configuration for a given client name,
   * falling back to the global defaults when no override is defined.
   */
  public TimeoutProperties resolveTimeoutForClient(String clientName) {
    ClientProperties clientProps = clients.get(clientName);
    if (clientProps == null || clientProps.getTimeout() == null) {
      return timeout;
    }
    TimeoutProperties override = clientProps.getTimeout();

    TimeoutProperties merged = new TimeoutProperties();
    merged.setConnectMs(
        override.getConnectMs() != null ? override.getConnectMs() : timeout.getConnectMs());
    merged.setReadMs(
        override.getReadMs() != null ? override.getReadMs() : timeout.getReadMs());

    return merged;
  }

  public static class ClientProperties {

    /**
     * Optional per-client timeout override.
     */
    private TimeoutProperties timeout;

    public TimeoutProperties getTimeout() {
      return timeout;
    }

    public void setTimeout(TimeoutProperties timeout) {
      this.timeout = timeout;
    }
  }

  /**
   * Timeout configuration in milliseconds.
   */
  public static class TimeoutProperties {

    /**
     * Connect timeout in milliseconds.
     * <p>
     * Default: 1000 ms.
     */
    private Integer connectMs = 1000;

    /**
     * Read/response timeout in milliseconds.
     * <p>
     * Default: 5000 ms.
     */
    private Integer readMs = 5000;

    public Integer getConnectMs() {
      return connectMs;
    }

    public void setConnectMs(Integer connectMs) {
      this.connectMs = connectMs;
    }

    public Integer getReadMs() {
      return readMs;
    }

    public void setReadMs(Integer readMs) {
      this.readMs = readMs;
    }
  }
}
