package com.mivet.veterinaria.helpers;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class UIHelper {

    public static void mostrarAlerta(Activity activity, String titulo, String mensaje, int color) {
        activity.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(titulo);
            builder.setMessage(mensaje);
            builder.setPositiveButton("OK", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
        });
    }
}
