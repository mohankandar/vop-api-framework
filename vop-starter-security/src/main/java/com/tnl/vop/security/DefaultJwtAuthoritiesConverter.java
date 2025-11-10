package com.tnl.vop.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default converter: maps claims to authorities.
 * - "groups" or "roles" -> ROLE_*
 * - "scp" (array) or "scope" (space-delimited) -> SCOPE_*
 */
public class DefaultJwtAuthoritiesConverter implements JwtAuthoritiesConverter {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<String> roles = new LinkedHashSet<>();
        Map<String, Object> claims = jwt.getClaims();

        // groups / roles as ROLE_*
        roles.addAll(readStringArray(claims.get("groups")).stream()
                .map(r -> "ROLE_" + normalize(r)).toList());
        roles.addAll(readStringArray(claims.get("roles")).stream()
                .map(r -> "ROLE_" + normalize(r)).toList());

        // scopes as SCOPE_*
        roles.addAll(readScopes(claims).stream()
                .map(s -> "SCOPE_" + normalize(s)).toList());

        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toUnmodifiableSet());
    }

    private static List<String> readStringArray(Object claim) {
        if (claim == null) return List.of();
        if (claim instanceof Collection<?> col)
            return col.stream().map(Object::toString).toList();
        if (claim.getClass().isArray())
            return Arrays.stream((Object[]) claim).map(Object::toString).toList();
        return List.of(claim.toString());
    }

    private static List<String> readScopes(Map<String, Object> claims) {
        if (claims == null) return List.of();
        Object scp = claims.get("scp");
        if (scp instanceof Collection<?> col) return col.stream().map(Object::toString).toList();
        Object scope = claims.get("scope");
        if (scope instanceof String s) return Arrays.asList(s.split("\\s+"));
        return List.of();
    }

    private static String normalize(String s) {
        return s.trim().replaceAll("[^A-Za-z0-9:_\\-\\.]", "_").toUpperCase(Locale.ROOT);
    }
}
