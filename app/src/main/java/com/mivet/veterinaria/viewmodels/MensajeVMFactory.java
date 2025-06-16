package com.mivet.veterinaria.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mivet.veterinaria.API.repository.UsuarioRepository;

public class MensajeVMFactory implements ViewModelProvider.Factory {

    private final UsuarioRepository usuarioRepository;

    public MensajeVMFactory(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MensajeVM.class)) {
            return (T) new MensajeVM(usuarioRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
