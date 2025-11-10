package com.tnl.vop.data.jpa.paging;

import com.tnl.vop.core.paging.Paged;
import org.springframework.data.domain.Page;

import java.util.List;

/** Maps Spring Data Page<T> to framework Paged<T>. */
public class PageMapper {
    public <T> Paged<T> toPaged(Page<T> page) {
        List<T> content = page.getContent();
        return Paged.of(content, page.getNumber(), page.getSize(), page.getTotalElements());
    }
}
