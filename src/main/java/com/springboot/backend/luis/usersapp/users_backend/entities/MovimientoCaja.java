package com.springboot.backend.luis.usersapp.users_backend.entities;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "movimientos_caja")
public class MovimientoCaja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idMovimiento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "movimientos"})
    private Caja caja;
    
    @NotEmpty
    @Column(name = "tipo_movimiento")
    private String tipoMovimiento; // INGRESO, EGRESO
    
    @NotEmpty
    private String concepto;
    
    @NotNull
    @Positive
    private BigDecimal monto;
    
    @NotNull
    @Column(name = "fecha_movimiento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaMovimiento;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public Long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Caja getCaja() {
        return caja;
    }

    public void setCaja(Caja caja) {
        this.caja = caja;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Getters y Setters
    
}
