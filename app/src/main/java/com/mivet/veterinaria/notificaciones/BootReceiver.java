package com.mivet.veterinaria.notificaciones;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mivet.veterinaria.API.models.Cita;
import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.helpers.FechaUtils;

import java.util.Date;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            UsuarioRepository repo = new UsuarioRepository(context);
            repo.getCitas(new UsuarioRepository.CitasCallback() {
                @Override
                public void onSuccess(List<Cita> citas) {
                    for (Cita cita : citas) {
                        Date fechaCita = FechaUtils.parsearDesdeISO(cita.getFecha());
                        if (fechaCita != null && fechaCita.after(new Date())) {
                            ProgramadorRecordatorios.programar(context, cita);
                        }
                    }
                }

                @Override
                public void onFailure(Throwable error) {
                    Log.e("BootReceiver", "Error al recuperar citas: ", error);
                }

            });
        }
    }
}
