package com.springboot.backend.luis.usersapp.users_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.springboot.backend.luis.usersapp.users_backend.entities.Objetivo;
import com.springboot.backend.luis.usersapp.users_backend.entities.User;
import com.springboot.backend.luis.usersapp.users_backend.services.ObjetivoService;
import com.springboot.backend.luis.usersapp.users_backend.services.UserService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/objetivos")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ObjetivoController {
    
    @Autowired
    private ObjetivoService objetivoService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long userId, Authentication authentication) {
        System.out.println("==== DEBUG OBJETIVO CONTROLLER ====");
        System.out.println("UserId solicitado: " + userId);
        System.out.println("Authentication: " + authentication);
        
        if (authentication == null) {
            System.out.println("No hay autenticación");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("UserDetails username: " + userDetails.getUsername());
        System.out.println("UserDetails authorities: " + userDetails.getAuthorities());
        
        User user = userService.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        System.out.println("User ID from DB: " + user.getId());
        
        if (!user.getId().equals(userId)) {
            System.out.println("IDs no coinciden");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(objetivoService.findByUserId(userId));
    }
    
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Objetivo objetivo, BindingResult result, Authentication authentication) {
        if(result.hasErrors()) {
            return validar(result);
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
        objetivo.setUser(user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(objetivoService.save(objetivo));
    }
    
    @PutMapping("/{id}/progreso")
    public ResponseEntity<?> actualizarProgreso(@PathVariable Long id, @RequestBody Integer progreso) {
        objetivoService.actualizarProgreso(id, progreso);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        objetivoService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<?> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}