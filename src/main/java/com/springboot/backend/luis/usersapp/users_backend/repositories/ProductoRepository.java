package com.springboot.backend.luis.usersapp.users_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.backend.luis.usersapp.users_backend.entities.Producto;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoria(String categoria);
}
