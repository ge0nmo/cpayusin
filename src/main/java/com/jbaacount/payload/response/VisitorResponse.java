package com.jbaacount.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VisitorResponse
{
    private Long yesterday;
    private Long today;
    private Long total;

}
