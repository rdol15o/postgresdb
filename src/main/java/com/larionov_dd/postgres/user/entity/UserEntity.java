package com.larionov_dd.postgres.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Entity
@NoArgsConstructor
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column(name = "first_name")
    protected String firstName;

    @Column(name = "last_name")
    protected String lastName;

    protected String email;
    protected String password;
}
