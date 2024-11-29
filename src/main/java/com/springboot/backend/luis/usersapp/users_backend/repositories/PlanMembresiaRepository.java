package com.springboot.backend.luis.usersapp.users_backend.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.springboot.backend.luis.usersapp.users_backend.entities.PlanMembresia;

public interface PlanMembresiaRepository extends JpaRepository<PlanMembresia, Long> {
    Optional<PlanMembresia> findByDuracion(String duracion);
}
