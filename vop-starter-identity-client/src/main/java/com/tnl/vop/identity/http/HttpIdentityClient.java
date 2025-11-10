package com.tnl.vop.identity.http;

import com.tnl.vop.identity.VopIdentity;
import com.tnl.vop.identity.VopIdentityClient;
import com.tnl.vop.identity.VopIdentityProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;

@Primary
public class HttpIdentityClient implements VopIdentityClient {

    private final RestTemplate restTemplate;       // used if WebClient not present
    private final WebClient webClient;             // used if present
    private final VopIdentityProperties props;

    public HttpIdentityClient(RestTemplateBuilder builder, @Nullable WebClient webClient, VopIdentityProperties props) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(props.getTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(props.getTimeoutMs()))
                .build();
        this.webClient = webClient;
        this.props = props;
    }

    @Override
    public Optional<VopIdentity> current() {
        // Not doing SecurityContext here; current() is best-effort in claims client.
        return Optional.empty();
    }

    @Override
    public Optional<VopIdentity> byNetworkId(String networkId, @Nullable String inboundBearer) {
        if (!StringUtils.hasText(props.getBaseUrl())) return Optional.empty();

        String url = props.getBaseUrl() + props.getPathTemplate().replace("{networkId}", networkId);
        String token = StringUtils.hasText(props.getBearer()) ? props.getBearer() : inboundBearer;

        try {
            if (webClient != null) {
                Map<String, Object> body = webClient.get()
                        .uri(url)
                        .headers(h -> setAuth(h, token))
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block(Duration.ofMillis(props.getTimeoutMs()));
                return Optional.ofNullable(mapToIdentity(body));
            } else {
                var headers = new org.springframework.http.HttpHeaders();
                setAuth(headers, token);
                var entity = new org.springframework.http.HttpEntity<>(headers);
                var resp = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, Map.class);
                return Optional.ofNullable(mapToIdentity(resp.getBody()));
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static void setAuth(HttpHeaders headers, @Nullable String token) {
        if (StringUtils.hasText(token)) headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    @SuppressWarnings("unchecked")
    private static VopIdentity mapToIdentity(Map<String, Object> m) {
        if (m == null) return null;
        String networkId = String.valueOf(m.getOrDefault("networkId", ""));
        String firstName = (String) m.get("firstName");
        String lastName = (String) m.get("lastName");
        String email = (String) m.get("email");
        List<String> roles = Optional.ofNullable((Collection<?>) m.get("roles"))
                .map(col -> col.stream().map(String::valueOf).toList())
                .orElse(List.of());
        return new VopIdentity(networkId, firstName, lastName, email, roles, m);
    }
}
