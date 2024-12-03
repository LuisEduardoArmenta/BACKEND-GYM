package com.springboot.backend.luis.usersapp.users_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.springboot.backend.luis.usersapp.users_backend.entities.Venta;
import com.springboot.backend.luis.usersapp.users_backend.services.VentaService;

import jakarta.validation.Valid;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = {"http://localhost:4200"})
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public List<Venta> listar() {
        return ventaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> ver(@PathVariable Long id) {
        Optional<Venta> ventaOpt = ventaService.findById(id);
        if (ventaOpt.isPresent()) {
            return ResponseEntity.ok(ventaOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Venta venta, BindingResult result) {
        if (result.hasErrors()) {
            return validar(result);
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.save(venta));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Venta venta, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }
        
        try {
            return ResponseEntity.ok(ventaService.update(id, venta));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Venta> ventaOpt = ventaService.findById(id);
        if (ventaOpt.isPresent()) {
            ventaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/vendedor/{vendedor}")
    public List<Venta> buscarPorVendedor(@PathVariable String vendedor) {
        return ventaService.findByVendedor(vendedor);
    }

    private ResponseEntity<?> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
