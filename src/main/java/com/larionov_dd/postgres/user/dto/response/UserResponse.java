package com.larionov_dd.postgres.user.dto.response;

import com.larionov_dd.postgres.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserResponse extends UserEntity {

    protected Long id;
    protected String firstName;
    protected String lastName;
    protected String email;

    public static UserResponse of(UserEntity user){
        return UserResponse.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .build();



    }
}
