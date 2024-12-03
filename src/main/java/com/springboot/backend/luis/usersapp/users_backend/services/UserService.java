package com.springboot.backend.luis.usersapp.users_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import com.springboot.backend.luis.usersapp.users_backend.entities.User;
import com.springboot.backend.luis.usersapp.users_backend.models.UserRequest;


public interface UserService {

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(@NonNull Integer id);

    User save(User user);

    Optional<User> update(UserRequest user, Integer id);

    void deleteById(Integer id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String token);
}
