package com.springboot.backend.luis.usersapp.users_backend.services;

import java.math.BigDecimal;
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
public class CajaService {

    @Autowired
    private CajaRepository cajaRepository;

    @Autowired
    private MovimientoCajaRepository movimientoCajaRepository;

    public Optional<Caja> getCajaActual() {
        return cajaRepository.findFirstByEstadoOrderByFechaAperturaDesc("ABIERTA");
    }

    @Transactional
    public Caja abrirCaja(Caja caja) {
        if (getCajaActual().isPresent()) {
            throw new RuntimeException("Ya existe una caja abierta");
        }

        caja.setEstado("ABIERTA");
        caja.setFechaApertura(new Date());
        return cajaRepository.save(caja);
    }

    @Transactional
    public void cerrarCaja(Long id) {
        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));

        if (!"ABIERTA".equals(caja.getEstado())) {
            throw new RuntimeException("La caja ya está cerrada");
        }

        caja.setEstado("CERRADA");
        caja.setFechaCierre(new Date());
        
        // Calcular monto final
        BigDecimal montoFinal = calcularMontoFinal(caja);
        caja.setMontoFinal(montoFinal);
        
        cajaRepository.save(caja);
    }

    public List<MovimientoCaja> getMovimientos(Long idCaja) {
        return movimientoCajaRepository.findByCajaIdCajaOrderByFechaMovimientoDesc(idCaja);
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
        return movimientoCajaRepository.save(movimiento);
    }

    private BigDecimal calcularMontoFinal(Caja caja) {
        BigDecimal montoInicial = caja.getMontoInicial();
        List<MovimientoCaja> movimientos = getMovimientos(caja.getIdCaja());
        
        return movimientos.stream()
                .reduce(montoInicial,
                        (subtotal, mov) -> "INGRESO".equals(mov.getTipoMovimiento()) ?
                                subtotal.add(mov.getMonto()) :
                                subtotal.subtract(mov.getMonto()),
                        BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public List<Caja> findAll() {
        return cajaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Caja> findById(Long id) {
        return cajaRepository.findById(id);
    }

    @Transactional
    public Caja update(Caja caja, Long id) {
        return cajaRepository.findById(id)
            .map(cajaDb -> {
                cajaDb.setObservaciones(caja.getObservaciones());
                // No permitimos actualizar montos ni estados por seguridad
                return cajaRepository.save(cajaDb);
            })
            .orElseThrow(() -> new RuntimeException("Caja no encontrada"));
    }

    @Transactional
    public void deleteById(Long id) {
        Caja caja = cajaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Caja no encontrada"));
        
        if ("ABIERTA".equals(caja.getEstado())) {
            throw new RuntimeException("No se puede eliminar una caja abierta");
        }
        cajaRepository.deleteById(id);
    }

    // Otros métodos útiles...
}
