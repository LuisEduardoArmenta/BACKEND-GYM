package com.springboot.backend.luis.usersapp.users_backend.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.springboot.backend.luis.usersapp.users_backend.entities.MovimientoCaja;
import com.springboot.backend.luis.usersapp.users_backend.services.MovimientoCajaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = {"http://localhost:4200"})
public class MovimientoCajaController {

    @Autowired
    private MovimientoCajaService movimientoService;

    @GetMapping
    public List<MovimientoCaja> listar() {
        return movimientoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> ver(@PathVariable Long id) {
        return movimientoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/caja/{idCaja}")
    public List<MovimientoCaja> listarPorCaja(@PathVariable Long idCaja) {
        return movimientoService.findByIdCaja(idCaja);
    }

    @PostMapping("/{idCaja}/movimientos")
    public ResponseEntity<?> registrarMovimiento(
            @PathVariable Long idCaja,
            @Valid @RequestBody MovimientoCaja movimiento,
            BindingResult result) {
        
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> {
                errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errores);
        }

        try {
            MovimientoCaja nuevoMovimiento = movimientoService.registrarMovimiento(idCaja, movimiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMovimiento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            movimientoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }
}
