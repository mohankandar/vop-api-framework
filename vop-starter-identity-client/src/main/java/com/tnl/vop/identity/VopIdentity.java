package com.tnl.vop.identity;

import java.util.List;
import java.util.Map;

public record VopIdentity(
        String networkId,
        String firstName,
        String lastName,
        String email,
        List<String> roles,
        Map<String, Object> attributes
) {
    public static VopIdentity minimal(String networkId) {
        return new VopIdentity(networkId, null, null, null, List.of(), Map.of());
    }
}
