package com.mivet.veterinaria.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mivet.veterinaria.API.models.Mensaje;
import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.helpers.FechaUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MensajeVM extends ViewModel {

    private final MutableLiveData<List<Mensaje>> mensajesLiveData = new MutableLiveData<>();
    private final UsuarioRepository usuarioRepository;

    public MensajeVM(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        cargarMensajes(); // carga al crearse
    }

    public LiveData<List<Mensaje>> getMensajes() {
        return mensajesLiveData;
    }

    public void cargarMensajes() {
        usuarioRepository.getMensajes(new UsuarioRepository.MensajesCallback() {
            @Override
            public void onSuccess(List<Mensaje> mensajes) {
                // Ordena usando el Date real
                Collections.sort(mensajes, (m1, m2) -> {
                    Date fecha1 = FechaUtils.parsearDesdeISO(m1.getFechaEnvio());
                    Date fecha2 = FechaUtils.parsearDesdeISO(m2.getFechaEnvio());
                    return fecha2.compareTo(fecha1); // descendente
                });

                mensajesLiveData.postValue(mensajes);
            }


            @Override
            public void onFailure(Throwable t) {
                Log.e("MensajeVM", "Error cargando mensajes", t);
            }
        });
    }

    public void marcarComoLeido(long idMensaje) {
        usuarioRepository.marcarMensajeLeido(idMensaje, new UsuarioRepository.OperacionCallback() {
            @Override
            public void onSuccess() {
                List<Mensaje> actual = mensajesLiveData.getValue();
                if (actual != null) {
                    for (Mensaje m : actual) {
                        if (m.getId() == idMensaje) {
                            m.setLeido(true);
                            break;
                        }
                    }
                    mensajesLiveData.postValue(actual);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("MensajeVM", "Error al marcar mensaje como leído", t);
            }
        });
    }

}
