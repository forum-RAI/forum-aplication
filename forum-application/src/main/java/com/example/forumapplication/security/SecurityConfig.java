package com.example.forumapplication.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public static final int BCRYPT_STRENGTH = 11;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers("/myCards").authenticated()
                                        .requestMatchers("/auth/login").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/users/").authenticated()
                                        .requestMatchers(HttpMethod.DELETE, "/api/users").authenticated()
                                        .requestMatchers(HttpMethod.POST, "api/users/register").permitAll()
                                        .requestMatchers(HttpMethod.POST, "api/users/admin/register").hasRole("ADMIN")
                                        .requestMatchers(HttpMethod.POST, "api/users/admin/register/").hasRole("ADMIN")
                                        .requestMatchers("/").permitAll()
                                        .requestMatchers("/contact").permitAll()
                                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/auth/login")
                        .defaultSuccessUrl("/home")
                        .failureUrl("/auth/login?error=true")
                        .permitAll())
                .csrf(AbstractHttpConfigurer::disable)
//                .csrf(cr -> cr.disable())
                .formLogin(withDefaults());
//                .httpBasic(withDefaults());
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }



}