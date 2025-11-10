package com.tnl.vop.identity;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface VopIdentityClient {
    /** Resolve identity for the currently-authenticated user (from SecurityContext). */
    Optional<VopIdentity> current();

    /** Resolve identity for a specific networkId (may call remote). */
    Optional<VopIdentity> byNetworkId(@NonNull String networkId, @Nullable String bearerToken);
}
