package com.springboot.backend.luis.usersapp.users_backend.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.springboot.backend.luis.usersapp.users_backend.entities.MovimientoCaja;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findByCajaIdCajaOrderByFechaMovimientoDesc(Long idCaja);
}
