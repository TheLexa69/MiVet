package com.mivet.veterinaria.API.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.retrofit.ApiClient;
import com.mivet.veterinaria.API.retrofit.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MascotaRepository {

    private final ApiService apiService;
    private final Context context;

    public MascotaRepository(Context context) {
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
            Log.e("MascotaRepository", "Error obteniendo token", e);
            return null;
        }
    }

    // Callback para lista de mascotas
    public interface MascotasCallback {
        void onSuccess(List<PetInfo> mascotas);
        void onFailure(Throwable t);
    }

    // Callback para una mascota individual
    public interface MascotaCallback {
        void onSuccess(PetInfo mascota);
        void onFailure(Throwable t);
    }

    // Callback para operaciones sin retorno
    public interface OperacionCallback {
        void onSuccess();
        void onFailure(Throwable t);
    }

    // Obtener todas las mascotas
    public void getTodasLasMascotas(MascotasCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.getTodasLasMascotas(token).enqueue(new Callback<List<PetInfo>>() {
            @Override
            public void onResponse(Call<List<PetInfo>> call, Response<List<PetInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Error al obtener mascotas. Código: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<PetInfo>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // Obtener una mascota por ID
    public void getMascotaPorId(Long id, MascotaCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.getMascotaPorId(token, id).enqueue(new Callback<PetInfo>() {
            @Override
            public void onResponse(Call<PetInfo> call, Response<PetInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Mascota no encontrada. Código: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<PetInfo> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // Eliminar una mascota
    public void eliminarMascota(Long id, OperacionCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.eliminarMascota(token, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(new Exception("Error al eliminar mascota. Código: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}
