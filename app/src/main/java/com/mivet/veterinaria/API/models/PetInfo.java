package com.mivet.veterinaria.API.models;

public class PetInfo {
    public String tipoAnimal;
    public String nombre;
    public String raza;
    public String fechaNacimiento;

    public PetInfo(String tipoAnimal, String nombre, String raza, String fechaNacimiento) {
        this.tipoAnimal = tipoAnimal;
        this.nombre = nombre;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
    }

    @Override
    public String toString() {
        return tipoAnimal + " - " + nombre + " - " + raza + " - " + fechaNacimiento;
    }
}
