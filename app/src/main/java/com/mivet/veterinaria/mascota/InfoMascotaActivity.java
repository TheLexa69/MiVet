package com.mivet.veterinaria.mascota;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;
import com.mivet.veterinaria.API.repository.MascotaRepository;
import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.usuario.UsuarioMascotasActivity;
import com.mivet.veterinaria.usuario.UsuarioMenuActivity;
import com.mivet.veterinaria.usuario.UsuarioPerfilActivity;
import com.mivet.veterinaria.viewmodels.MascotaVM;
import com.mivet.veterinaria.viewmodels.MascotaVMFactory;

import java.time.LocalDate;
import java.util.Calendar;

public class InfoMascotaActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private EditText etNombre, etRaza, etFechaNac;
    private Button btnEditar, btnBorrar, btnGuardar;

    private boolean modoEdicion = false;
    private LocalDate fechaSeleccionada;
    private MascotaVM vm;
    private Long idMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_mascota);

        // --- Toolbar y Drawer ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            int id = item.getItemId();
            if (id == R.id.action_perfil) {
                startActivity(new Intent(this, UsuarioPerfilActivity.class));
            } else if (id == R.id.action_mascotas) {
                startActivity(new Intent(this, UsuarioMascotasActivity.class));
            } else if (id == R.id.action_cerrar_sesion) {
                com.mivet.veterinaria.helpers.SesionUtils.cerrarSesion(this);
            }
            return true;
        });

        // --- Inicializar vistas ---
        etNombre = findViewById(R.id.etNombre);
        etRaza = findViewById(R.id.etRaza);
        etFechaNac = findViewById(R.id.etFechaNac);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnGuardar = findViewById(R.id.btnGuardar);

        // ViewModel
        vm = new ViewModelProvider(this, new MascotaVMFactory(this)).get(MascotaVM.class);

        idMascota = getIntent().getLongExtra("ID_MASCOTA", -1);
        if (idMascota == -1) {
            finish();
            return;
        }

        etFechaNac.setOnClickListener(v -> {
            if (!modoEdicion) return;
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        fechaSeleccionada = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                        etFechaNac.setText(fechaSeleccionada.toString());
                    }, year, month, day);
            datePickerDialog.show();
        });

        vm.mascotaLD.observe(this, mascota -> {
            if (mascota != null) {
                etNombre.setText(mascota.getNombre());
                etRaza.setText(mascota.getRaza());
                etFechaNac.setText(mascota.getFechaNac());
            }
        });

        vm.cargarMascotaPorId(idMascota);

        btnEditar.setOnClickListener(v -> {
            modoEdicion = true;
            habilitarCampos(true);
            btnGuardar.setVisibility(Button.VISIBLE);
            btnBorrar.setText("Cancelar");
        });

        btnBorrar.setOnClickListener(v -> {
            if (modoEdicion) {
                modoEdicion = false;
                habilitarCampos(false);
                btnGuardar.setVisibility(Button.GONE);
                btnBorrar.setText("Borrar");
                vm.cargarMascotaPorId(idMascota); // Restaurar
            } else {
                mostrarDialogoConfirmacion(idMascota);
            }
        });

        btnGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void habilitarCampos(boolean habilitar) {
        etNombre.setEnabled(habilitar);
        etRaza.setEnabled(habilitar);
        etFechaNac.setEnabled(habilitar);
    }

    private void guardarCambios() {
        String nombre = etNombre.getText().toString().trim();
        String raza = etRaza.getText().toString().trim();
        String fecha = etFechaNac.getText().toString().trim();

        if (nombre.isEmpty() || raza.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        vm.actualizarMascota(idMascota, nombre, raza, fecha, new UsuarioRepository.OperacionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(InfoMascotaActivity.this, "Mascota actualizada", Toast.LENGTH_SHORT).show();
                modoEdicion = false;
                habilitarCampos(false);
                btnGuardar.setVisibility(Button.GONE);
                btnBorrar.setText("Borrar");
                vm.cargarMascotaPorId(idMascota);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(InfoMascotaActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoConfirmacion(Long id) {
        new AlertDialog.Builder(this)
                .setTitle("¿Eliminar mascota?")
                .setMessage("¿Estás seguro de que deseas eliminar esta mascota?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    vm.eliminarMascota(id, new MascotaRepository.OperacionCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(InfoMascotaActivity.this, "Mascota eliminada", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Toast.makeText(InfoMascotaActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
