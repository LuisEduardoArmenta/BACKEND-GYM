package com.springboot.backend.luis.usersapp.users_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.backend.luis.usersapp.users_backend.entities.Caja;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Long> {
    Optional<Caja> findFirstByEstadoOrderByFechaAperturaDesc(String estado);
}
