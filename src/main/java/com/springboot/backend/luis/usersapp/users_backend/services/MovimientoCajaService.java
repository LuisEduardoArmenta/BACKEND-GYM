package com.springboot.backend.luis.usersapp.users_backend.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.luis.usersapp.users_backend.entities.Caja;
import com.springboot.backend.luis.usersapp.users_backend.entities.MovimientoCaja;
import com.springboot.backend.luis.usersapp.users_backend.repositories.CajaRepository;
import com.springboot.backend.luis.usersapp.users_backend.repositories.MovimientoCajaRepository;

@Service
public class MovimientoCajaService {

    @Autowired
    private MovimientoCajaRepository movimientoRepository;

    @Autowired
    private CajaRepository cajaRepository;

    @Transactional(readOnly = true)
    public List<MovimientoCaja> findAll() {
        List<MovimientoCaja> movimientos = movimientoRepository.findAll();
        movimientos.forEach(mov -> mov.getCaja().getIdCaja());
        return movimientos;
    }

    @Transactional(readOnly = true)
    public Optional<MovimientoCaja> findById(Long id) {
        return movimientoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<MovimientoCaja> findByIdCaja(Long id) {
        List<MovimientoCaja> movimientos = movimientoRepository.findByCajaIdCajaOrderByFechaMovimientoDesc(id);
        movimientos.forEach(mov -> mov.getCaja().getIdCaja());
        return movimientos;
    }

    @Transactional
    public MovimientoCaja save(MovimientoCaja movimiento, Long idCaja) {
        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));

        if (!"ABIERTA".equals(caja.getEstado())) {
            throw new RuntimeException("La caja está cerrada, no se pueden registrar movimientos");
        }

        movimiento.setCaja(caja);
        return movimientoRepository.save(movimiento);
    }

    @Transactional
    public void deleteById(Long id) {
        MovimientoCaja movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        if (!"ABIERTA".equals(movimiento.getCaja().getEstado())) {
            throw new RuntimeException("No se puede eliminar un movimiento de una caja cerrada");
        }

        movimientoRepository.deleteById(id);
    }

    @Transactional
    public MovimientoCaja update(MovimientoCaja movimiento, Long id) {
        return movimientoRepository.findById(id)
            .map(movDb -> {
                if (!"ABIERTA".equals(movDb.getCaja().getEstado())) {
                    throw new RuntimeException("No se puede modificar un movimiento de una caja cerrada");
                }
                movDb.setConcepto(movimiento.getConcepto());
                movDb.setMonto(movimiento.getMonto());
                return movimientoRepository.save(movDb);
            })
            .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));
    }

    @Transactional
    public MovimientoCaja registrarMovimiento(Long idCaja, MovimientoCaja movimiento) {
        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));

        if (!"ABIERTA".equals(caja.getEstado())) {
            throw new RuntimeException("No se pueden registrar movimientos en una caja cerrada");
        }

        movimiento.setCaja(caja);
        movimiento.setFechaMovimiento(new Date());
        return movimientoRepository.save(movimiento);
    }
}
