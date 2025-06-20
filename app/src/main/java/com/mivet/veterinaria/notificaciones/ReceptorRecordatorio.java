package com.mivet.veterinaria.notificaciones;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mivet.veterinaria.R;

public class ReceptorRecordatorio extends BroadcastReceiver {

    @Override
    public void onReceive(Context contexto, Intent intent) {
        long idCita = intent.getLongExtra("ID_CITA", -1);
        String empresa = intent.getStringExtra("EMPRESA");
        String fecha = intent.getStringExtra("FECHA");

        // Crear el canal si aún no existe
        GestorNotificaciones.crearCanal(contexto);

        // Construir la notificación
        Notification notificacion = new NotificationCompat.Builder(contexto, GestorNotificaciones.ID_CANAL)
                .setSmallIcon(R.drawable.logo2)
                .setContentTitle("Recordatorio de cita")
                .setContentText("Tienes una cita en " + empresa + " a las " + fecha)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        // Verificar permisos (necesario desde Android 13+)
        if (android.os.Build.VERSION.SDK_INT < 33 ||
                ActivityCompat.checkSelfPermission(contexto, android.Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {

            NotificationManagerCompat.from(contexto).notify((int) idCita, notificacion);
        }else {
            Log.w("ReceptorRecordatorio", "Permiso de notificaciones no concedido. Notificación no mostrada.");
        }

    }
}
