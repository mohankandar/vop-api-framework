package com.tnl.vop.search.doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class BulkRequest {
    public sealed interface Op permits IndexOp, DeleteOp {
        String index();
        String id();
        String routing();
    }

    public static final class IndexOp implements Op {
        private final String index;
        private final String id; // may be null for autogen
        private final Map<String, Object> source;
        private final String routing;

        public IndexOp(String index, String id, Map<String, Object> source, String routing) {
            this.index = index; this.id = id; this.source = source; this.routing = routing;
        }
        public String index() { return index; }
        public String id() { return id; }
        public Map<String, Object> source() { return source; }
        public String routing() { return routing; }
    }

    public static final class DeleteOp implements Op {
        private final String index;
        private final String id;
        private final String routing;

        public DeleteOp(String index, String id, String routing) {
            this.index = index; this.id = id; this.routing = routing;
        }
        public String index() { return index; }
        public String id() { return id; }
        public String routing() { return routing; }
    }

    private final List<Op> operations;

    private BulkRequest(List<Op> ops) {
        this.operations = Collections.unmodifiableList(new ArrayList<>(ops));
    }

    public List<Op> operations() { return operations; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final List<Op> ops = new ArrayList<>();
        public Builder index(String index, String id, Map<String,Object> source) {
            ops.add(new IndexOp(index, id, source, null)); return this;
        }
        public Builder index(String index, Map<String,Object> source) {
            ops.add(new IndexOp(index, null, source, null)); return this;
        }
        public Builder delete(String index, String id) {
            ops.add(new DeleteOp(index, id, null)); return this;
        }
        public BulkRequest build() { return new BulkRequest(ops); }
    }
}
