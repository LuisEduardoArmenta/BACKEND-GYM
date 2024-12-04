package com.springboot.backend.luis.usersapp.users_backend.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.luis.usersapp.users_backend.entities.User;
import com.springboot.backend.luis.usersapp.users_backend.repositories.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== DEBUG LOGIN ===");
        System.out.println("Intentando autenticar usuario: " + username);
        
        Optional<User> optionalUser = repository.findByUsername(username);
        
        if (optionalUser.isEmpty()) {
            System.out.println("Usuario no encontrado: " + username);
            throw new UsernameNotFoundException(String.format("Username %s no existe en el sistema", username));
        }

        User user = optionalUser.orElseThrow();
        System.out.println("Usuario encontrado: " + username);
        System.out.println("Password hash almacenado: " + user.getPassword());

        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
            username,
            user.getPassword(),
            true,
            true,
            true,
            true,
            authorities
        );
    }

}
