package com.blog.app.config;

import com.blog.app.config.filters.AuthenticationFilter;
import com.blog.app.config.filters.JWTAuthenticationFilter;
import com.blog.app.config.jwt.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;


    @Autowired
    public SpringSecurityConfig(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JWTService jwtService
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Bean
    protected SecurityFilterChain filterChain(
            HttpSecurity http,
            @Autowired AuthenticationManager authenticationManager
    ) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/api/1.0/auth").permitAll()
                .requestMatchers("/api/**").authenticated()
                //.requestMatchers("/secure/info").authenticated()
                .anyRequest().permitAll();
        http.addFilter(new AuthenticationFilter(authenticationManager, jwtService));
        http.addFilterBefore(new JWTAuthenticationFilter(), AuthenticationFilter.class);
        //http.addFilterBefore(new TokenAuthenticationFilter(), AuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }


}
