package com.jbaacount.global.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaginationUtils
{
    public <T> Slice<T> toSlice(Pageable pageable, List<T> entities)
    {
        boolean hasNext = false;
        if(entities.size() > pageable.getPageSize())
        {
           entities.remove(entities.size() - 1);
           return new SliceImpl<>(entities, pageable, true);
        }

        return new SliceImpl<>(entities, pageable, false);
    }
}
