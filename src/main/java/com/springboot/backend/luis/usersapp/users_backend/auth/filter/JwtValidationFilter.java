package com.springboot.backend.luis.usersapp.users_backend.auth.filter;

import static com.springboot.backend.luis.usersapp.users_backend.auth.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.backend.luis.usersapp.users_backend.auth.SimpleGrantedAuthorityJsonCreator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Arrays;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends BasicAuthenticationFilter{

    
    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
    private boolean isPublicRoute(String uri) {
        return uri.equals("/api/users/register") || 
        uri.equals("/api/users/login") || 
        uri.startsWith("/api/objetivos");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    
        String requestURI = request.getRequestURI();
    
        // Excluir rutas públicas del filtro JWT
        if (isPublicRoute(requestURI)) {
            chain.doFilter(request, response);
            return;
        }
    
        String header = request.getHeader(HEADER_AUTHORIZATION);
        System.out.println("URI requested: " + request.getRequestURI());
        System.out.println("Method: " + request.getMethod());
        System.out.println("Authorization header: " + header);
    
        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            chain.doFilter(request, response);
            return;
        }
    
        String token = header.replace(PREFIX_TOKEN, "");
        try {
            Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
            System.out.println("Token validated successfully");
            System.out.println("Claims: " + claims);
            String username = claims.getSubject();
            Object authoritiesClaims = claims.get("authorities");
    
            // Agregar logging
            System.out.println("Token claims: " + claims);
            System.out.println("Authorities: " + authoritiesClaims);

            Collection<? extends GrantedAuthority> roles = Arrays.asList(new ObjectMapper()
                .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));
    
            // Verificar roles
            System.out.println("Roles parsed: " + roles);
    
            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(username, null, roles);
    
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);
        } catch (JwtException e) {
            System.out.println("Token validation failed: " + e.getMessage());
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token es invalido!");
    
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(CONTENT_TYPE);
        }
    }
    
            
    }


