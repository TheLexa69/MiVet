package com.mivet.veterinaria.API.retrofit;

import com.mivet.veterinaria.API.dto.CambioContrasena;
import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    /*
    * USUARIO
    * */
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



    /*MASCOTAS*/
    // Obtener todas las mascotas (accesible por usuarios con permisos)
    @GET("/api/mascotas")
    Call<List<PetInfo>> getTodasLasMascotas(@Header("Authorization") String token);

    // Obtener mascota por ID
    @GET("/api/mascotas/{id}")
    Call<PetInfo> getMascotaPorId(@Header("Authorization") String token, @Path("id") Long id);

    // Eliminar mascota por ID
    @DELETE("/api/mascotas/mascotas/{id}")
    Call<Void> eliminarMascota(@Header("Authorization") String token, @Path("id") Long id);

    @GET("/api/mascotas/adopcion")
    Call<List<PetInfo>> getMascotasAdopcion(@Header("Authorization") String token);

    /*ADOPCIONES USUARIO*/
    @POST("/api/adopciones/{idMascota}/solicitar")
    Call<Void> solicitarAdopcion(
            @Header("Authorization") String token,
            @Path("idMascota") Long idMascota,
            @Query("mensaje") String mensaje
    );



    /*PROTECTORAS*/
    @PUT("/api/protectora/perfil")
    Call<Void> actualizarPerfilProtectora(
            @Header("Authorization") String token,
            @Body Protectora dto
    );

    // Obtener perfil de protectora
    @GET("/api/protectora/perfil")
    Call<Protectora> getPerfilProtectora(@Header("Authorization") String token);

    // Obtener mascotas de protectora
    @GET("/api/protectora/mascotas")
    Call<List<PetInfo>> getMascotasProtectora(@Header("Authorization") String token);

    // Guardar nueva mascota
    @POST("/api/protectora/mascotas")
    Call<PetInfo> crearMascotaProtectora(@Header("Authorization") String token, @Body PetInfo mascotaDTO);

    // Obtener solicitudes de adopción pendientes
    @GET("/api/adopciones/pendientes")
    Call<List<Adopcion>> getSolicitudesPendientes(@Header("Authorization") String token);

    // Cambiar estado de solicitud
    @PATCH("/api/adopciones/{id}/estado")
    Call<Adopcion> actualizarEstadoAdopcion(
            @Header("Authorization") String token,
            @Path("id") Long id,
            @Query("estado") String estado // debe ser "aceptada" o "rechazada"
    );
}
