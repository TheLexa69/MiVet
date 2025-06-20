package com.mivet.veterinaria.notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class GestorNotificaciones {

    public static final String ID_CANAL = "canal_citas";

    /**
     * Crea el canal de notificaciones necesario para Android 8.0 (API 26) en adelante.
     * Este canal se usa para los recordatorios de citas.
     */
    public static void crearCanal(Context contexto) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "Recordatorios de citas";
            String descripcion = "Notificaciones que te recuerdan tus próximas citas";
            int importancia = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel canal = new NotificationChannel(ID_CANAL, nombre, importancia);
            canal.setDescription(descripcion);

            NotificationManager gestor = contexto.getSystemService(NotificationManager.class);
            if (gestor != null) {
                gestor.createNotificationChannel(canal);
            }
        }
    }
}
