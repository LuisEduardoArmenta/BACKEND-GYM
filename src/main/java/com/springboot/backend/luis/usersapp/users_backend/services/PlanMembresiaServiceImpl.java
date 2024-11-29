package com.springboot.backend.luis.usersapp.users_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.luis.usersapp.users_backend.entities.PlanMembresia;
import com.springboot.backend.luis.usersapp.users_backend.repositories.PlanMembresiaRepository;

@Service
public class PlanMembresiaServiceImpl implements PlanMembresiaService {

    @Autowired
    private PlanMembresiaRepository planMembresiaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PlanMembresia> findAll() {
        return planMembresiaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlanMembresia> findById(Long id) {
        return planMembresiaRepository.findById(id);
    }

    @Override
    @Transactional
    public PlanMembresia save(PlanMembresia planMembresia) {
        return planMembresiaRepository.save(planMembresia);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        planMembresiaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlanMembresia> findByDuracion(String duracion) {
        return planMembresiaRepository.findByDuracion(duracion);
    }
}
