package com.springboot.backend.luis.usersapp.users_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.backend.luis.usersapp.users_backend.entities.Objetivo;
import com.springboot.backend.luis.usersapp.users_backend.repositories.ObjetivoRepository;

@Service
public class ObjetivoService {

  
    @Autowired
    private ObjetivoRepository objetivoRepository;
    
    public List<Objetivo> findByUserId(Long userId) {
        return objetivoRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public Objetivo save(Objetivo objetivo) {
        return objetivoRepository.save(objetivo);
    }
    
    public void deleteById(Long id) {
        objetivoRepository.deleteById(id);
    }
    
    public Optional<Objetivo> findById(Long id) {
        return objetivoRepository.findById(id);
    }
    
    public void actualizarProgreso(Long id, Integer progreso) {
        objetivoRepository.findById(id).ifPresent(objetivo -> {
            objetivo.setProgreso(progreso);
            objetivo.setCompletado(progreso >= 100);
            objetivoRepository.save(objetivo);
        });
    }
}
