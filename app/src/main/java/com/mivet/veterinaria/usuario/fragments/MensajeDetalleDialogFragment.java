package com.mivet.veterinaria.usuario.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.viewmodels.MensajeVM;
import com.mivet.veterinaria.viewmodels.MensajeVMFactory;

public class MensajeDetalleDialogFragment extends DialogFragment {

    private static final String ARG_TITULO = "titulo";
    private static final String ARG_CUERPO = "cuerpo";
    private static final String ARG_FECHA = "fecha";
    private static final String ARG_ID = "id";
    public static final String LEIDO = "leido";


    private MensajeVM mensajeVM;

    public static MensajeDetalleDialogFragment newInstance(String titulo, String cuerpo, String fecha, long id, boolean leido) {
        MensajeDetalleDialogFragment fragment = new MensajeDetalleDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITULO, titulo);
        args.putString(ARG_CUERPO, cuerpo);
        args.putString(ARG_FECHA, fecha);
        args.putLong(ARG_ID, id);
        args.putBoolean(LEIDO, leido);
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_mensaje_detalle, null);
        TextView tvTitulo = view.findViewById(R.id.tvTituloDialogo);
        TextView tvCuerpo = view.findViewById(R.id.tvCuerpoDialogo);
        TextView tvFecha = view.findViewById(R.id.tvFechaMensaje);

        Bundle args = getArguments();
        if (args != null) {
            tvTitulo.setText(args.getString(ARG_TITULO));
            tvCuerpo.setText(args.getString(ARG_CUERPO));
            tvFecha.setText(args.getString(ARG_FECHA));
        }

        UsuarioRepository repo = new UsuarioRepository(requireContext());
        mensajeVM = new ViewModelProvider(requireActivity(), new MensajeVMFactory(repo)).get(MensajeVM.class);

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("Cerrar", (dialog, which) -> {
                    if (args != null) {
                        long id = args.getLong(ARG_ID);
                        boolean yaLeido = args.getBoolean(LEIDO, false);
                        if (!yaLeido) {
                            mensajeVM.marcarComoLeido(id);
                        }
                    }
                })
                .create();
    }
}
