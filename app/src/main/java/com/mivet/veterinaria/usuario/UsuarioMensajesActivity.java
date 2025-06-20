package com.mivet.veterinaria.usuario;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mivet.veterinaria.API.models.Mensaje;
import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.helpers.DrawerUtils;
import com.mivet.veterinaria.helpers.FechaUtils;
import com.mivet.veterinaria.viewmodels.MensajeVM;
import com.mivet.veterinaria.viewmodels.MensajeVMFactory;

import java.util.List;

public class UsuarioMensajesActivity extends AppCompatActivity {

    private RecyclerView rvMensajes;
    private MensajeVM mensajeVM;
    private MensajesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_mensajes);

        // 1. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. DrawerLayout y NavigationView
        DrawerUtils.configurarDrawerUsuario(this, toolbar);
        //FIN DEL TOOLBAR

        // ViewModel con Factory
        MensajeVMFactory factory = new MensajeVMFactory(new UsuarioRepository(this));
        mensajeVM = new ViewModelProvider(this, factory).get(MensajeVM.class);

        // RecyclerView
        rvMensajes = findViewById(R.id.rvMensajes);
        rvMensajes.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MensajesAdapter();
        rvMensajes.setAdapter(adapter);

        // Observa cambios
        mensajeVM.getMensajes().observe(this, mensajes -> {
            if (mensajes != null) adapter.setMensajes(mensajes);
        });

        mensajeVM.cargarMensajes();
    }

    private class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.MensajeViewHolder> {
        private List<Mensaje> mensajes;

        public void setMensajes(List<Mensaje> mensajes) {
            this.mensajes = mensajes;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
            return new MensajeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
            Mensaje mensaje = mensajes.get(position);
            holder.tvTitulo.setText(mensaje.getTitulo());
            holder.tvFecha.setText(FechaUtils.convertirAFormatoLeible(mensaje.getFechaEnvio()));

            if (!mensaje.isLeido()) {
                holder.tvTitulo.setTypeface(null, Typeface.BOLD);
                holder.tvTitulo.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.clr_font));
                holder.itemView.setAlpha(1f);
                holder.ivIcono.setImageResource(R.drawable.mensajecerrado);
            } else {
                holder.tvTitulo.setTypeface(null, Typeface.NORMAL);
                holder.tvTitulo.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.clr_font));
                holder.itemView.setAlpha(0.75f);
                holder.ivIcono.setImageResource(R.drawable.mensajeabierto);
            }

            holder.itemView.setOnClickListener(v -> {
                com.mivet.veterinaria.usuario.fragments.MensajeDetalleDialogFragment dialog = com.mivet.veterinaria.usuario.fragments.MensajeDetalleDialogFragment.newInstance(
                        mensaje.getTitulo(),
                        mensaje.getCuerpo(),

                        mensaje.getFechaEnvio(),
                        mensaje.getId(),
                        mensaje.isLeido()
                );
                dialog.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "DetalleMensaje");
            });
        }


        @Override
        public int getItemCount() {
            return mensajes != null ? mensajes.size() : 0;
        }

        class MensajeViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitulo, tvFecha;
            ImageView ivIcono;

            public MensajeViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitulo = itemView.findViewById(R.id.tvTituloMensaje);
                tvFecha = itemView.findViewById(R.id.tvFechaMensaje);
                ivIcono = itemView.findViewById(R.id.ivIconoMensaje);
            }
        }
    }
}
