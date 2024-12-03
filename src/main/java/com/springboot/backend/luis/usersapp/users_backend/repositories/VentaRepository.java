package com.springboot.backend.luis.usersapp.users_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.backend.luis.usersapp.users_backend.entities.Venta;

import java.util.Date;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByVendedor(String vendedor);
    List<Venta> findByFechaVentaBetween(Date inicio, Date fin);
}
