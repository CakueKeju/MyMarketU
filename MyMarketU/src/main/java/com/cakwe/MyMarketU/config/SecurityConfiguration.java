/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.config;

import com.cakwe.MyMarketU.service.CustomUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    private final CustomUserDetailService userDetailsService;

    public SecurityConfiguration(CustomUserDetailService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
            .csrf(csrf -> csrf.disable()) // Nonaktifkan CSRF jika tidak diperlukan
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/registration","/assets/**", "/css/**", "/js/**", "/img/**").permitAll() // Izinkan akses ke /register tanpa login
                .anyRequest().authenticated() // Semua endpoint lain membutuhkan login
            )
            .formLogin(login -> login
                .loginPage("/login") // Halaman login
                .loginProcessingUrl("/login") // URL untuk proses login
                .usernameParameter("email") // Email sebagai username
                .passwordParameter("password") // Parameter password
                .successHandler((request, response, authentication) -> {
                    String redirectUrl = getRedirectUrlBasedOnRole(authentication);
                    response.sendRedirect(redirectUrl); // Redirect berdasarkan role
                })
                .failureUrl("/login?error=true") // Redirect jika login gagal
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // URL logout
                .logoutSuccessUrl("/") // Redirect ke home setelah logout
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecretKey")
                .tokenValiditySeconds(1209600) // 2 minggu
            );
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encoder for password hashing
    }

    private String getRedirectUrlBasedOnRole(org.springframework.security.core.Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .findFirst()
            .map(role -> {
                switch (role) {
                    case "ROLE_CUSTOMER": return "/customer/dashboard";
                    case "ROLE_ADMIN": return "/admin/dashboard";
                    default: return "/";
                }
            })
            .orElse("/");
    }
}