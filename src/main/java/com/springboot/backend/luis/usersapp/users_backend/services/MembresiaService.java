package com.springboot.backend.luis.usersapp.users_backend.services;

import java.util.List;
import java.util.Optional;

import com.springboot.backend.luis.usersapp.users_backend.entities.Membresia;

public interface MembresiaService {
    List<Membresia> findAll();
    Optional<Membresia> findById(Long id);
    Membresia save(Membresia membresia);
    void deleteById(Long id);
    Optional<Membresia> update(Membresia membresia, Long id);
}
