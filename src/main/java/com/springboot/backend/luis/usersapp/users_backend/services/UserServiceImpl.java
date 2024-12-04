package com.springboot.backend.luis.usersapp.users_backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.luis.usersapp.users_backend.entities.Role;
import com.springboot.backend.luis.usersapp.users_backend.entities.User;
import com.springboot.backend.luis.usersapp.users_backend.models.IUser;
import com.springboot.backend.luis.usersapp.users_backend.models.UserRequest;
import com.springboot.backend.luis.usersapp.users_backend.repositories.RoleRepository;
import com.springboot.backend.luis.usersapp.users_backend.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Un solo constructor con @Autowired
    @Autowired
    public UserServiceImpl(UserRepository repository, 
                        PasswordEncoder passwordEncoder, 
                        RoleRepository roleRepository) {  
        this.repository = repository;
        this.passwordEncoder = passwordEncoder; 
        this.roleRepository = roleRepository;  
    }
    


    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List) this.repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(@NonNull Integer id) {
        return repository.findById(id);
    }
    
    @Override
@Transactional(readOnly = true)
public Optional<User> findByUsername(String username) {
    return repository.findByUsername(username);
}

@Override
@Transactional
public User save(User user) {
    // Si es una actualización, asegurarse que la contraseña esté encriptada
    if (user.getId() != null && user.getPassword() != null) {
        System.out.println("Actualizando usuario existente: " + user.getUsername());
        System.out.println("Password a guardar: " + user.getPassword());
    }
    return repository.save(user);
}
    

    @Transactional
    @Override
    public Optional<User> update(UserRequest user, Integer id) {
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isPresent()) {
            User userDb = userOptional.get();
            userDb.setEmail(user.getEmail());
            userDb.setLastname(user.getLastname());
            userDb.setName(user.getName());
            userDb.setUsername(user.getUsername());
            

            userDb.setRoles(getRoles(user));
            return Optional.of(repository.save(userDb));
        }

        return Optional.empty();
    }


    @Transactional
    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }


    private List<Role> getRoles(IUser user) {
        List<Role> roles = new ArrayList<>();
        
        Role roleUser = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Role USER not found"));
        roles.add(roleUser);
        
        if (user.isAdmin()) {
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));
            roles.add(roleAdmin);
        }
        return roles;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByResetToken(String token) {
        return repository.findByResetToken(token);
    }

}
