package com.springboot.backend.luis.usersapp.users_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.luis.usersapp.users_backend.entities.Producto;
import com.springboot.backend.luis.usersapp.users_backend.entities.Venta;
import com.springboot.backend.luis.usersapp.users_backend.repositories.ProductoRepository;
import com.springboot.backend.luis.usersapp.users_backend.repositories.VentaRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> findById(Long id) {
        return ventaRepository.findById(id);
    }

    @Transactional
    public Venta save(Venta venta) {
        Producto producto = productoRepository.findById(venta.getProducto().getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getStock() < venta.getCantidad()) {
            throw new RuntimeException("Stock insuficiente");
        }

        // Calcular el total
        BigDecimal precio = producto.getPrecio();
        BigDecimal cantidad = new BigDecimal(venta.getCantidad());
        venta.setTotal(precio.multiply(cantidad));

        // Actualizar stock
        producto.setStock(producto.getStock() - venta.getCantidad());
        productoRepository.save(producto);

        return ventaRepository.save(venta);
    }

    @Transactional
    public Venta update(Long id, Venta venta) {
        Venta ventaExistente = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
        Producto producto = productoRepository.findById(venta.getProducto().getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Restaurar el stock anterior
        Producto productoAnterior = ventaExistente.getProducto();
        productoAnterior.setStock(productoAnterior.getStock() + ventaExistente.getCantidad());
        productoRepository.save(productoAnterior);

        // Verificar nuevo stock
        if (producto.getStock() < venta.getCantidad()) {
            throw new RuntimeException("Stock insuficiente");
        }

        // Actualizar la venta
        ventaExistente.setProducto(producto);
        ventaExistente.setCantidad(venta.getCantidad());
        ventaExistente.setVendedor(venta.getVendedor());

        // Recalcular el total
        BigDecimal precio = producto.getPrecio();
        BigDecimal cantidad = new BigDecimal(venta.getCantidad());
        ventaExistente.setTotal(precio.multiply(cantidad));

        // Actualizar nuevo stock
        producto.setStock(producto.getStock() - venta.getCantidad());
        productoRepository.save(producto);

        return ventaRepository.save(ventaExistente);
    }

    public List<Venta> findByVendedor(String vendedor) {
        return ventaRepository.findByVendedor(vendedor);
    }

    public List<Venta> findByFechas(Date inicio, Date fin) {
        return ventaRepository.findByFechaVentaBetween(inicio, fin);
    }

    @Transactional
    public void deleteById(Long id) {
        ventaRepository.deleteById(id);
    }
}
