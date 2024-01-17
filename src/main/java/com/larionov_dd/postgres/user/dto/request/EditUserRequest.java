package com.larionov_dd.postgres.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EditUserRequest {
    public String get;
    private String firstName;
    private String lastName;

}
