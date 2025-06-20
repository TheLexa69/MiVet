package com.mivet.veterinaria.notificaciones;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mivet.veterinaria.API.models.Cita;
import com.mivet.veterinaria.helpers.FechaUtils;

import java.util.Date;

public class ProgramadorRecordatorios {

    /** Devuelve un PendingIntent único para una cita + offset (minutosAntes) */
    private static PendingIntent crearPI(Context ctx, Cita cita, int minutosAntes) {
        int codigo = (int) (cita.getId() * 10 + minutosAntes);   // único
        Intent i = new Intent(ctx, ReceptorRecordatorio.class);
        i.putExtra("ID_CITA",  cita.getId());
        i.putExtra("EMPRESA",  cita.getEmpresa());
        i.putExtra("FECHA",    FechaUtils.convertirAFormatoLeible(cita.getFecha()));
        return PendingIntent.getBroadcast(
                ctx, codigo, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    /** Programa las alarmas 60 min y 30 min antes de la cita */
    public static void programar(Context ctx, Cita cita) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        // VERIFICAR PERMISO EN ANDROID 12+ (API 31+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!am.canScheduleExactAlarms()) {
                // Opcional: Mostrar aviso o log
                return; // No tienes permiso, no programes la alarma
            }
        }

        Date fecha = FechaUtils.parsearDesdeISO(cita.getFecha());
        long tCita = fecha.getTime();

        for (int offset : new int[]{60, 30}) {
            long trigger = tCita - offset * 60_000L;
            if (trigger < System.currentTimeMillis()) continue;   // ya pasó
            PendingIntent pi = crearPI(ctx, cita, offset);
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pi);
        }
    }


    /** Cancela las dos alarmas de la cita (1 h / 30 min) */
    public static void cancelar(Context ctx, Cita cita) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        for (int offset : new int[]{60, 30}) {
            PendingIntent pi = crearPI(ctx, cita, offset);
            am.cancel(pi);
        }
    }
}
