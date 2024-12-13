package com.springboot.backend.luis.usersapp.users_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.springboot.backend.luis.usersapp.users_backend.entities.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Map;
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
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token no proporcionado");
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        User user = userService.findByResetToken(token)
            .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));
            
        if (user.getResetTokenExpiry().before(new Date())) {
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userService.save(user);
            throw new RuntimeException("Token expirado. Solicita un nuevo enlace de recuperación");
        }
        
        // Agregar logs para debug
        System.out.println("=== DEBUG RESET PASSWORD ===");
        System.out.println("Usuario encontrado: " + user.getUsername());
        System.out.println("Password anterior hash: " + user.getPassword());
        
        // Encriptar y actualizar contraseña
        String encodedPassword = passwordEncoder.encode(newPassword);
        System.out.println("Nuevo password hash: " + encodedPassword);
        
        user.setPassword(encodedPassword);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        
        // Guardar usuario
        User savedUser = userService.save(user);
        System.out.println("Password guardado hash: " + savedUser.getPassword());
    }
    
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
    
    private void sendResetEmail(String email, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Recuperación de contraseña - ARGYM");
        message.setText("Hola,\n\n" +
            "Has solicitado restablecer tu contraseña.\n\n" +
            "Tu código de recuperación es:\n\n" +
            resetUrl.substring(resetUrl.indexOf("token=") + 6) + "\n\n" +
            "Ingresa este código en la página de recuperación de contraseña.\n\n" +
            "Este código expirará en 24 horas.\n\n" +
            "Si no solicitaste restablecer tu contraseña, puedes ignorar este correo.\n\n" +
            "Saludos,\n" +
            "Equipo ARGYM");
        
        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage());
        }
    }


    public void sendQrEmailWithAttachment(String email, byte[] qrImage) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    try {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(email);
        helper.setSubject("Tu Código QR de acceso - ARGYM");
        helper.setText("Hola,\n\n" +
            "Gracias por ser parte de la familia ARGYM.\n\n" +
            "Te presentamos tu forma de acceso al gimnasio:\n\n" +
            "Adjuntamos tu código QR para que acceses a nuestras instalaciones");

        // Adjuntar imagen QR
        helper.addAttachment("qr-code.png", new ByteArrayResource(qrImage));
        
        mailSender.send(mimeMessage);
    } catch (MessagingException e) {
        throw new RuntimeException("Error al enviar el correo: " + e.getMessage());
    }
}

}
