package com.larionov_dd.postgres.user.dto.request;

import lombok.Getter;

@Getter
public class CreateUserRequest {
    private String firstName;
    private String lastName;
}
