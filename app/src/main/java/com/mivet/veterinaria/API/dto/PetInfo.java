package com.mivet.veterinaria.API.dto;

public class PetInfo {
    public String id;
    public String tipo;
    public String nombre;
    public String raza;
    public String fechaNac;

    public PetInfo(String tipo, String nombre, String raza, String fechaNacimiento) {
        this.tipo = tipo;
        this.nombre = nombre;
        this.raza = raza;
        this.fechaNac = fechaNacimiento;
    }

    public PetInfo(String id, String tipo, String nombre, String raza, String fechaNac) {
        this.id = id;
        this.tipo = tipo;
        this.nombre = nombre;
        this.raza = raza;
        this.fechaNac = fechaNac;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    @Override
    public String toString() {
        return tipo + " - " + nombre + " - " + raza + " - " + fechaNac;
    }
}
