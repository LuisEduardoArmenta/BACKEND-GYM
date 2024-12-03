package com.springboot.backend.luis.usersapp.users_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.backend.luis.usersapp.users_backend.entities.Objetivo;

@Repository
public interface ObjetivoRepository extends JpaRepository<Objetivo, Long> {
    List<Objetivo> findByUserIdOrderByCreatedAtDesc(Long userId);
}
