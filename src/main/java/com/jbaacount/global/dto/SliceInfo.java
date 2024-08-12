package com.jbaacount.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Slice;

@AllArgsConstructor
@Data
public class SliceInfo<T>
{
    private int size; //1개의 슬라이스에 포함 된 최대 개수

    private int numberOfElements; //현재 슬라이스에 실제로 포함 된 개수
    private boolean hasNext; //다음 페이지가 있는가?

    public SliceInfo (Slice<T> slice)
    {
        this.size = slice.getSize();
        this.numberOfElements = slice.getNumberOfElements();
        this.hasNext = slice.hasNext();
    }
}
