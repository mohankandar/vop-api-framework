package com.tnl.vop.security.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Framework-provided endpoint for issuing short-lived JWTs.
 * Always visible in Swagger. Behavior controlled by vop.security.token-endpoint.enabled.
 */
@RestController
@RequestMapping(path = "/api/token", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(
    name = "Token",
    description = "Generate a short-lived JWT for manual testing",
    extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "0"))
)
public class TokenEndpointController {

  private static final Set<String> RESERVED = Set.of(
      "iss","sub","iat","nbf","exp","jti","aud","applicationID","userID","roles"
  );

  private final TokenEndpointProperties props;
  private final VopTokenProperties tokenProps;   // YAML extras
  private final SecretKey key;                   // HS256 key

  public TokenEndpointController(TokenEndpointProperties props, VopTokenProperties tokenProps) {
    this.props = props;
    this.tokenProps = tokenProps;

    byte[] bytes = props.getHmacSecret().getBytes(StandardCharsets.UTF_8);
    if (bytes.length < 32) {
      throw new IllegalStateException("vop.security.token-endpoint.hmac-secret must be >= 32 bytes");
    }
    this.key = Keys.hmacShaKeyFor(bytes);
  }

  @Operation(
      summary = "Generate a short-lived JWT for manual testing",
      description = "Always documented. Enabled/disabled via vop.security.token-endpoint.enabled."
  )
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> issue(@RequestBody TokenRequest req, HttpServletRequest http) {
    if (!props.isEnabled()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ErrorBody("TOKEN_ISSUANCE_DISABLED", "Token issuance is disabled in this environment."));
    }

    var now = Instant.now();
    var exp = now.plusSeconds(props.getExpirySeconds());

    var roles = (req.roles == null || req.roles.isEmpty()) ? List.of("ROLE_USER") : req.roles;
    String subject = StringUtils.hasText(req.sub) ? req.sub : "user";

    var jwtBuilder = Jwts.builder()
        .setIssuer(props.getIssuer())
        .setSubject(subject)
        .setIssuedAt(Date.from(now))
        .setNotBefore(Date.from(now))
        .setExpiration(Date.from(exp))
        .setId(UUID.randomUUID().toString())
        .claim("applicationID", props.getIssuer())
        .claim("userID", subject)
        .claim("roles", roles);

    if (StringUtils.hasText(req.aud)) {
      jwtBuilder.setAudience(req.aud);
    }
    if (StringUtils.hasText(props.getKid())) {
      jwtBuilder.setHeaderParam("kid", props.getKid());
    }

    // Merge YAML-configured extra claims (non-reserved only)
    if (tokenProps != null && tokenProps.getClaimsDefaults() != null) {
      for (Map.Entry<String, Object> e : tokenProps.getClaimsDefaults().entrySet()) {
        String k = e.getKey();
        if (StringUtils.hasText(k) && !RESERVED.contains(k)) {
          jwtBuilder.claim(k, e.getValue());
        }
      }
    }

    String token = jwtBuilder.signWith(key, SignatureAlgorithm.HS256).compact();

    return ResponseEntity.ok()
        .header(HttpHeaders.CACHE_CONTROL, "no-store")
        .header("Pragma", "no-cache")
        .body(new TokenResponse(token, exp.toString(), props.getIssuer()));
  }

  public static class TokenRequest {
    public String sub;
    public String aud;
    public List<String> roles;
  }

  public static class TokenResponse {
    public final String token;
    public final String expiresAt;
    public final String issuer;
    public TokenResponse(@JsonProperty("token") String token,
        @JsonProperty("expiresAt") String expiresAt,
        @JsonProperty("issuer") String issuer) {
      this.token = token;
      this.expiresAt = expiresAt;
      this.issuer = issuer;
    }
  }

  public static class ErrorBody {
    public final String code;
    public final String message;
    public ErrorBody(String code, String message) {
      this.code = code; this.message = message;
    }
  }
}
