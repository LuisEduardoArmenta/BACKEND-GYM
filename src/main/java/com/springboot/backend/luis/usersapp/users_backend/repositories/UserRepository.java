package com.springboot.backend.luis.usersapp.users_backend.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.springboot.backend.luis.usersapp.users_backend.entities.User;


public interface UserRepository extends CrudRepository<User, Integer>{

    Page<User> findAll(Pageable pageable);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String token);
}