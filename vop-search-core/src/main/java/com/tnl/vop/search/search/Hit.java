package com.tnl.vop.search.search;

import java.util.List;
import java.util.Map;

public record Hit<T>(String id, Double score, T source, Map<String, List<String>> highlight) { }
