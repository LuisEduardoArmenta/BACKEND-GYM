package com.springboot.backend.luis.usersapp.users_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.luis.usersapp.users_backend.entities.Membresia;
import com.springboot.backend.luis.usersapp.users_backend.repositories.MembresiaRepository;

@Service
public class MembresiaServiceImpl implements MembresiaService {

    @Autowired
    private MembresiaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Membresia> findAll() {
        return (List<Membresia>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Membresia> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Membresia save(Membresia membresia) {
        return repository.save(membresia);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<Membresia> update(Membresia membresia, Long id) {
        Optional<Membresia> o = this.findById(id);
        if (o.isPresent()) {
            Membresia membresiaDb = o.get();
            membresiaDb.setUsuario(membresia.getUsuario());
            membresiaDb.setPlan(membresia.getPlan());
            membresiaDb.setFechaInicio(membresia.getFechaInicio());
            membresiaDb.setFechaExpiracion(membresia.getFechaExpiracion());
            membresiaDb.setEstadoMembresia(membresia.getEstadoMembresia());
            return Optional.of(repository.save(membresiaDb));
        }
        return Optional.empty();
    }
}
