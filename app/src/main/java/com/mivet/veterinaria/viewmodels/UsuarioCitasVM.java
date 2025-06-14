package com.mivet.veterinaria.viewmodels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mivet.veterinaria.API.models.Cita;
import com.mivet.veterinaria.API.repository.UsuarioRepository;

import java.util.List;

public class UsuarioCitasVM extends ViewModel {

    public MutableLiveData<List<Cita>> citasLD = new MutableLiveData<>();
    private final UsuarioRepository usuarioRepository;

    public UsuarioCitasVM(Context context) {
        this.usuarioRepository = new UsuarioRepository(context);
    }

    public void cargarCitas() {
        usuarioRepository.getCitas(new UsuarioRepository.CitasCallback() {
            @Override
            public void onSuccess(List<Cita> citas) {
                citasLD.postValue(citas);
            }

            @Override
            public void onFailure(Throwable t) {
                citasLD.postValue(null); // Puedes también usar otro LiveData para errores
            }
        });
    }
}
