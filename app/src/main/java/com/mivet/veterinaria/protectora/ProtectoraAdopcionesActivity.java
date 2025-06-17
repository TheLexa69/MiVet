package com.mivet.veterinaria.protectora;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.Adopcion;
import com.mivet.veterinaria.API.repository.MascotaRepository;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.helpers.FechaUtils;
import com.mivet.veterinaria.viewmodels.ProtectoraVM;
import com.mivet.veterinaria.viewmodels.ProtectoraVMFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProtectoraAdopcionesActivity extends AppCompatActivity {

    private ProtectoraVM viewModel;
    private AdopcionesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protectora_adopciones);

        RecyclerView rvSolicitudes = findViewById(R.id.rvAdopciones);
        rvSolicitudes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdopcionesAdapter();
        rvSolicitudes.setAdapter(adapter);

        ProtectoraVMFactory factory = new ProtectoraVMFactory(this);
        viewModel = new ViewModelProvider(this, factory).get(ProtectoraVM.class);

        viewModel.solicitudesLD.observe(this, adapter::setLista);
        viewModel.cargarSolicitudesPendientes();
    }

    private class AdopcionesAdapter extends RecyclerView.Adapter<AdopcionesAdapter.AdopcionViewHolder> {
        private List<Adopcion> lista;
        private final MascotaRepository mascotaRepository = new MascotaRepository(ProtectoraAdopcionesActivity.this);

        public void setLista(List<Adopcion> lista) {
            this.lista = lista;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AdopcionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_adopcion, parent, false);
            return new AdopcionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdopcionViewHolder holder, int position) {
            Adopcion adopcion = lista.get(position);

            // Muestra temporal mientras carga
            holder.tvNombreMascota.setText("Mascota #" + adopcion.getIdMascota());

            // Carga nombre real de la mascota
            mascotaRepository.getMascotaPorId(adopcion.getIdMascota(), new MascotaRepository.MascotaCallback() {
                @Override
                public void onSuccess(PetInfo mascota) {
                    runOnUiThread(() ->
                            holder.tvNombreMascota.setText(mascota.getNombre())
                    );
                }

                @Override
                public void onFailure(Throwable t) {
                    // Opcional: puedes loguear o dejar el texto por defecto
                }
            });

            holder.tvMensaje.setText(adopcion.getMensaje());
            holder.tvFecha.setText(FechaUtils.convertirAFormatoLeible(adopcion.getFechaSolicitud()));

            holder.btnAceptar.setOnClickListener(v -> {
                holder.btnAceptar.setEnabled(false);
                holder.btnRechazar.setEnabled(false);

                viewModel.actualizarEstadoAdopcion(
                        adopcion.getId(),
                        "aceptada",
                        () -> runOnUiThread(() -> {
                            Toast.makeText(ProtectoraAdopcionesActivity.this, "Solicitud aceptada", Toast.LENGTH_SHORT).show();
                            viewModel.cargarSolicitudesPendientes(); // Esto recargará la lista
                        }),
                        error -> runOnUiThread(() -> {
                            Toast.makeText(ProtectoraAdopcionesActivity.this, "Error al aceptar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            // Rehabilita si hubo error
                            holder.btnAceptar.setEnabled(true);
                            holder.btnRechazar.setEnabled(true);
                        })
                );
            });

            holder.btnRechazar.setOnClickListener(v -> {
                holder.btnAceptar.setEnabled(false);
                holder.btnRechazar.setEnabled(false);

                viewModel.actualizarEstadoAdopcion(
                        adopcion.getId(),
                        "rechazada",
                        () -> runOnUiThread(() -> {
                            Toast.makeText(ProtectoraAdopcionesActivity.this, "Solicitud rechazada", Toast.LENGTH_SHORT).show();
                            viewModel.cargarSolicitudesPendientes();
                        }),
                        error -> runOnUiThread(() -> {
                            Toast.makeText(ProtectoraAdopcionesActivity.this, "Error al rechazar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            holder.btnAceptar.setEnabled(true);
                            holder.btnRechazar.setEnabled(true);
                        })
                );
            });

        }

        @Override
        public int getItemCount() {
            return lista != null ? lista.size() : 0;
        }

        class AdopcionViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombreMascota, tvMensaje, tvFecha;
            Button btnAceptar, btnRechazar;

            public AdopcionViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombreMascota = itemView.findViewById(R.id.tvNombreMascota);
                tvMensaje = itemView.findViewById(R.id.tvMensaje);
                tvFecha = itemView.findViewById(R.id.tvFecha);
                btnAceptar = itemView.findViewById(R.id.btnAceptar);
                btnRechazar = itemView.findViewById(R.id.btnRechazar);
            }
        }
    }

}
