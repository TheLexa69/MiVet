package com.mivet.veterinaria.API.models;

import com.mivet.veterinaria.API.dto.PetInfo;

import java.util.List;

public class Usuario {
    private int id;
    private String nombre;
    private String correo;
    private String tipoUsuario;
    public String rol;
    private String contrasena;
    private List<PetInfo> mascotas;

    public Usuario(int id, String nombre, String correo, String tipoUsuario, String rol, String contrasena, List<PetInfo> mascotas) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.tipoUsuario = tipoUsuario;
        this.rol = rol;
        this.contrasena = contrasena;
        this.mascotas = mascotas;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
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
