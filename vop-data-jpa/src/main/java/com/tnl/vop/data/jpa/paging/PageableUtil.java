package com.tnl.vop.data.jpa.paging;

import com.tnl.vop.core.paging.PageRequest;
import com.tnl.vop.core.paging.SortOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.lang.reflect.Method;

public class PageableUtil {

    public Pageable toPageable(PageRequest req) {
        if (req == null) {
            return org.springframework.data.domain.PageRequest.of(0, 10);
        }

        Sort sort = Sort.unsorted();
        if (req.getSort() != null && !req.getSort().isEmpty()) {
            Sort.Order[] orders = req.getSort().stream()
                    .map(o -> new Sort.Order(
                            toSpringDirection(o),
                            resolveProperty(o)))
                    .toArray(Sort.Order[]::new);
            sort = Sort.by(orders);
        }

        return org.springframework.data.domain.PageRequest.of(req.getPage(), req.getSize(), sort);
    }

    private Sort.Direction toSpringDirection(SortOrder o) {
        try {
            // preferred enum method
            SortOrder.Direction d = (SortOrder.Direction)
                    o.getClass().getMethod("getDirection").invoke(o);
            return (d == SortOrder.Direction.DESC) ? Sort.Direction.DESC : Sort.Direction.ASC;
        } catch (Exception ignore) {
            // fallback: maybe it's a String direction
            try {
                Object val = o.getClass().getMethod("getDirection").invoke(o);
                return String.valueOf(val).equalsIgnoreCase("DESC")
                        ? Sort.Direction.DESC : Sort.Direction.ASC;
            } catch (Exception e2) {
                return Sort.Direction.ASC;
            }
        }
    }

    /** Support different field names: property / field / name */
    private String resolveProperty(SortOrder o) {
        for (String m : new String[]{"getProperty", "getField", "getName"}) {
            try {
                Method mm = o.getClass().getMethod(m);
                Object v = mm.invoke(o);
                if (v != null && !String.valueOf(v).isBlank()) return String.valueOf(v);
            } catch (Exception ignore) { }
        }
        return "id"; // safe default if not provided
    }
}
