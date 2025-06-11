package com.mivet.veterinaria.viewmodels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UsuarioVMFactory implements ViewModelProvider.Factory {

    private final Context context;

    public UsuarioVMFactory(Context context) {
        this.context = context.getApplicationContext(); // Evita memory leaks
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UsuarioVM(context);
    }
}
