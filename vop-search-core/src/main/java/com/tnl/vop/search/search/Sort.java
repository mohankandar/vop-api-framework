package com.tnl.vop.search.search;

import com.tnl.vop.core.paging.SortOrder;

public record Sort(String field, SortOrder.Direction direction) {
    public static Sort asc(String field) { return new Sort(field, SortOrder.Direction.ASC); }
    public static Sort desc(String field) { return new Sort(field, SortOrder.Direction.DESC); }
}
