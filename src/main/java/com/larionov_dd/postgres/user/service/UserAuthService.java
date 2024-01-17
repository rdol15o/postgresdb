package com.larionov_dd.postgres.user.service;

import com.larionov_dd.postgres.user.entity.UserEntity;
import com.larionov_dd.postgres.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserAuthService implements UserDetailsService {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        if (optionalUserEntity.isEmpty()) throw new UsernameNotFoundException("User with this email not found");

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("user"));

        UserEntity user = optionalUserEntity.get();

        return new User(user.getEmail(), user.getPassword(), authorities);
    }
}
