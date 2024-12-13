package com.springboot.backend.luis.usersapp.users_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springboot.backend.luis.usersapp.users_backend.services.PasswordResetService;

import java.util.Collections;

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    @PostMapping("/reset-request")
    public ResponseEntity<?> requestReset(@RequestParam String email) {
        try {
            passwordResetService.solicitarRecuperacion(email);
            return ResponseEntity.ok().body(Collections.singletonMap("mensaje", 
                "Se ha enviado un correo con las instrucciones"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", 
                e.getMessage()));
        }
    }
    
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestParam String token, 
                                         @RequestParam String newPassword) {
        try {
            passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok().body(Collections.singletonMap("mensaje", 
                "Contraseña actualizada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", 
                e.getMessage()));
        }
    }

    
}
