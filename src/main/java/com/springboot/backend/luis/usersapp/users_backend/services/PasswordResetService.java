package com.springboot.backend.luis.usersapp.users_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.backend.luis.usersapp.users_backend.entities.User;

import java.util.Date;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void solicitarRecuperacion(String email) {
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        // Generar token temporal (válido por 24 horas)
        String token = generateToken();
        user.setResetToken(token);
        user.setResetTokenExpiry(new Date(System.currentTimeMillis() + 86400000));
        userService.save(user);
        
        // Enviar correo
        String resetUrl = "http://localhost:4200/reset-password?token=" + token;
        sendResetEmail(email, resetUrl);
    }
    
    public void resetPassword(String token, String newPassword) {
        User user = userService.findByResetToken(token)
            .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));
            
        if (user.getResetTokenExpiry().before(new Date())) {
            throw new RuntimeException("Token expirado");
        }
        
        // Asegurarse que la contraseña se encripte
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userService.save(user);
    }
    
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
    
    private void sendResetEmail(String email, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Recuperación de contraseña");
        message.setText("Para resetear tu contraseña, haz clic en el siguiente enlace: " + resetUrl);
        mailSender.send(message);
    }
}
