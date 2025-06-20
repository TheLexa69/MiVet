package com.mivet.veterinaria.API.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.Cita;
import com.mivet.veterinaria.API.models.Gasto;
import com.mivet.veterinaria.API.models.Mensaje;
import com.mivet.veterinaria.API.models.Usuario;
import com.mivet.veterinaria.API.dto.CambioContrasena;
import com.mivet.veterinaria.API.retrofit.ApiClient;
import com.mivet.veterinaria.API.retrofit.ApiService;

import java.util.List;
import java.util.Map;

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

    public interface MensajesCallback {
        void onSuccess(List<Mensaje> mensajes);

        void onFailure(Throwable t);
    }

    public interface GastosCallback {
        void onSuccess(List<Gasto> gastos);

        void onFailure(Throwable t);
    }

    public interface CitasCallback {
        void onSuccess(List<Cita> citas);

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

    //USUARIO
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


    //MASCOTAS
    public void actualizarMascota(Long id, PetInfo mascota, OperacionCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.actualizarMascota(token, id, mascota).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(new Exception("Error al actualizar mascota"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void crearMascota(PetInfo mascota, OperacionCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.crearMascota(token, mascota).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(new Exception("Error al crear mascota"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

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

    //CITAS
    public void getCitas(CitasCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.getCitas(token).enqueue(new Callback<List<Cita>>() {
            @Override
            public void onResponse(Call<List<Cita>> call, Response<List<Cita>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Error al obtener citas"));
                }
            }

            @Override
            public void onFailure(Call<List<Cita>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void crearCita(Cita cita, OperacionCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.crearCita(token, cita).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(new Exception("Error al crear la cita"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void actualizarCita(Long id, Cita cita, OperacionCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.actualizarCita(token, id, cita).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(new Exception("Error al actualizar la cita"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void eliminarCita(Long id, OperacionCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.eliminarCita(token, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(new Exception("Error al eliminar la cita"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    //GASTOS
    public void filtrarGastos(String tipo, String dia, Integer mes, Integer anio, String desde, String hasta, GastosCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.filtrarGastos(token, tipo, dia, mes, anio, desde, hasta).enqueue(new Callback<List<Gasto>>() {
            @Override
            public void onResponse(Call<List<Gasto>> call, Response<List<Gasto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Error al filtrar gastos. código: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Gasto>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void cargarGastos(Map<String, String> filtros, GastosCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }
        apiService.filtrarGastos(token,
                        filtros.get("tipo"),
                        filtros.get("dia"),
                        filtros.get("mes") != null ? Integer.parseInt(filtros.get("mes")) : null,
                        filtros.get("anio") != null ? Integer.parseInt(filtros.get("anio")) : null,
                        filtros.get("desde"),
                        filtros.get("hasta"))
                .enqueue(new Callback<List<Gasto>>() {
                    @Override
                    public void onResponse(Call<List<Gasto>> call, Response<List<Gasto>> response) {
                        if (response.isSuccessful()) callback.onSuccess(response.body());
                        else callback.onFailure(new Exception("Error al obtener gastos. código: " + response.code()));
                    }

                    @Override
                    public void onFailure(Call<List<Gasto>> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    public void crearGasto(Gasto gasto, OperacionCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }
        apiService.crearGasto(token, gasto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess();
                else callback.onFailure(new Exception("Error al crear gasto. código: " + response.code()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void eliminarGasto(Long id, OperacionCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }
        apiService.eliminarGasto(token, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess();
                else callback.onFailure(new Exception("Error al eliminar gasto. código: " + response.code()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    //MENSAJES
    public void getMensajes(MensajesCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.getMensajes(token).enqueue(new Callback<List<Mensaje>>() {
            @Override
            public void onResponse(Call<List<Mensaje>> call, Response<List<Mensaje>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Error al obtener mensajes. código: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Mensaje>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void marcarMensajeLeido(Long id, OperacionCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onFailure(new Exception("Token no disponible"));
            return;
        }

        apiService.marcarMensajeLeido(token, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(new Exception("Error al marcar como leído. código: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


}
