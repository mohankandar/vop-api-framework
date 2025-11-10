package com.tnl.vop.search.search;

import com.tnl.vop.core.paging.PageRequest;
import com.tnl.vop.search.query.Query;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SearchRequest {
    private final Set<String> indices;
    private final Query query;
    private final PageRequest page;
    private final List<Sort> sort;
    private final List<String> includeFields;
    private final List<String> excludeFields;

    private SearchRequest(Builder b) {
        this.indices = Collections.unmodifiableSet(new HashSet<>(b.indices));
        this.query = b.query;
        this.page = b.page == null ? PageRequest.of(0, 10) : b.page;
        this.sort = List.copyOf(b.sort);
        this.includeFields = List.copyOf(b.includeFields);
        this.excludeFields = List.copyOf(b.excludeFields);
    }

    public Set<String> getIndices() { return indices; }
    public Query getQuery() { return query; }
    public PageRequest getPage() { return page; }
    public List<Sort> getSort() { return sort; }
    public List<String> getIncludeFields() { return includeFields; }
    public List<String> getExcludeFields() { return excludeFields; }

    public static Builder builder(String... indices) { return new Builder(indices); }

    public static final class Builder {
        private final Set<String> indices = new HashSet<>();
        private Query query;
        private PageRequest page;
        private List<Sort> sort = List.of();
        private List<String> includeFields = List.of();
        private List<String> excludeFields = List.of();

        public Builder(String... idx) { if (idx != null) for (var i : idx) if (i != null && !i.isBlank()) indices.add(i); }
        public Builder query(Query q) { this.query = q; return this; }
        public Builder page(PageRequest p) { this.page = p; return this; }
        public Builder sort(List<Sort> s) { this.sort = s == null ? List.of() : List.copyOf(s); return this; }
        public Builder include(List<String> f) { this.includeFields = f == null ? List.of() : List.copyOf(f); return this; }
        public Builder exclude(List<String> f) { this.excludeFields = f == null ? List.of() : List.copyOf(f); return this; }
        public SearchRequest build() { return new SearchRequest(this); }
    }
}
