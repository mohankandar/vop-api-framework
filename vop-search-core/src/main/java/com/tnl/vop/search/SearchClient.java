package com.tnl.vop.search;

import com.tnl.vop.search.doc.BulkRequest;
import com.tnl.vop.search.doc.IndexRequest;
import com.tnl.vop.search.doc.RefreshPolicy;
import com.tnl.vop.search.search.SearchRequest;
import com.tnl.vop.search.search.SearchResponse;

import java.io.Closeable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Vendor-neutral search client SPI. Phase 1: interface only (no implementation).
 * Bindings (OpenSearch/Elasticsearch) will implement this in Phase 3.
 */
public interface SearchClient extends Closeable {

    /** Lightweight ping/health check. */
    boolean ping();

    /** Create or update an index with settings/mappings. Idempotent per binding. */
    void putIndex(String index, Map<String, Object> settingsOrMappings);

    /** Ensure index exists (create if missing with default settings). */
    void ensureIndex(String index);

    /** Drop an index if present. */
    void deleteIndex(String index);

    /** Index upsert. */
    void index(IndexRequest request, RefreshPolicy refresh);

    /** Bulk mixed operations (index/delete). */
    void bulk(BulkRequest bulk, RefreshPolicy refresh);

    /** Fetch a document by id (source deserialization is binding-specific). */
    Optional<Map<String, Object>> get(String index, String id);

    /** Execute a search and map sources as raw maps. */
    SearchResponse<Map<String, Object>> search(SearchRequest request);

    /** Close underlying resources (HTTP clients, etc.). */
    @Override
    void close();
}
