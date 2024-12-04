package com.springboot.backend.luis.usersapp.users_backend.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.filter.CorsFilter;

import com.springboot.backend.luis.usersapp.users_backend.auth.filter.JwtAuthenticationFilter;
import com.springboot.backend.luis.usersapp.users_backend.auth.filter.JwtValidationFilter;

import jakarta.servlet.FilterRegistration;


@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authz -> authz
                // Rutas protegidas para usuarios
                .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/page/{page}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/register", "/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")
            
                // Rutas protegidas para planes de membresía
                .requestMatchers(HttpMethod.GET, "/api/planes").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/planes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/planes/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/planes/{id}").hasRole("ADMIN")
                
                // Rutas protegidas para membresías
                .requestMatchers(HttpMethod.GET, "/api/membresias").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/membresias/{id}").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/api/membresias/usuario/{userId}").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/membresias").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/membresias/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/membresias/{id}").hasRole("ADMIN")

                // Rutas protegidas para productos
                .requestMatchers(HttpMethod.GET, "/api/productos").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/productos/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/productos/buscar/{nombre}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/productos/categoria/{categoria}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/productos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/productos/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/{id}").hasRole("ADMIN")
                
                // Rutas protegidas para ventas
                .requestMatchers(HttpMethod.GET, "/api/ventas/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/ventas/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.PUT, "/api/ventas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/ventas/**").hasRole("ADMIN")
                
                
                // Rutas protegidas para control de caja
                .requestMatchers(HttpMethod.GET, "/api/caja/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/caja/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/caja/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/caja/**").hasRole("ADMIN")

                // Rutas protegidas para movimientos de caja
                .requestMatchers(HttpMethod.GET, "/api/movimientos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/movimientos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/movimientos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/movimientos/**").hasRole("ADMIN")

                // Rutas protegidas para recuperación de contraseña
                .requestMatchers("/api/password/**").permitAll()

                // Rutas protegidas para acceso
                .requestMatchers("/api/access/**").permitAll()
                .requestMatchers("/api/qr/**").permitAll()

                // Rutas para QR y Access Control
                .requestMatchers("/api/qr/generate/**").permitAll()
                .requestMatchers("/api/qr/validate").permitAll()
                .requestMatchers("/api/access/register").permitAll()
                .requestMatchers("/api/access/logs/**").permitAll()

                .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(config -> config.disable())
                .cors(cors -> cors.configurationSource(configurationSource()))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    CorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);        

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(
            new CorsFilter(configurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
        
    }


    }

