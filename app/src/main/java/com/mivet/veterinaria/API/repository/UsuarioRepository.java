package com.mivet.veterinaria.API.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.Usuario;
import com.mivet.veterinaria.API.models.CambioContrasena;
import com.mivet.veterinaria.API.retrofit.ApiClient;
import com.mivet.veterinaria.API.retrofit.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioRepository {

    private final ApiService apiService;
    private final Context context;

    public interface UsuarioCallback {
        void onSuccess(Usuario usuario);
        void onFailure(Throwable t);
    }

    public interface OperacionCallback {
        void onSuccess();
        void onFailure(Throwable t);
    }

    // Callback específico para la lista de mascotas
    public interface MascotasCallback {
        void onSuccess(java.util.List<PetInfo> mascotas);
        void onFailure(Throwable t);
    }

    public UsuarioRepository(Context context) {
        this.context = context;
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    private String getToken() {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String token = sharedPreferences.getString("TOKEN", null);
            if (token == null) throw new Exception("Token no encontrado");
            return "Bearer " + token;

        } catch (Exception e) {
            Log.e("UsuarioRepository", "Error obteniendo token", e);
            return null;
        }
    }

    // Obtener perfil
    public void getPerfil(UsuarioCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.getPerfil(token).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Error al obtener perfil"));
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // Obtener mascotas
    public void getMascotas(MascotasCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.getMascotas(token).enqueue(new Callback<List<PetInfo>>() {
            @Override
            public void onResponse(Call<List<com.mivet.veterinaria.API.dto.PetInfo>> call, Response<List<com.mivet.veterinaria.API.dto.PetInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Error al obtener mascotas. Código: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<com.mivet.veterinaria.API.dto.PetInfo>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // Actualizar perfil
    public void actualizarPerfil(Usuario usuario, OperacionCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.updatePerfil(token, usuario).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(new Exception("Error al actualizar perfil"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // Cambiar contraseña
    public void cambiarContrasena(CambioContrasena dto, OperacionCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.cambiarContrasena(token, dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(new Exception("Error al cambiar contraseña"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}
