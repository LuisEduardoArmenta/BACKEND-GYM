package com.springboot.backend.luis.usersapp.users_backend.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.springboot.backend.luis.usersapp.users_backend.entities.Caja;
import com.springboot.backend.luis.usersapp.users_backend.entities.MovimientoCaja;
import com.springboot.backend.luis.usersapp.users_backend.services.CajaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/caja")
@CrossOrigin(origins = {"http://localhost:4200"})
public class CajaController {

    @Autowired
    private CajaService cajaService;

    @GetMapping("/actual")
    public ResponseEntity<?> getCajaActual() {
        return ResponseEntity.ok(cajaService.getCajaActual());
    }

    @PostMapping("/abrir")
    public ResponseEntity<?> abrirCaja(@Valid @RequestBody Caja caja, BindingResult result) {
        if (result.hasErrors()) {
            return validar(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(cajaService.abrirCaja(caja));
    }

    @PutMapping("/cerrar/{id}")
    public ResponseEntity<?> cerrarCaja(@PathVariable Long id) {
        cajaService.cerrarCaja(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idCaja}/movimientos")
    public ResponseEntity<?> getMovimientos(@PathVariable Long idCaja) {
        return ResponseEntity.ok(cajaService.getMovimientos(idCaja));
    }

    @PostMapping("/{idCaja}/movimientos")
    public ResponseEntity<?> registrarMovimiento(
            @PathVariable Long idCaja,
            @Valid @RequestBody MovimientoCaja movimiento,
            BindingResult result) {
        
        if (result.hasErrors()) {
            return validar(result);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cajaService.registrarMovimiento(idCaja, movimiento));
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(cajaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> ver(@PathVariable Long id) {
        return ResponseEntity.ok(cajaService.findById(id));
    }

    private ResponseEntity<?> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
