package com.mivet.veterinaria.API.models;

import java.time.LocalDate;
import java.util.Date;

public class Gasto {
    private Long id;
    private Long idMascota;
    private String descripcion;
    private double cantidad;
    private String fecha;
    private TipoGasto tipo;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdMascota() { return idMascota; }
    public void setIdMascota(Long idMascota) { this.idMascota = idMascota; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public TipoGasto getTipo() { return tipo; }
    public void setTipo(TipoGasto tipo) { this.tipo = tipo; }
}
