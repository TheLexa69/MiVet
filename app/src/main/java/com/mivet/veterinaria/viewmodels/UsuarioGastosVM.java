package com.mivet.veterinaria.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mivet.veterinaria.API.models.Gasto;
import com.mivet.veterinaria.API.repository.UsuarioRepository;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class UsuarioGastosVM extends ViewModel {

    private final UsuarioRepository repository;
    private final MutableLiveData<List<Gasto>> gastosLD = new MutableLiveData<>();

    public UsuarioGastosVM(Context context) {
        this.repository = new UsuarioRepository(context);
    }

    public LiveData<List<Gasto>> getGastos() {
        return gastosLD;
    }

    public void cargarGastosDelMes(int mes, int anio) {
        repository.filtrarGastos(null, null, mes, anio, null, null, new UsuarioRepository.GastosCallback() {
            @Override
            public void onSuccess(List<Gasto> gastos) {
                if (gastos != null) {
                    // Ordenar por fecha descendente
                    Collections.sort(gastos, (g1, g2) -> g2.getFecha().compareTo(g1.getFecha()));
                }
                gastosLD.postValue(gastos);
            }

            @Override
            public void onFailure(Throwable t) {
                gastosLD.postValue(null);
            }
        });
    }

    public void cargarTodosLosGastos() {
        repository.filtrarGastos(null, null, null, null, null, null, new UsuarioRepository.GastosCallback() {
            @Override
            public void onSuccess(List<Gasto> gastos) {
                if (gastos != null) {
                    // Ordenar por fecha descendente
                    Collections.sort(gastos, (g1, g2) -> g2.getFecha().compareTo(g1.getFecha()));
                }
                gastosLD.postValue(gastos);
            }

            @Override
            public void onFailure(Throwable t) {
                gastosLD.postValue(null);
            }
        });
    }

    public void filtrarGastos(String tipo, String dia, Integer mes, Integer anio, String desde, String hasta) {
        repository.filtrarGastos(tipo, dia, mes, anio, desde, hasta, new UsuarioRepository.GastosCallback() {
            @Override
            public void onSuccess(List<Gasto> gastos) {
                if (gastos != null) {
                    Collections.sort(gastos, (g1, g2) -> g2.getFecha().compareTo(g1.getFecha()));
                }
                gastosLD.postValue(gastos);
            }

            @Override
            public void onFailure(Throwable t) {
                gastosLD.postValue(null);
            }
        });
    }


}
