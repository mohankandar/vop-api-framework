package com.tnl.vop.search.search;

import com.tnl.vop.core.paging.Paged;

import java.util.List;

public final class SearchResponse<T> {
    private final long tookMillis;
    private final Paged<Hit<T>> page;

    private SearchResponse(long tookMillis, Paged<Hit<T>> page) {
        this.tookMillis = tookMillis;
        this.page = page;
    }

    public long getTookMillis() { return tookMillis; }
    public Paged<Hit<T>> getPage() { return page; }

    public static <T> SearchResponse<T> of(long tookMillis, List<Hit<T>> hits, int page, int size, long total) {
        return new SearchResponse<>(tookMillis, Paged.of(hits, page, size, total));
    }
}
