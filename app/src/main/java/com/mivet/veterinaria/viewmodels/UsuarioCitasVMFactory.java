package com.mivet.veterinaria.viewmodels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UsuarioCitasVMFactory implements ViewModelProvider.Factory {
    private final Context context;

    public UsuarioCitasVMFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UsuarioCitasVM(context);
    }
}
