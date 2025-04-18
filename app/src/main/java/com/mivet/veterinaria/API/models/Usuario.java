package com.mivet.veterinaria.API.models;

import com.mivet.veterinaria.API.dto.PetInfo;

import java.util.List;

public class Usuario {
    private String nombre;
    private String correo;
    private String contrasena;
    private List<PetInfo> mascotas;

    public Usuario(String nombre, String correo, String contrasena, List<PetInfo> mascotas) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.mascotas = mascotas;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public List<PetInfo> getMascotas() {
        return mascotas;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setMascotas(List<PetInfo> mascotas) {
        this.mascotas = mascotas;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", contrasena='" + contrasena + '\'' +
                ", mascotas=" + mascotas +
                '}';
    }
}
