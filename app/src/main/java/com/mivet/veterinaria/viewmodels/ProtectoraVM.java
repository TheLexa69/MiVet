package com.mivet.veterinaria.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.Protectora;
import com.mivet.veterinaria.API.models.Adopcion;
import com.mivet.veterinaria.API.repository.ProtectoraRepository;
import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.API.retrofit.ApiService;
import com.mivet.veterinaria.helpers.SesionUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProtectoraVM extends ViewModel {

    public MutableLiveData<Protectora> protectoraLD = new MutableLiveData<>();
    public MutableLiveData<List<PetInfo>> mascotasLD = new MutableLiveData<>();
    public MutableLiveData<List<Adopcion>> solicitudesLD = new MutableLiveData<>();

    private final ProtectoraRepository protectoraRepository;

    public ProtectoraVM(Context context) {
        this.protectoraRepository = new ProtectoraRepository(context);
    }

    public void actualizarPerfil(Protectora protectora, Runnable onSuccess, java.util.function.Consumer<Throwable> onError) {
        protectoraRepository.actualizarPerfil(protectora, new ProtectoraRepository.ProtectoraCallback() {
            @Override
            public void onSuccess(Protectora p) {
                protectoraLD.postValue(p);
                onSuccess.run();
            }

            @Override
            public void onFailure(Throwable t) {
                onError.accept(t);
            }
        });
    }

    public void cargarPerfil() {
        protectoraRepository.obtenerPerfil(new ProtectoraRepository.ProtectoraCallback() {
            @Override
            public void onSuccess(Protectora protectora) {
                protectoraLD.postValue(protectora);
            }

            @Override
            public void onFailure(Throwable t) {
                // Manejo de errores si quieres añadir MutableLiveData<Throwable>
            }
        });
    }

    public void cargarMascotas() {
        protectoraRepository.getMascotasProtectora(new ProtectoraRepository.ListaCallback<PetInfo>() {
            @Override
            public void onSuccess(List<PetInfo> lista) {
                mascotasLD.postValue(lista);
            }

            @Override
            public void onFailure(Throwable t) {
                // Log o manejo de error
            }
        });
    }

    public void cargarSolicitudesPendientes() {
        protectoraRepository.getSolicitudesPendientes(new ProtectoraRepository.ListaCallback<Adopcion>() {
            @Override
            public void onSuccess(List<Adopcion> lista) {
                Log.d("VM", "Solicitudes recibidas: " + lista.size());
                solicitudesLD.postValue(lista);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("VM", "Error al cargar solicitudes", t);
            }
        });
    }

    public void actualizarEstadoAdopcion(Long id, String estado, Runnable onSuccess, java.util.function.Consumer<Throwable> onError) {
        protectoraRepository.actualizarEstadoAdopcion(id, estado, new Callback<Adopcion>() {
            @Override
            public void onResponse(Call<Adopcion> call, Response<Adopcion> response) {
                if (response.isSuccessful()) {
                    onSuccess.run();
                } else {
                    onError.accept(new Exception("Error al actualizar estado"));
                }
            }

            @Override
            public void onFailure(Call<Adopcion> call, Throwable t) {
                onError.accept(t);
            }
        });
    }

    public void crearMascota(PetInfo mascota, ProtectoraRepository.OperacionCallback callback) {
        protectoraRepository.crearMascota(mascota, new ProtectoraRepository.OperacionCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }

}
