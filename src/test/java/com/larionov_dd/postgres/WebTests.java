package com.larionov_dd.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.larionov_dd.postgres.user.dto.request.RegistrationRequest;
import com.larionov_dd.postgres.user.dto.request.EditUserRequest;
import com.larionov_dd.postgres.user.dto.response.UserResponse;
import com.larionov_dd.postgres.user.entity.UserEntity;
import com.larionov_dd.postgres.user.repository.UserRepository;
import com.larionov_dd.postgres.user.routes.UserRoutes;
import lombok.RequiredArgsConstructor;
import org.h2.engine.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WebTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Value("${init.email}")
    private String initUser;

    @Value("${init.password}")
    private String initPassword;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void config(){
        Optional<UserEntity> check = userRepository.findByEmail(initUser);
        if(check.isPresent()) return;

        UserEntity user = UserEntity.builder()
                .firstName("")
                .lastName("")
                .email(initUser)
                .password(passwordEncoder.encode(initPassword))
                .build();

        userRepository.save(user);
    }

    public String authHeader(){
        return "Basic " + Base64.getEncoder().encodeToString((initUser + ":" + initPassword).getBytes());
    }

    @Test
    void contextLoad() throws Exception {
        UserEntity user = UserEntity.builder()
                .firstName("1")
                .lastName("1")
                .build();

        user = userRepository.save(user);

        mockMvc.perform(
                MockMvcRequestBuilders.get(UserRoutes.BY_ID, user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader())
        ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void createTest() throws Exception{
        RegistrationRequest request = RegistrationRequest.builder()
                .firstName("createTest")
                .lastName("createTest")
                .email("e@e.ru")
                .password("1")
                .build();

        mockMvc.perform(
                MockMvcRequestBuilders.post(UserRoutes.REGISTRATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(content().string(containsString("createTest")));


    }

    @Test
    void findByIdTest() throws Exception{
        UserEntity user = UserEntity.builder()
                .firstName("findById")
                .lastName("findById")
                .build();

        userRepository.save(user);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(UserRoutes.BY_ID, user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authHeader())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("findById")));


    }

    @Test
    void findById_NotFound_Test() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get(UserRoutes.BY_ID, 1000)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authHeader())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTest() throws Exception{
        UserEntity user = UserEntity.builder()
                .firstName("q")
                .lastName("q")
                .build();

        userRepository.save(user);

        EditUserRequest request = EditUserRequest.builder()
                .firstName("updateTest")
                .lastName("updateTest")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders.put(UserRoutes.EDIT, user.getId().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header(HttpHeaders.AUTHORIZATION, authHeader())
                )
                .andDo(print())
                .andExpect(content().string(containsString("updateTest")));

    }

    @Test
    void deleteTest() throws Exception{
        UserEntity user = UserEntity.builder()
                .firstName("updateTest")
                .lastName("updateTest")
                .build();

        user = userRepository.save(user);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(UserRoutes.BY_ID, user.getId())
                                .header(HttpHeaders.AUTHORIZATION, authHeader())
                )
                .andDo(print())
                .andExpect(status().isOk());

        assert userRepository.findById(user.getId()).isEmpty();
    }

    @Test
    void searchTest() throws Exception{
        List<UserResponse> result = new ArrayList<>();

        for (int i = 0; i < 100; i++){
            UserEntity user = UserEntity.builder()
                    .firstName("firstName_"+i)
                    .lastName("lastName_"+i)
                    .build();

            user = userRepository.save(user);
            result.add(UserResponse.of(user));
        }

        mockMvc.perform(
                        MockMvcRequestBuilders.get(UserRoutes.SEARCH)
                                .param("size", "100")
                                .param("query", "firstName_")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authHeader())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));

    }
}
