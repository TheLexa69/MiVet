package com.mivet.veterinaria.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mivet.veterinaria.API.dto.CambioContrasena;
import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.R;

public class CambiarContrasenaDialog extends DialogFragment {

    private UsuarioRepository usuarioRepository;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = requireContext();
        usuarioRepository = new UsuarioRepository(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cambiar contraseña");

        // Inflar vista personalizada
        final android.view.View view = getLayoutInflater().inflate(R.layout.dialog_cambiar_contrasena, null);
        EditText etActual = view.findViewById(R.id.etActual);
        EditText etNueva = view.findViewById(R.id.etNueva);
        EditText etConfirmar = view.findViewById(R.id.etConfirmar);
        Button btnCambiar = view.findViewById(R.id.btnCambiar);

        builder.setView(view);

        AlertDialog dialog = builder.create();

        btnCambiar.setOnClickListener(v -> {
            String actual = etActual.getText().toString().trim();
            String nueva = etNueva.getText().toString().trim();
            String confirmar = etConfirmar.getText().toString().trim();

            if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!nueva.equals(confirmar)) {
                Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            CambioContrasena cambio = new CambioContrasena(actual, nueva);
            usuarioRepository.cambiarContrasena(cambio, new UsuarioRepository.OperacionCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
                    dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(context, "Error al cambiar contraseña", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return dialog;
    }
}
