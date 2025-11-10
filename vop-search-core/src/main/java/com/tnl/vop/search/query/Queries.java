package com.tnl.vop.search.query;

import java.util.List;

public final class Queries {
    private Queries() {}

    public static Query term(String field, Object value) { return new Query.Term(field, value); }
    public static Query match(String field, String text) { return new Query.Match(field, text); }
    public static Query range(String field, Range r)     { return new Query.RangeQ(field, r); }
    public static Query bool(List<Query> must, List<Query> should,
                             List<Query> filter, List<Query> mustNot) {
        return new Query.Bool(nvl(must), nvl(should), nvl(filter), nvl(mustNot));
    }
    private static <T> List<T> nvl(List<T> l) { return l == null ? List.of() : List.copyOf(l); }
}
