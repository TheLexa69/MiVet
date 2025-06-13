package com.mivet.veterinaria.viewmodels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.Usuario;
import com.mivet.veterinaria.API.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

public class UsuarioVM extends ViewModel {

    public MutableLiveData<Usuario> usuarioLD = new MutableLiveData<>();
    public MutableLiveData<List<PetInfo>> mascotasLD = new MutableLiveData<>();

    private final UsuarioRepository usuarioRepository;

    public UsuarioVM(Context context) {
        this.usuarioRepository = new UsuarioRepository(context);
    }

    public void cargarPerfil() {
        usuarioRepository.getPerfil(new UsuarioRepository.UsuarioCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                usuarioLD.postValue(usuario);
            }

            @Override
            public void onFailure(Throwable t) {
                // Manejo de errores si quieres añadir MutableLiveData<Throwable>
            }
        });
    }

    public void cargarMascotas() {
        usuarioRepository.getMascotas(new UsuarioRepository.MascotasCallback() {
            @Override
            public void onSuccess(List<PetInfo> mascotas) {
                mascotasLD.postValue(mascotas != null ? mascotas : new ArrayList<>());
            }

            @Override
            public void onFailure(Throwable t) {
                // puedes loguear o manejar el error aquí
            }
        });
    }


}
