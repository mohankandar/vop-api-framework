package com.tnl.vop.search.query;

import java.time.Instant;
import java.util.Objects;

/** Inclusive/exclusive range for numbers/strings/instants (binding decides coercion). */
public final class Range {
    public enum BoundType { OPEN, CLOSED }

    private final Object from;
    private final Object to;
    private final BoundType fromType;
    private final BoundType toType;

    private Range(Object from, BoundType fromType, Object to, BoundType toType) {
        this.from = from; this.to = to;
        this.fromType = fromType == null ? BoundType.CLOSED : fromType;
        this.toType = toType == null ? BoundType.CLOSED : toType;
    }

    public static Range closed(Object from, Object to) { return new Range(from, BoundType.CLOSED, to, BoundType.CLOSED); }
    public static Range open(Object from, Object to)   { return new Range(from, BoundType.OPEN, to, BoundType.OPEN); }
    public static Range atLeast(Object from)           { return new Range(Objects.requireNonNull(from), BoundType.CLOSED, null, BoundType.OPEN); }
    public static Range atMost(Object to)              { return new Range(null, BoundType.OPEN, Objects.requireNonNull(to), BoundType.CLOSED); }
    public static Range since(Instant from)            { return atLeast(from); }

    public Object from() { return from; }
    public Object to() { return to; }
    public BoundType fromType() { return fromType; }
    public BoundType toType() { return toType; }
}
