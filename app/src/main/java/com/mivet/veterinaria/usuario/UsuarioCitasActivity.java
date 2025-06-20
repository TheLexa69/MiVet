package com.mivet.veterinaria.usuario;

import static com.mivet.veterinaria.helpers.SesionUtils.cerrarSesion;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.Cita;
import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.helpers.DrawerUtils;
import com.mivet.veterinaria.helpers.FechaUtils;
import com.mivet.veterinaria.helpers.UIHelper;
import com.mivet.veterinaria.notificaciones.ProgramadorRecordatorios;
import com.mivet.veterinaria.viewmodels.UsuarioCitasVM;
import com.mivet.veterinaria.viewmodels.UsuarioCitasVMFactory;
import com.mivet.veterinaria.viewmodels.UsuarioVM;
import com.mivet.veterinaria.viewmodels.UsuarioVMFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UsuarioCitasActivity extends AppCompatActivity {
    UsuarioCitasVMFactory factory;
    UsuarioCitasVM citasVM;
    private List<PetInfo> mascotas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_citas);


        factory = new UsuarioCitasVMFactory(this);
        citasVM = new ViewModelProvider(this, factory).get(UsuarioCitasVM.class);

        // 1. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. DrawerLayout y NavigationView
        DrawerUtils.configurarDrawerUsuario(this, toolbar);
        //FIN DEL TOOLBAR

        RecyclerView rv = findViewById(R.id.rvCitas);
        rv.setLayoutManager(new LinearLayoutManager(this));

        citasVM.citasLD.observe(this, citas -> {
            if (citas != null) {
                rv.setAdapter(new CitasAdapter(citas));
            }
        });

        citasVM.cargarCitas();

        FloatingActionButton fabAgregar = findViewById(R.id.fabNuevaCita);
        fabAgregar.setOnClickListener(v -> {
            UsuarioVM vm = new ViewModelProvider(this,
                    new UsuarioVMFactory(this)).get(UsuarioVM.class);

            Observer<List<PetInfo>> tempObserver = new Observer<>() {
                @Override
                public void onChanged(List<PetInfo> lista) {
                    vm.mascotasLD.removeObserver(this); // ← clave
                    if (lista != null && !lista.isEmpty()) {
                        mostrarDialogoNuevaCitaConMascota(lista);
                    } else {
                        UIHelper.mostrarAlerta(UsuarioCitasActivity.this, "Sin mascotas", "No puedes crear una cita sin mascotas", getColor(R.color.error_red));
                    }
                }
            };

            vm.mascotasLD.observe(this, tempObserver);
            vm.cargarMascotas();
        });

    }

    private class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.CitaViewHolder> {

        private final List<Cita> citas;

        public CitasAdapter(List<Cita> citas) {
            this.citas = citas;
        }

        @NonNull
        @Override
        public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
            return new CitaViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
            Cita cita = citas.get(position);
            holder.tvTipo.setText(cita.getTipo());
            holder.tvEmpresa.setText(cita.getEmpresa());
            holder.tvFecha.setText(FechaUtils.convertirAFormatoLeible(cita.getFecha()));


            holder.btnEditarCita.setOnClickListener(v -> {
                View dialogView = LayoutInflater.from(UsuarioCitasActivity.this).inflate(R.layout.dialog_editar_cita, null);
                EditText etEmpresa = dialogView.findViewById(R.id.etEmpresa);
                EditText etFecha = dialogView.findViewById(R.id.etFecha);
                Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);

                // Pre-cargar valores actuales
                etEmpresa.setText(cita.getEmpresa());
                etFecha.setText(FechaUtils.convertirAFormatoLeible(cita.getFecha()));

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        UsuarioCitasActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        new String[]{"veterinaria", "peluqueria", "vacunacion"}
                );
                spinnerTipo.setAdapter(adapter);
                spinnerTipo.setSelection(adapter.getPosition(cita.getTipo())); // set tipo actual

                // Date picker para fecha
                etFecha.setOnClickListener(v2 -> {
                    Calendar calendar = Calendar.getInstance();
                    Date fechaParseada = FechaUtils.parsearDesdeISO(cita.getFecha());
                    if (fechaParseada != null) {
                        calendar.setTime(fechaParseada);
                    } else {
                        calendar.setTime(new Date()); // fallback
                    }

                    new DatePickerDialog(UsuarioCitasActivity.this, (view, year, month, dayOfMonth) -> {
                        new TimePickerDialog(UsuarioCitasActivity.this, (view1, hour, minute) -> {
                            calendar.set(year, month, dayOfMonth, hour, minute);
                            Date fecha = calendar.getTime();
                            etFecha.setText(FechaUtils.convertirAFormatoLeibleDate(fecha));
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                });


                new AlertDialog.Builder(UsuarioCitasActivity.this)
                        .setTitle("Editar Cita")
                        .setView(dialogView)
                        .setPositiveButton("Guardar", (dialog, which) -> {
                            cita.setEmpresa(etEmpresa.getText().toString());
                            cita.setTipo(spinnerTipo.getSelectedItem().toString());
                            String fechaLeible = etFecha.getText().toString();
                            Date fechaComoDate = FechaUtils.parsearDesdeLegible(fechaLeible);
                            if (fechaComoDate != null) {
                                cita.setFecha(FechaUtils.convertirAFormatoISO(fechaComoDate));
                            } else {
                                UIHelper.mostrarAlerta(UsuarioCitasActivity.this, "Error", "La fecha ingresada no es válida.", getColor(R.color.clr_font));
                                return;
                            }


                            // Llamar al repo para actualizar
                            UsuarioRepository repo = new UsuarioRepository(UsuarioCitasActivity.this);
                            repo.actualizarCita(cita.getId(), cita, new UsuarioRepository.OperacionCallback() {
                                @Override
                                public void onSuccess() {
                                    runOnUiThread(() -> {
                                        notifyItemChanged(holder.getAdapterPosition());
                                        ProgramadorRecordatorios.cancelar(getApplicationContext(), cita); // cita previa (mismo objeto)
                                        ProgramadorRecordatorios.programar(getApplicationContext(), cita); // con la fecha nueva
                                        UIHelper.mostrarAlerta(UsuarioCitasActivity.this, "Éxito", "Cita actualizada correctamente", getColor(R.color.clr_font));
                                    });
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    runOnUiThread(() -> Toast.makeText(UsuarioCitasActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show());
                                }
                            });
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });


            holder.btnEliminarCita.setOnClickListener(v -> {
                new AlertDialog.Builder(UsuarioCitasActivity.this)
                        .setTitle("Eliminar Cita")
                        .setMessage("¿Estás seguro de que deseas eliminar esta cita?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            UsuarioRepository repo = new UsuarioRepository(UsuarioCitasActivity.this);
                            repo.eliminarCita(cita.getId(), new UsuarioRepository.OperacionCallback() {
                                @Override
                                public void onSuccess() {
                                    runOnUiThread(() -> {
                                        citas.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        ProgramadorRecordatorios.cancelar(getApplicationContext(), cita);
                                        UIHelper.mostrarAlerta(UsuarioCitasActivity.this, "Éxito", "Cita eliminada correctamente", getColor(R.color.clr_font));
                                    });
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    runOnUiThread(() -> Toast.makeText(UsuarioCitasActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show());
                                }
                            });
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });


        }

        @Override
        public int getItemCount() {
            return citas.size();
        }

        class CitaViewHolder extends RecyclerView.ViewHolder {
            TextView tvTipo, tvEmpresa, tvFecha;
            ImageView btnEditarCita, btnEliminarCita;

            public CitaViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTipo = itemView.findViewById(R.id.tvTipoCita);
                tvEmpresa = itemView.findViewById(R.id.tvEmpresaCita);
                tvFecha = itemView.findViewById(R.id.tvFechaCita);
                btnEditarCita = itemView.findViewById(R.id.btnEditarCita);
                btnEliminarCita = itemView.findViewById(R.id.btnEliminarCita);

            }
        }
    }


    private void mostrarDialogoNuevaCitaConMascota(List<PetInfo> mascotas) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_nueva_cita, null);
        EditText etEmpresa = dialogView.findViewById(R.id.etEmpresa);
        EditText etFecha = dialogView.findViewById(R.id.etFecha);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);
        Spinner spinnerMascota = dialogView.findViewById(R.id.spinnerMascota);

        // Tipos
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"veterinaria", "peluqueria", "vacunacion"});
        spinnerTipo.setAdapter(tipoAdapter);

        // Mascotas
        ArrayAdapter<String> mascotaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        for (PetInfo m : mascotas) mascotaAdapter.add(m.getNombre());
        spinnerMascota.setAdapter(mascotaAdapter);

        // Fecha
        Calendar calendar = Calendar.getInstance();
        etFecha.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                new TimePickerDialog(this, (view1, hour, minute) -> {
                    calendar.set(year, month, dayOfMonth, hour, minute);
                    etFecha.setText(FechaUtils.convertirAFormatoLeibleDate(calendar.getTime()));
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        new AlertDialog.Builder(this)
                .setTitle("Nueva Cita")
                .setView(dialogView)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String empresa = etEmpresa.getText().toString();
                    String tipo = spinnerTipo.getSelectedItem().toString();
                    String fechaStr = etFecha.getText().toString();
                    Date fecha = FechaUtils.parsearDesdeLegible(fechaStr);
                    int posMascota = spinnerMascota.getSelectedItemPosition();

                    if (empresa.isEmpty() || fecha == null || posMascota < 0) {
                        UIHelper.mostrarAlerta(this, "Error", "Todos los campos son obligatorios", getColor(R.color.error_red));
                        return;
                    }

                    PetInfo mascotaSeleccionada = mascotas.get(posMascota);

                    Cita cita = new Cita();
                    cita.setEmpresa(empresa);
                    cita.setTipo(tipo);
                    cita.setFecha(FechaUtils.convertirAFormatoISO(fecha));
                    cita.setIdMascota(Long.parseLong(mascotaSeleccionada.getId())); // asegurarse de que sea Long

                    UsuarioRepository repo = new UsuarioRepository(this);
                    repo.crearCita(cita, new UsuarioRepository.OperacionCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                UIHelper.mostrarAlerta(UsuarioCitasActivity.this, "Éxito", "Cita creada correctamente", getColor(R.color.clr_font));
                                ProgramadorRecordatorios.programar(getApplicationContext(), cita);
                                citasVM.cargarCitas();
                            });
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            runOnUiThread(() -> UIHelper.mostrarAlerta(UsuarioCitasActivity.this, "Error", "Error al crear cita", getColor(R.color.error_red)));
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


}