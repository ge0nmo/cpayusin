package com.jbaacount.payload.request.member;

import com.jbaacount.global.validation.CustomEmailValidation;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailRequest
{
    @CustomEmailValidation
    @Email
    private String email;
}
