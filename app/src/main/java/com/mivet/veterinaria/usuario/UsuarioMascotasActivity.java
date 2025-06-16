package com.mivet.veterinaria.usuario;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.helpers.DrawerUtils;
import com.mivet.veterinaria.mascota.InfoMascotaActivity;
import com.mivet.veterinaria.viewmodels.UsuarioVM;
import com.mivet.veterinaria.viewmodels.UsuarioVMFactory;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UsuarioMascotasActivity extends AppCompatActivity {
    private RecyclerView rv;
    private UsuarioVM usuarioVM;
    private MascotasAdapter adapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private FloatingActionButton fabNuevaMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_mascotas);

        // 1. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. DrawerLayout y NavigationView
        DrawerUtils.configurarDrawerUsuario(this, toolbar);
        //FIN DEL TOOLBAR

        rv = findViewById(R.id.rvMascotas);
        rv.setLayoutManager(new LinearLayoutManager(this));

        UsuarioVMFactory factory = new UsuarioVMFactory(this);
        usuarioVM = new ViewModelProvider(this, factory).get(UsuarioVM.class);

        adapter = new MascotasAdapter();
        rv.setAdapter(adapter);

        usuarioVM.mascotasLD.observe(this, mascotas -> {
            if (mascotas != null) {
                adapter.setMascotas(mascotas);
            }
        });

        fabNuevaMascota = findViewById(R.id.fabNuevaMascota);
        fabNuevaMascota.setOnClickListener(v -> mostrarDialogoNuevaMascota());

        usuarioVM.cargarMascotas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        usuarioVM.cargarMascotas();
    }

    private void mostrarDialogoNuevaMascota() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_nueva_mascota, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombreMascota);
        EditText etRaza = dialogView.findViewById(R.id.etRazaMascota);
        EditText etFecha = dialogView.findViewById(R.id.etFechaNacMascota);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionMascota);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipoMascota);
        Button btnGuardar = dialogView.findViewById(R.id.btnGuardar);

        etFecha.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            int anio = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        etFecha.setText(fecha);
                    },
                    anio, mes, dia
            );
            datePicker.show();
        });


        // Spinner opciones
        String[] tiposVisibles = {"Perro", "Gato", "Exótico"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposVisibles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String raza = etRaza.getText().toString().trim();
            String fechaNac = etFecha.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String tipoSeleccionado = spinnerTipo.getSelectedItem().toString();

            if (nombre.isEmpty() || raza.isEmpty() || fechaNac.isEmpty()) {
                Toast.makeText(this, "Por favor completa nombre, raza y fecha de nacimiento", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!fechaNac.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Toast.makeText(this, "Formato de fecha inválido (usa AAAA-MM-DD)", Toast.LENGTH_SHORT).show();
                return;
            }

            String tipo;
            switch (tipoSeleccionado) {
                case "Perro":
                    tipo = "perro";
                    break;
                case "Gato":
                    tipo = "gato";
                    break;
                case "Exótico":
                    tipo = "exotico";
                    break;
                default:
                    tipo = "perro";
            }

            PetInfo pet = new PetInfo();
            pet.setNombre(nombre);
            pet.setRaza(raza);
            pet.setTipo(tipo);
            pet.setFechaNac(fechaNac);
            pet.setDescripcion(descripcion);

            usuarioVM.crearMascota(pet, new UsuarioRepository.OperacionCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(UsuarioMascotasActivity.this, "Mascota creada correctamente", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    usuarioVM.cargarMascotas();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(UsuarioMascotasActivity.this, "Error al crear mascota", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();

        fabNuevaMascota.setEnabled(false);
        fabNuevaMascota.setAlpha(0.5f);

        dialog.setOnDismissListener(d -> {
            fabNuevaMascota.setEnabled(true);
            fabNuevaMascota.setAlpha(1f);
        });
    }

    private class MascotasAdapter extends RecyclerView.Adapter<MascotasAdapter.MascotaViewHolder> {
        private List<PetInfo> mascotas;

        public void setMascotas(List<PetInfo> mascotas) {
            this.mascotas = mascotas;
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
            PetInfo mascota = mascotas.get(position);
            holder.tvNombreAnimal.setText(mascota.getNombre());
            holder.tvRazaAnimal.setText(mascota.getRaza());
            holder.tvFechaNac.setText(mascota.getFechaNac());
            switch (mascota.getTipo()) {
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

            holder.imgIrInfoAnimal.setOnClickListener(v -> {
                Intent intent = new Intent(UsuarioMascotasActivity.this, InfoMascotaActivity.class);
                intent.putExtra("ID_MASCOTA", Long.parseLong(mascota.getId()));
                startActivity(intent);
            });



        }

        @Override
        public int getItemCount() {
            return (mascotas != null) ? mascotas.size() : 0;
        }

        class MascotaViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombreAnimal, tvRazaAnimal, tvFechaNac;
            ImageView imgAnimal, imgIrInfoAnimal;
            ConstraintLayout cl;

            public MascotaViewHolder(@NonNull View view) {
                super(view);
                tvNombreAnimal = view.findViewById(R.id.tvNombreAnimal);
                tvRazaAnimal = view.findViewById(R.id.tvRazaAnimal);
                tvFechaNac = view.findViewById(R.id.tvFechaNac);
                imgAnimal = view.findViewById(R.id.imgAnimal);
                imgIrInfoAnimal = view.findViewById(R.id.imgIrInfoAnimal);
                cl = view.findViewById(R.id.animalCardConstraint);
            }
        }
    }
}
