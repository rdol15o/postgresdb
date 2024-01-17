package com.larionov_dd.postgres.base.config;

import com.larionov_dd.postgres.base.routes.BaseRoutes;
import com.larionov_dd.postgres.user.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class BaseConfig {
    @Autowired
    private UserAuthService userAuthService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests(
                        auth -> auth.requestMatchers((BaseRoutes.NOT_SECURED + "/**")).anonymous()
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(AbstractHttpConfigurer::disable)
                .userDetailsService(userAuthService)
                .build();
    }
}
