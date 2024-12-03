package com.springboot.backend.luis.usersapp.users_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.backend.luis.usersapp.users_backend.entities.Producto;
import com.springboot.backend.luis.usersapp.users_backend.repositories.ProductoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> findByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> findByCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public boolean actualizarStock(Long idProducto, int cantidad) {
        Optional<Producto> productoOpt = productoRepository.findById(idProducto);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            if (producto.getStock() >= cantidad) {
                producto.setStock(producto.getStock() - cantidad);
                productoRepository.save(producto);
                return true;
            }
        }
        return false;
    }
}
