package com.mivet.veterinaria.API.repository;

import android.content.Context;
import android.util.Log;

import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.Adopcion;
import com.mivet.veterinaria.API.retrofit.ApiService;
import com.mivet.veterinaria.API.retrofit.ApiClient;
import com.mivet.veterinaria.API.models.Protectora;
import com.mivet.veterinaria.helpers.SesionUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProtectoraRepository {
    private final ApiService apiService;
    private final Context context;

    public interface ProtectoraCallback {
        void onSuccess(Protectora protectora);
        void onFailure(Throwable t);
    }
    public interface ListaCallback<T> {
        void onSuccess(List<T> lista);
        void onFailure(Throwable t);
    }
    public interface OperacionCallback {
        void onSuccess();
        void onFailure(Throwable t);
    }


    public ProtectoraRepository(Context context) {
        this.context = context;
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    public void actualizarPerfil(Protectora protectora, ProtectoraCallback callback) {
        String token = "Bearer " + SesionUtils.obtenerToken(context);

        apiService.actualizarPerfilProtectora(token, protectora).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Idealmente recargar el perfil luego de guardar
                    obtenerPerfil(callback);
                } else {
                    callback.onFailure(new Exception("Error al actualizar el perfil de la protectora"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void obtenerPerfil(ProtectoraCallback callback) {
        String token = "Bearer " + SesionUtils.obtenerToken(context);

        apiService.getPerfilProtectora(token).enqueue(new Callback<Protectora>() {
            @Override
            public void onResponse(Call<Protectora> call, Response<Protectora> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Error al obtener el perfil de la protectora"));
                }
            }

            @Override
            public void onFailure(Call<Protectora> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void getMascotasProtectora(ListaCallback<PetInfo> callback) {
        String token = "Bearer " + SesionUtils.obtenerToken(context);
        apiService.getMascotasProtectora(token).enqueue(new Callback<List<PetInfo>>() {
            @Override
            public void onResponse(Call<List<PetInfo>> call, Response<List<PetInfo>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Error al obtener mascotas de la protectora"));
                }
            }

            @Override
            public void onFailure(Call<List<PetInfo>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void getSolicitudesPendientes(ListaCallback<Adopcion> callback) {
        String token = "Bearer " + SesionUtils.obtenerToken(context);

        apiService.getSolicitudesPendientes(token).enqueue(new Callback<List<Adopcion>>() {
            @Override
            public void onResponse(Call<List<Adopcion>> call, Response<List<Adopcion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ADOPCIONES", "Cargadas " + response.body().size() + " solicitudes");
                    callback.onSuccess(response.body());
                } else {
                    Log.e("ADOPCIONES", "Respuesta vacía o incorrecta: " + response.code());
                    callback.onFailure(new Exception("Error al obtener solicitudes"));
                }
            }

            @Override
            public void onFailure(Call<List<Adopcion>> call, Throwable t) {
                Log.e("ADOPCIONES", "Error de red", t);
                callback.onFailure(t);
            }
        });
    }

    public void actualizarEstadoAdopcion(Long id, String estado, Callback<Adopcion> callback) {
        String token = "Bearer " + SesionUtils.obtenerToken(context);
        apiService.actualizarEstadoAdopcion(token, id, estado).enqueue(callback);
    }

    public void crearMascota(PetInfo mascota, OperacionCallback callback) {
        String token = "Bearer " + SesionUtils.obtenerToken(context);
        apiService.crearMascotaProtectora(token, mascota).enqueue(new Callback<PetInfo>() {
            @Override
            public void onResponse(Call<PetInfo> call, Response<PetInfo> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    String errorMsg = "Error al crear mascota. Código: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " | " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMsg += " | (No se pudo leer el cuerpo del error)";
                    }

                    Log.e("CREAR_MASCOTA", errorMsg); // Añade este log para ver el error exacto
                    callback.onFailure(new Exception(errorMsg));
                }
            }


            @Override
            public void onFailure(Call<PetInfo> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }




}
