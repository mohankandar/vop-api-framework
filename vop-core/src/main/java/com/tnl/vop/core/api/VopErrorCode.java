package com.tnl.vop.core.api;

/**
 * Marker interface for all error codes used in VOP.
 *
 * Framework-level enums (ErrorCode) and application-specific enums
 * can both implement this, and all can be passed to ErrorDetail.of(...).
 */
public interface VopErrorCode {
  String getCode();
}
