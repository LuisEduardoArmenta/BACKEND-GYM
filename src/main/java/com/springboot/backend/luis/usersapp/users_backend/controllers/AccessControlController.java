package com.springboot.backend.luis.usersapp.users_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springboot.backend.luis.usersapp.users_backend.entities.AccessLog;
import com.springboot.backend.luis.usersapp.users_backend.entities.User;
import com.springboot.backend.luis.usersapp.users_backend.services.AccessLogService;
import com.springboot.backend.luis.usersapp.users_backend.services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/access")
@CrossOrigin(origins = {"http://localhost:4200"})
public class AccessControlController {

    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerAccess(@RequestBody Map<String, Object> accessData) {
        Integer userId = (Integer) accessData.get("userId");
        String accessType = (String) accessData.get("accessType");

        return userService.findById(userId)
            .map(user -> {
                AccessLog log = accessLogService.registerAccess(user, accessType);
                return ResponseEntity.ok().body((Object)log);
            })
            .orElse(ResponseEntity.badRequest()
                .body((Object)Map.of("error", "Usuario no encontrado")));
    }

    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<?> getUserLogs(@PathVariable Integer userId) {
        return userService.findById(userId)
            .map(user -> {
                List<AccessLog> logs = accessLogService.getUserAccessLogs(user);
                return ResponseEntity.ok().body((Object)logs);
            })
            .orElse(ResponseEntity.badRequest()
                .body((Object)Map.of("error", "Usuario no encontrado")));
    }

    @GetMapping("/logs/range")
    public ResponseEntity<?> getLogsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<AccessLog> logs = accessLogService.getAccessLogsByDateRange(start, end);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Formato de fecha inválido"));
        }
    }
}
