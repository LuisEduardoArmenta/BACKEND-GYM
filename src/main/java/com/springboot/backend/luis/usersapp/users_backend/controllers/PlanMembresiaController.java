package com.springboot.backend.luis.usersapp.users_backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.springboot.backend.luis.usersapp.users_backend.entities.PlanMembresia;
import com.springboot.backend.luis.usersapp.users_backend.services.PlanMembresiaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/planes")
@CrossOrigin(originPatterns = "*")
public class PlanMembresiaController {

    @Autowired
    private PlanMembresiaService planService;

    @GetMapping
    public List<PlanMembresia> list() {
        return planService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return planService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PlanMembresia plan, BindingResult result) {
        if (result.hasErrors()) {
            return validar(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.save(plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody PlanMembresia plan, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }
        return planService.findById(id)
                .map(p -> {
                    p.setNombre(plan.getNombre());
                    p.setDuracion(plan.getDuracion());
                    p.setDescripcion(plan.getDescripcion());
                    p.setPrecio(plan.getPrecio());
                    return ResponseEntity.status(HttpStatus.CREATED).body(planService.save(p));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return planService.findById(id)
                .map(p -> {
                    planService.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
