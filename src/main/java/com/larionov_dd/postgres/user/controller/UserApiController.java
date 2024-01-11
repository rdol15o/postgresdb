package com.larionov_dd.postgres.user.controller;

import com.larionov_dd.postgres.user.dto.request.CreateUserRequest;
import com.larionov_dd.postgres.user.dto.request.EditUserRequest;
import com.larionov_dd.postgres.user.dto.response.UserResponse;
import com.larionov_dd.postgres.user.entity.UserEntity;
import com.larionov_dd.postgres.user.exception.UserNotFoundException;
import com.larionov_dd.postgres.user.repository.UserRepository;
import com.larionov_dd.postgres.user.routes.UserRoutes;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class UserApiController {
    private final UserRepository userRepository;

    @GetMapping("/")
    public UserEntity test(){
        UserEntity user = UserEntity.builder()
                .firstName("test")
                .lastName("test")
                .build();

        user = userRepository.save(user);
        return user;
    }

    @PostMapping(UserRoutes.CREATE)
    public UserResponse create(@RequestBody CreateUserRequest request){
        UserEntity user = UserEntity.builder()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
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

    @PutMapping(UserRoutes.BY_ID)
    public UserResponse edit(@PathVariable Long id, @RequestBody EditUserRequest request) throws UserNotFoundException {
        UserEntity user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
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


}
