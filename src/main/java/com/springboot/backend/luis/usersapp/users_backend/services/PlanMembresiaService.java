package com.springboot.backend.luis.usersapp.users_backend.services;

import com.springboot.backend.luis.usersapp.users_backend.entities.PlanMembresia;
import java.util.List;
import java.util.Optional;

public interface PlanMembresiaService {
    List<PlanMembresia> findAll();
    Optional<PlanMembresia> findById(Long id);
    PlanMembresia save(PlanMembresia planMembresia);
    void deleteById(Long id);
    Optional<PlanMembresia> findByDuracion(String duracion);
} 