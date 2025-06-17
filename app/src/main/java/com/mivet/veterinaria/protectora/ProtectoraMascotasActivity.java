package com.mivet.veterinaria.protectora;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.helpers.FechaUtils;
import com.mivet.veterinaria.viewmodels.ProtectoraVM;
import com.mivet.veterinaria.viewmodels.ProtectoraVMFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProtectoraMascotasActivity extends AppCompatActivity {

    private ProtectoraVM viewModel;
    private MascotaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protectora_mascotas);

        RecyclerView rvMascotas = findViewById(R.id.rvMascotas);
        rvMascotas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MascotaAdapter();
        rvMascotas.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabNuevaMascota);
        fab.setOnClickListener(v -> mostrarDialogNuevaMascota());

//        fab.setOnClickListener(v -> {
//
//            Toast.makeText(this, "Abrir formulario para nueva mascota", Toast.LENGTH_SHORT).show();
//        });

        ProtectoraVMFactory factory = new ProtectoraVMFactory(this);
        viewModel = new ViewModelProvider(this, factory).get(ProtectoraVM.class);

        viewModel.mascotasLD.observe(this, adapter::setLista);
        viewModel.cargarMascotas();
    }

    private class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {
        private List<PetInfo> lista;

        public void setLista(List<PetInfo> lista) {
            this.lista = lista;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_animal_card, parent, false);
            return new MascotaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
            PetInfo mascota = lista.get(position);

            if (holder.tvNombre != null) holder.tvNombre.setText(mascota.getNombre());
            if (holder.tvRaza != null) holder.tvRaza.setText(mascota.getRaza());
            if (holder.tvFechaNac != null) holder.tvFechaNac.setText(mascota.getFechaNac());

            if (holder.imgAnimal != null) {
                switch (mascota.getTipo().toLowerCase()) {
                    case "gato":
                        holder.imgAnimal.setImageResource(R.drawable.gatocolorido);
                        break;
                    case "exotico":
                        holder.imgAnimal.setImageResource(R.drawable.iguanacolorida);
                        break;
                    default:
                        holder.imgAnimal.setImageResource(R.drawable.perrocolorido);
                        break;
                }
            }
        }


        @Override
        public int getItemCount() {
            return lista != null ? lista.size() : 0;
        }

        class MascotaViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombre, tvRaza, tvFechaNac;
            ImageView imgAnimal, imgIrInfo;

            public MascotaViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombre = itemView.findViewById(R.id.tvNombreAnimal);
                tvRaza = itemView.findViewById(R.id.tvRazaAnimal);
                tvFechaNac = itemView.findViewById(R.id.tvFechaNac);
                imgAnimal = itemView.findViewById(R.id.imgAnimal);
                imgIrInfo = itemView.findViewById(R.id.imgIrInfoAnimal);

                // Desactivar botón derecho
                if (imgIrInfo != null) {
                    imgIrInfo.setEnabled(false);
                    imgIrInfo.setAlpha(0.3f);
                }
            }
        }

    }

    private void mostrarDialogNuevaMascota() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_nueva_mascota, null);
        EditText etNombre = dialogView.findViewById(R.id.etNombreMascota);
        EditText etRaza = dialogView.findViewById(R.id.etRazaMascota);
        EditText etFecha = dialogView.findViewById(R.id.etFechaNacMascota);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionMascota);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipoMascota);

        // Spinner tipos
        String[] tipos = {"perro", "gato", "exotico"};
        ArrayAdapter<String> adapterTipos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos);
        spinnerTipo.setAdapter(adapterTipos);

        // Fecha picker
        final Calendar calendar = Calendar.getInstance();

        etFecha.setOnClickListener(view -> {
            new DatePickerDialog(this, (datePicker, year, month, day) -> {
                calendar.set(year, month, day);
                String fechaFormateada = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH));

                etFecha.setText(fechaFormateada);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Nueva Mascota")
                .setView(dialogView)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();

        Button btnGuardar = dialogView.findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(view -> {
            String nombre = etNombre.getText().toString().trim();
            String tipo = spinnerTipo.getSelectedItem().toString();
            String raza = etRaza.getText().toString().trim();
            String fechaNacLegible = etFecha.getText().toString().trim();
            String fechaNac = fechaNacLegible;  // Ya en formato yyyy-MM-dd


            String descripcion = etDescripcion.getText().toString().trim();

            if (!fechaNac.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Toast.makeText(this, "Formato de fecha inválido (usa AAAA-MM-DD)", Toast.LENGTH_SHORT).show();
                return;
            }


            if (nombre.isEmpty() || tipo.isEmpty() || fechaNac.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos obligatorios.", Toast.LENGTH_SHORT).show();
                return;
            }

            PetInfo nuevaMascota = new PetInfo();
            nuevaMascota.setNombre(nombre);
            nuevaMascota.setTipo(tipo);
            nuevaMascota.setRaza(raza);
            nuevaMascota.setFechaNac(fechaNac);
            nuevaMascota.setDescripcion(descripcion);

            viewModel.crearMascota(nuevaMascota, new com.mivet.veterinaria.API.repository.ProtectoraRepository.OperacionCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(ProtectoraMascotasActivity.this, "Mascota registrada correctamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        viewModel.cargarMascotas(); // Recargar lista
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    runOnUiThread(() -> Toast.makeText(ProtectoraMascotasActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show());
                }
            });
        });
    }

}
