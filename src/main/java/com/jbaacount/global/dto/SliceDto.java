package com.jbaacount.global.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@NoArgsConstructor
public class SliceDto<T>
{
    private List<T> data;

    private SliceInfo sliceInfo;

    public SliceDto(List<T> data, Slice slice)
    {
        this.data = data;
        this.sliceInfo = new SliceInfo(slice.getSize(),
                slice.getNumberOfElements(), slice.hasNext());
    }
}
