package com.springboot.backend.luis.usersapp.users_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.springboot.backend.luis.usersapp.users_backend.entities.Producto;
import com.springboot.backend.luis.usersapp.users_backend.services.ProductoService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> listar() {
        return productoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> ver(@PathVariable Long id) {
        Optional<Producto> productoOpt = productoService.findById(id);
        if (productoOpt.isPresent()) {
            return ResponseEntity.ok(productoOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Producto producto, BindingResult result) {
        if (result.hasErrors()) {
            return validar(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.save(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Producto producto, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Producto> productoOpt = productoService.findById(id);
        if (productoOpt.isPresent()) {
            Producto productoDb = productoOpt.get();
            productoDb.setNombre(producto.getNombre());
            productoDb.setDescripcion(producto.getDescripcion());
            productoDb.setPrecio(producto.getPrecio());
            productoDb.setStock(producto.getStock());
            productoDb.setCategoria(producto.getCategoria());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(productoService.save(productoDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Producto> productoOpt = productoService.findById(id);
        if (productoOpt.isPresent()) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar/{nombre}")
    public List<Producto> buscarPorNombre(@PathVariable String nombre) {
        return productoService.findByNombre(nombre);
    }

    @GetMapping("/categoria/{categoria}")
    public List<Producto> buscarPorCategoria(@PathVariable String categoria) {
        return productoService.findByCategoria(categoria);
    }

    private ResponseEntity<?> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
