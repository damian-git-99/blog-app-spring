package com.blog.app.config.security;

import com.blog.app.config.security.filters.AuthenticationFilter;
import com.blog.app.config.security.filters.JWTAuthenticationFilter;
import com.blog.app.config.security.jwt.JWTService;
import com.blog.app.config.security.providers.JWTAuthenticationProvider;
import com.blog.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final UserService userService;


    @Autowired
    public SpringSecurityConfig(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JWTService jwtService,
            UserService userService
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Bean
    protected SecurityFilterChain filterChain(
            HttpSecurity http,
            @Autowired AuthenticationManager authenticationManager
    ) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.logout().disable();
        http.authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/logout").permitAll()
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/verify-token").authenticated()
                // user
                .requestMatchers(HttpMethod.GET, "/users/profile").authenticated()
                .requestMatchers(HttpMethod.PUT, "/users/profile/{id}").authenticated()
                .requestMatchers(HttpMethod.POST, "/users/add-favorite-post/{postId}").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/users/add-favorite-post/{postId}").authenticated()
                .requestMatchers(HttpMethod.GET, "/users/is-favorite-post/{postId}").authenticated()
                .requestMatchers(HttpMethod.GET, "/users/favorite-posts").authenticated()
                // post
                .requestMatchers(HttpMethod.POST, "/posts").authenticated()
                .requestMatchers(HttpMethod.GET, "/posts").permitAll()
                .requestMatchers(HttpMethod.GET, "/my-posts").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/{id}").authenticated()
                .requestMatchers(HttpMethod.GET, "/{id}").anonymous()
                .requestMatchers(HttpMethod.PUT, "/{id}").authenticated()
                .requestMatchers(HttpMethod.PUT, "/toggle-status/{id}").authenticated()
                .requestMatchers(HttpMethod.GET, "/by-username/{username}").authenticated()
                //.requestMatchers("/secure/info").authenticated()
                .anyRequest().permitAll();
        http.addFilter(new AuthenticationFilter(authenticationManager, jwtService, userService));
        http.addFilterAfter(new JWTAuthenticationFilter(authenticationManager), AuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .authenticationProvider(new JWTAuthenticationProvider(jwtService))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://vite-frontend:5173", "http://localhost:5173"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }


}
