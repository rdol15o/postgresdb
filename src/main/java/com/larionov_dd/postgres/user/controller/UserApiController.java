package com.larionov_dd.postgres.user.controller;

import com.larionov_dd.postgres.user.dto.request.RegistrationRequest;
import com.larionov_dd.postgres.user.dto.request.EditUserRequest;
import com.larionov_dd.postgres.user.dto.response.UserResponse;
import com.larionov_dd.postgres.user.entity.UserEntity;
import com.larionov_dd.postgres.user.exception.BadRequestException;
import com.larionov_dd.postgres.user.exception.UserAlreadyExistExceprion;
import com.larionov_dd.postgres.user.exception.UserNotFoundException;
import com.larionov_dd.postgres.user.repository.UserRepository;
import com.larionov_dd.postgres.user.routes.UserRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserApiController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${init.email}")
    private String initUser;
    @Value("${init.password}")
    private String initPassord;

    @GetMapping("/")
    public UserEntity root(){
        UserEntity user = UserEntity.builder()
                .firstName("test")
                .lastName("test")
                .build();

        user = userRepository.save(user);
        return user;
    }

    @PostMapping(UserRoutes.REGISTRATION)
    public UserResponse registration(@RequestBody RegistrationRequest request) throws BadRequestException, UserAlreadyExistExceprion {
        request.validate();

        Optional<UserEntity> check = userRepository.findByEmail(request.getEmail());
        if (check.isPresent()) throw new UserAlreadyExistExceprion();

        UserEntity user = UserEntity.builder()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);
        return UserResponse.of(user);
    }

    @GetMapping(UserRoutes.BY_ID)
    public UserResponse byId(@PathVariable Long id) throws UserNotFoundException {
        return UserResponse.of(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
    }

    @GetMapping(UserRoutes.SEARCH)
    public List<UserResponse> search(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "") String query
    ){
        Pageable pageable = PageRequest.of(page, size);

        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Example<UserEntity> example = Example.of(
                UserEntity.builder().firstName(query).lastName(query).build(),
                exampleMatcher);

        return userRepository.findAll(example, pageable).stream().map(UserResponse::of).collect(Collectors.toList());
    }

    @Operation(summary = "Редактирование пользвателя.", description = "Редактируем пользователя, который существует в базе.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Успешно отредактированный пользователь",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = {@Content}),
                    @ApiResponse(responseCode = "404", description = "Пользователь с таким id не найден",
                            content = {@Content})
            }
    )
    @PutMapping(UserRoutes.EDIT)
    public UserResponse edit(Principal principal, @RequestBody EditUserRequest request) throws UserNotFoundException {
        UserEntity user = userRepository.findByEmail(principal.getName()).orElseThrow(UserNotFoundException::new);
        user.setLastName(request.getLastName());
        user.setFirstName(request.getFirstName());
        user = userRepository.save(user);

        return UserResponse.of(user);

    }

    @DeleteMapping(UserRoutes.BY_ID)
    public String delete(@PathVariable Long id){
        userRepository.deleteById(id);
        return HttpStatus.OK.name();
    }

    @GetMapping(UserRoutes.INIT)
    public UserResponse init(){
        Optional<UserEntity> checkUser = userRepository.findByEmail(initUser);
        UserEntity user;

        if (checkUser.isEmpty()){
            user = UserEntity.builder()
                    .firstName("Default")
                    .lastName("Default")
                    .email(initUser)
                    .password(passwordEncoder.encode(initPassord))
                    .build();
            user = userRepository.save(user);
        }else{
            user = checkUser.get();
        }

        return UserResponse.of(user);
    }


}
