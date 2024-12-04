package com.springboot.backend.luis.usersapp.users_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springboot.backend.luis.usersapp.users_backend.entities.User;
import com.springboot.backend.luis.usersapp.users_backend.entities.Membresia;
import com.springboot.backend.luis.usersapp.users_backend.services.UserService;
import com.springboot.backend.luis.usersapp.users_backend.services.MembresiaService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;

@RestController
@RequestMapping("/api/qr")
@CrossOrigin(origins = {"http://localhost:4200"})
public class QrController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MembresiaService membresiaService;
    
    @PostMapping("/generate/{userId}")
    public ResponseEntity<?> generateQr(@PathVariable Integer userId) {
        User user = userService.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        // Buscar membresía activa del usuario
        Optional<Membresia> membresia = membresiaService.findAll().stream()
            .filter(m -> m.getUsuario().getId().equals(userId) && 
                    "ACTIVO".equals(m.getEstadoMembresia()))
            .findFirst();
            
        Map<String, Object> qrData = new HashMap<>();
        qrData.put("id", user.getId());
        qrData.put("nombre", user.getUsername());
        qrData.put("email", user.getEmail());
        qrData.put("timestamp", new Date().getTime());
        
        if (membresia.isPresent()) {
            qrData.put("membresia", membresia.get().getPlan().getNombre());
            qrData.put("fechaExpiracion", membresia.get().getFechaExpiracion());
        }
        
        return ResponseEntity.ok(qrData);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateQr(@RequestBody Map<String, Object> qrData) {
        Integer userId = (Integer) qrData.get("id");
        return userService.findById(userId)
            .map(user -> {
                // Verificar membresía activa
                Optional<Membresia> membresia = membresiaService.findAll().stream()
                    .filter(m -> m.getUsuario().getId().equals(userId) && 
                            "ACTIVO".equals(m.getEstadoMembresia()))
                    .findFirst();
                
                if (membresia.isPresent()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("user", user);
                    response.put("membresia", membresia.get());
                    response.put("accessTime", new Date());
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("message", "Membresía inactiva o expirada"));
                }
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "QR inválido")));
    }
}
