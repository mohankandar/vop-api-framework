package com.tnl.vop.search;

/** Generic search exception with optional HTTP/status code. */
public class SearchException extends RuntimeException {
    private final Integer status;

    public SearchException(String message) { super(message); this.status = null; }
    public SearchException(String message, Throwable cause) { super(message, cause); this.status = null; }
    public SearchException(String message, int status) { super(message); this.status = status; }
    public SearchException(String message, int status, Throwable cause) { super(message, cause); this.status = status; }

    public Integer getStatus() { return status; }
}
