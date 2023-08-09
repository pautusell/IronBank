package com.ironhack.ironbank.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, "/accounts").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/accounts").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/accounts").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/accounts").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/accounts").hasRole("ADMIN")
                .requestMatchers( "/operations/account/**").hasRole("USER")
                .requestMatchers( "/operations/card/**").permitAll()
                .requestMatchers( "/third-party/**").permitAll()
                .requestMatchers( "/users/ah/create").permitAll()
                .requestMatchers( "/users/reset-password").permitAll()
                .requestMatchers( "/users/ah/**").hasRole("USER")
                .requestMatchers( "/users/admin/**").hasRole("ADMIN")
                .requestMatchers( "/users/delete/**").hasRole("ADMIN")
                .requestMatchers( "/reports/activity/**").hasRole("USER")
                .requestMatchers( "/reports/admin/**").hasRole("ADMIN")
                .anyRequest()
                .permitAll()
                .and()
                .userDetailsService(jpaUserDetailsService)
                .httpBasic()
                .and()
                .build();
    }
}
