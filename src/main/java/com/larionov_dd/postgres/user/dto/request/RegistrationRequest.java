package com.larionov_dd.postgres.user.dto.request;

import com.larionov_dd.postgres.user.exception.BadRequestException;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public void validate() throws BadRequestException {
        if(email == null || email.isBlank()) throw new BadRequestException();
        if(password == null || password.isBlank()) throw new BadRequestException();

    }
}
