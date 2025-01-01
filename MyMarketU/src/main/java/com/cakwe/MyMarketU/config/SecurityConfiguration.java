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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
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
                // URL publik
                .requestMatchers("/", "/login", "/register", "/registration",
                               "/assets/**", "/css/**", "/js/**", "/img/**").permitAll()
                
                // URL khusus admin
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/admin/settings/**").hasRole("ADMIN")
                .requestMatchers("/admin/profile-settings-admin/**").hasRole("ADMIN")
                .requestMatchers("/admin/security-settings-admin/**").hasRole("ADMIN")
                
                // URL khusus customer
                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                
                // Semua request lainnya harus terautentikasi
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    logger.info("Login successful for user: {}", authentication.getName());
                    String redirectUrl = getRedirectUrlBasedOnRole(authentication);
                    response.sendRedirect(redirectUrl);
                })
                .failureHandler((request, response, exception) -> {
                    logger.warn("Login failed for user: {}", request.getParameter("email"));
                    response.sendRedirect("/login?error=true");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    logger.info("User logged out successfully");
                    response.sendRedirect("/?logout=true");
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecretKey")
                .tokenValiditySeconds(1209600) // 2 minggu
                .rememberMeParameter("remember-me")
                .userDetailsService(userDetailsService)
            )
            .sessionManagement(session -> session
                .maximumSessions(1) // Batasi 1 sesi per user
                .expiredUrl("/login?expired=true")
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private String getRedirectUrlBasedOnRole(org.springframework.security.core.Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .findFirst()
            .map(role -> {
                switch (role) {
                    case "ROLE_CUSTOMER":
                        logger.info("Redirecting customer to dashboard");
                        return "/customer/dashboard";
                    case "ROLE_ADMIN":
                        logger.info("Redirecting admin to dashboard");
                        return "/admin/dashboard";
                    default:
                        logger.warn("Unknown role: {}, redirecting to home", role);
                        return "/";
                }
            })
            .orElse("/");
    }
}