package com.mivet.veterinaria.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.mivet.veterinaria.MainActivity;

import java.util.Map;

public class SesionUtils {
    private static final String PREFS_NAME = "secure_prefs";

    public static void logCredenciales(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            Log.d("SesionUtils", "📦 Dump EncryptedSharedPreferences:");
            for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
                Log.d("SesionUtils", "🧩 Clave: " + entry.getKey() + " => Valor: " + entry.getValue());
            }

        } catch (Exception e) {
            Log.e("SesionUtils", "❌ Error al acceder a EncryptedSharedPreferences", e);
        }
    }

    public static String obtenerToken(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            return sharedPreferences.getString("TOKEN", null);
        } catch (Exception e) {
            Log.e("SesionUtils", "❌ No se pudo obtener el token", e);
            return null;
        }
    }

    public static long obtenerUsuarioId(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences prefs = EncryptedSharedPreferences.create(
                    context,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            Map<String, ?> allPrefs = prefs.getAll();
            Object userIdRaw = allPrefs.get("USER_ID");

            if (userIdRaw != null) {
                Log.d("SesionUtils", "🔍 USER_ID type = " + userIdRaw.getClass().getName() + " | value = " + userIdRaw.toString());

                if (userIdRaw instanceof Integer) {
                    return ((Integer) userIdRaw).longValue();
                } else if (userIdRaw instanceof Long) {
                    return (Long) userIdRaw;
                } else if (userIdRaw instanceof String) {
                    try {
                        return Long.parseLong((String) userIdRaw);
                    } catch (NumberFormatException e) {
                        Log.e("SesionUtils", "❌ USER_ID en String no convertible a long: " + userIdRaw);
                    }
                } else {
                    Log.w("SesionUtils", "❗ Tipo inesperado para USER_ID: " + userIdRaw.getClass().getName());
                }
            } else {
                Log.w("SesionUtils", "⚠️ USER_ID no encontrado en preferencias");
            }

        } catch (Exception e) {
            Log.e("SesionUtils", "❌ Error al leer USER_ID", e);
        }

        return -1; // valor por defecto en caso de error
    }



    public static void cerrarSesion(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences securePrefs = EncryptedSharedPreferences.create(
                    context,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            SharedPreferences.Editor editor = securePrefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

        } catch (Exception e) {
            Log.e("SesionUtils", "Error al cerrar sesión", e);
        }
    }
}
