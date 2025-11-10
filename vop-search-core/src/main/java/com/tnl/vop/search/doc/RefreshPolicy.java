package com.tnl.vop.search.doc;

public enum RefreshPolicy {
    NONE,          // let the engine refresh on its own schedule
    IMMEDIATE      // force refresh after operation (slower, read-after-write)
}
