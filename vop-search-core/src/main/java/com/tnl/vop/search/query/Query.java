package com.tnl.vop.search.query;

/**
 * Marker for a simple, portable query AST.
 * Bindings translate this to native queries (OpenSearch/Elasticsearch).
 */
public sealed interface Query permits
        Query.Term, Query.Match, Query.RangeQ, Query.Bool {

    record Term(String field, Object value) implements Query { }
    record Match(String field, String text) implements Query { }
    record RangeQ(String field, Range range) implements Query { }
    record Bool(java.util.List<Query> must,
                java.util.List<Query> should,
                java.util.List<Query> filter,
                java.util.List<Query> mustNot) implements Query { }
}
