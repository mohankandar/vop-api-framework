package com.tnl.vop.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Default converter: maps claims to authorities. - "groups" or "roles" -> ROLE_* - "scp" (array) or
 * "scope" (space-delimited) -> SCOPE_*
 */
public class DefaultJwtAuthoritiesConverter implements JwtAuthoritiesConverter {

  private final SecurityProperties props;

  public DefaultJwtAuthoritiesConverter(SecurityProperties props) {
    this.props = props;
  }

  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    Map<String, Object> claims = jwt.getClaims();
    var c = props.getClaims();

    List<String> roleClaims = new ArrayList<>();
    roleClaims.addAll(readStringArray(claims.get("groups"))); // keep common defaults
    roleClaims.addAll(readStringArray(claims.get("roles")));
    // also honor configured role claim
    roleClaims.addAll(readStringArray(claims.get(c.getRoles())));

    List<String> scopeClaims = new ArrayList<>();
    scopeClaims.addAll(readScopes(claims)); // scope/scp defaults
    // also honor configured scope claim (string or array)
    scopeClaims.addAll(readStringArray(claims.get(c.getScopes())));

    Set<String> out = new LinkedHashSet<>();
    roleClaims.forEach(r -> out.add("ROLE_" + normalize(r)));
    scopeClaims.forEach(s -> out.add("SCOPE_" + normalize(s)));
    return out.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toUnmodifiableSet());
  }

  private static List<String> readStringArray(Object claim) {
      if (claim == null) {
          return List.of();
      }
      if (claim instanceof Collection<?> col) {
          return col.stream().map(Object::toString).toList();
      }
      if (claim.getClass().isArray()) {
          return Arrays.stream((Object[]) claim).map(Object::toString).toList();
      }
    return List.of(claim.toString());
  }

  private static List<String> readScopes(Map<String, Object> claims) {
      if (claims == null) {
          return List.of();
      }
    Object scp = claims.get("scp");
      if (scp instanceof Collection<?> col) {
          return col.stream().map(Object::toString).toList();
      }
    Object scope = claims.get("scope");
      if (scope instanceof String s) {
          return Arrays.asList(s.split("\\s+"));
      }
    return List.of();
  }

  private static String normalize(String s) {
    return s.trim().replaceAll("[^A-Za-z0-9:_\\-\\.]", "_").toUpperCase(Locale.ROOT);
  }
}
