package com.tnl.vop.search.doc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class IndexRequest {
    public enum OpType { INDEX, CREATE } // CREATE = fail if exists; INDEX = upsert/replace

    private final String index;
    private final String id;
    private final Map<String, Object> source;
    private final OpType opType;
    private final String routing;

    private IndexRequest(Builder b) {
        this.index = Objects.requireNonNull(b.index, "index");
        this.id = b.id; // optional; server may autogen if null and supported
        this.source = Collections.unmodifiableMap(new HashMap<>(Objects.requireNonNull(b.source, "source")));
        this.opType = b.opType == null ? OpType.INDEX : b.opType;
        this.routing = b.routing;
    }

    public String getIndex() { return index; }
    public String getId() { return id; }
    public Map<String, Object> getSource() { return source; }
    public OpType getOpType() { return opType; }
    public String getRouting() { return routing; }

    public static Builder builder(String index) { return new Builder().index(index); }

    public static final class Builder {
        private String index;
        private String id;
        private Map<String, Object> source = new HashMap<>();
        private OpType opType = OpType.INDEX;
        private String routing;

        public Builder index(String index) { this.index = index; return this; }
        public Builder id(String id) { this.id = id; return this; }
        public Builder source(Map<String, Object> source) { this.source = source; return this; }
        public Builder opType(OpType opType) { this.opType = opType; return this; }
        public Builder routing(String routing) { this.routing = routing; return this; }
        public IndexRequest build() { return new IndexRequest(this); }
    }
}
