package com.springboot.backend.luis.usersapp.users_backend.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "cajas")
public class Caja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caja")
    private Long idCaja;
    
    @NotNull
    @Positive
    @Column(name = "monto_inicial")
    private BigDecimal montoInicial;
    
    @Column(name = "monto_final")
    private BigDecimal montoFinal;
    
    @Column(name = "fecha_apertura")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaApertura;
    
    @Column(name = "fecha_cierre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCierre;
    
    private String estado;
    
    private String observaciones;
    
    @OneToMany(mappedBy = "caja", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"caja"}, allowSetters = true)
    private List<MovimientoCaja> movimientos;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    // Getters y Setters
    public Long getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(Long idCaja) {
        this.idCaja = idCaja;
    }

    public BigDecimal getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(BigDecimal montoInicial) {
        this.montoInicial = montoInicial;
    }

    public BigDecimal getMontoFinal() {
        return montoFinal;
    }

    public void setMontoFinal(BigDecimal montoFinal) {
        this.montoFinal = montoFinal;
    }

    public Date getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(Date fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<MovimientoCaja> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoCaja> movimientos) {
        this.movimientos = movimientos;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
