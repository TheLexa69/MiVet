package com.mivet.veterinaria.viewmodels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UsuarioGastosVMFactory implements ViewModelProvider.Factory {

    private final Context context;

    public UsuarioGastosVMFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UsuarioGastosVM.class)) {
            return (T) new UsuarioGastosVM(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
