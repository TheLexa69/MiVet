package com.mivet.veterinaria.viewmodels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProtectoraVMFactory implements ViewModelProvider.Factory {

    private final Context context;

    public ProtectoraVMFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ProtectoraVM(context);
    }
}
