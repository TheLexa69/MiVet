package com.mivet.veterinaria.API.retrofit;

import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // PERFIL
    @GET("/api/usuario/perfil")
    Call<Usuario> getPerfil(@Header("Authorization") String token);

    @PUT("/api/usuario/perfil")
    Call<Void> updatePerfil(@Header("Authorization") String token, @Body Usuario dto);

    // MASCOTAS
    @GET("/api/usuario/mascotas")
    Call<List<PetInfo>> getMascotas(@Header("Authorization") String token);

    @POST("/api/usuario/mascotas")
    Call<Void> crearMascota(@Header("Authorization") String token, @Body PetInfo dto);

    @PUT("/api/usuario/mascotas/{id}")
    Call<Void> actualizarMascota(@Header("Authorization") String token, @Path("id") Long id, @Body PetInfo dto);

    // CITAS
    @GET("/api/usuario/citas")
    Call<List<Cita>> getCitas(@Header("Authorization") String token);

    @POST("/api/usuario/citas")
    Call<Void> crearCita(@Header("Authorization") String token, @Body Cita dto);

    @PUT("/api/usuario/citas/{id}")
    Call<Void> actualizarCita(@Header("Authorization") String token, @Path("id") Long id, @Body Cita dto);

    @DELETE("/api/usuario/citas/{id}")
    Call<Void> eliminarCita(@Header("Authorization") String token, @Path("id") Long id);

    // GASTOS
    @GET("/api/usuario/gastos")
    Call<List<Gasto>> filtrarGastos(
            @Header("Authorization") String token,
            @Query("tipo") String tipo,
            @Query("dia") String dia,
            @Query("mes") Integer mes,
            @Query("anio") Integer anio,
            @Query("desde") String desde,
            @Query("hasta") String hasta
    );

    @POST("/api/usuario/gastos")
    Call<Void> crearGasto(@Header("Authorization") String token, @Body Gasto dto);

    @DELETE("/api/usuario/gastos/{id}")
    Call<Void> eliminarGasto(@Header("Authorization") String token, @Path("id") Long id);

    // CONTRASEÑA
    @PUT("/api/usuario/cambiar-contrasena")
    Call<Void> cambiarContrasena(@Header("Authorization") String token, @Body CambioContrasena dto);

    // MENSAJES
    @GET("/api/usuario/mensajes")
    Call<List<Mensaje>> getMensajes(@Header("Authorization") String token);

    @PUT("/api/usuario/mensajes/{id}/leido")
    Call<Void> marcarMensajeLeido(@Header("Authorization") String token, @Path("id") Long id);
}
