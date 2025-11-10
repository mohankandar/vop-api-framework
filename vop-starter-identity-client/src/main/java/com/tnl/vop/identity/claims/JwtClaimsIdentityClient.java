package com.tnl.vop.identity.claims;

import com.tnl.vop.identity.VopIdentity;
import com.tnl.vop.identity.VopIdentityClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;
import java.util.stream.Collectors;

public class JwtClaimsIdentityClient implements VopIdentityClient {

    @Override
    public Optional<VopIdentity> current() {
        var ctx = SecurityContextHolder.getContext();
        if (ctx == null) return Optional.empty();
        return fromAuthentication(ctx.getAuthentication());
    }

    @Override
    public Optional<VopIdentity> byNetworkId(String networkId, String bearerToken) {
        // claims-only client cannot look up arbitrary networkId
        return Optional.empty();
    }

    private Optional<VopIdentity> fromAuthentication(Authentication auth) {
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            Map<String,Object> claims = jwt.getClaims();

            String networkId = firstNonBlank(
                    claimAsString(claims,"networkId"),
                    claimAsString(claims,"preferred_username"),
                    claimAsString(claims,"uid"),
                    auth.getName());

            String firstName = claimAsString(claims,"given_name");
            String lastName  = claimAsString(claims,"family_name");
            String email     = claimAsString(claims,"email");

            List<String> roles = extractRoles(claims, jwtAuth.getAuthorities());

            return Optional.of(new VopIdentity(networkId, firstName, lastName, email, roles, claims));
        }
        return Optional.empty();
    }

    private static String claimAsString(Map<String,Object> claims, String key) {
        Object v = claims.get(key);
        return v == null ? null : String.valueOf(v);
    }

    private static List<String> extractRoles(Map<String,Object> claims, Collection<? extends GrantedAuthority> auths) {
        // from authorities (scope/ROLE_*)
        Set<String> roles = auths == null ? new HashSet<>() :
                auths.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        // from custom claim "roles"
        Object c = claims.get("roles");
        if (c instanceof Collection<?> col) {
            col.forEach(x -> roles.add(String.valueOf(x)));
        }
        return List.copyOf(roles);
    }

    private static String firstNonBlank(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }
}
