package com.mivet.veterinaria.usuario;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.Gasto;
import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.API.models.TipoGasto;
import com.mivet.veterinaria.viewmodels.UsuarioGastosVM;
import com.mivet.veterinaria.viewmodels.UsuarioGastosVMFactory;
import com.mivet.veterinaria.helpers.SesionUtils;
import com.mivet.veterinaria.viewmodels.UsuarioVM;
import com.mivet.veterinaria.viewmodels.UsuarioVMFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UsuarioGastosActivity extends AppCompatActivity {

    private static final String TAG = "UsuarioGastosActivity";
    private RecyclerView recyclerView;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabNuevoGasto;
    private UsuarioGastosVM viewModel;
    private GastosAdapter adapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Button btnToggleFiltros, btnAplicarFiltros, btnBorrarFiltros;
    private LinearLayout layoutFiltros;
    private EditText etDia, etDesde, etHasta, etMes, etAnio;
    private boolean dialogoNuevoGastoActivo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_gastos);

        // Toolbar y Drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawers();

            if (id == R.id.action_perfil) {
                startActivity(new Intent(this, UsuarioPerfilActivity.class));
            } else if (id == R.id.action_mascotas) {
                startActivity(new Intent(this, UsuarioMascotasActivity.class));
            } else if (id == R.id.action_configuracion) {
                startActivity(new Intent(this, UsuarioMenuActivity.class));
            } else if (id == R.id.action_cerrar_sesion) {
                SesionUtils.cerrarSesion(this);
            }
            return true;
        });

        // Layout y filtros
        btnToggleFiltros = findViewById(R.id.btnToggleFiltros);
        layoutFiltros = findViewById(R.id.layoutFiltros);
        etDia = findViewById(R.id.etDia);
        etDesde = findViewById(R.id.etDesde);
        etHasta = findViewById(R.id.etHasta);
        etMes = findViewById(R.id.etMes);
        etAnio = findViewById(R.id.etAnio);
        btnAplicarFiltros = findViewById(R.id.btnAplicarFiltros);
        Button btnBorrarFiltros = findViewById(R.id.btnBorrarFiltros);

        btnToggleFiltros.setOnClickListener(v -> {
            if (layoutFiltros.getVisibility() == View.GONE) {
                layoutFiltros.setVisibility(View.VISIBLE);
                btnToggleFiltros.setText("FILTROS ⌃");
            } else {
                layoutFiltros.setVisibility(View.GONE);
                btnToggleFiltros.setText("FILTROS ⌄");
            }
        });

        // Spinner de tipo con opción "Todos"
        Spinner spinnerTipoFiltro = findViewById(R.id.spinnerTipoFiltro);
        List<String> opcionesTipo = new ArrayList<>();
        opcionesTipo.add("Todos");
        for (TipoGasto tipo : TipoGasto.values()) {
            opcionesTipo.add(tipo.name());
        }
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesTipo);
        spinnerTipoFiltro.setAdapter(adapterTipo);

        setupFechaPicker(etDia);
        setupFechaPicker(etDesde);
        setupFechaPicker(etHasta);

        btnAplicarFiltros.setOnClickListener(v -> {
            String tipoStr = spinnerTipoFiltro.getSelectedItem().toString();
            String tipo = tipoStr.equals("Todos") ? null : tipoStr;

            String dia = convertirFecha(etDia.getText().toString());
            String desde = convertirFecha(etDesde.getText().toString());
            String hasta = convertirFecha(etHasta.getText().toString());

            Integer mes = parseEntero(etMes.getText().toString());
            Integer anio = parseEntero(etAnio.getText().toString());

            viewModel.filtrarGastos(tipo, dia, mes, anio, desde, hasta);
        });

        btnBorrarFiltros.setOnClickListener(v -> {
            viewModel.cargarTodosLosGastos();
            etDia.setText("");
            etDesde.setText("");
            etHasta.setText("");
            etMes.setText("");
            etAnio.setText("");
            spinnerTipoFiltro.setSelection(0);
            //layoutFiltros.setVisibility(View.GONE); (DESCOMENTAR EN CASO DE NECESIDAD DE OCULTAR LOS FILTROS AL BORRAR LOS FILTROS)
            btnToggleFiltros.setText("FILTROS ⌄");
        });

        etDia.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = !s.toString().isEmpty();
                etMes.setEnabled(!hasText);
                etAnio.setEnabled(!hasText);
                etDesde.setEnabled(!hasText);
                etHasta.setEnabled(!hasText);
            }
        });

        TextWatcher rangoWatcher = new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean anyOther = !etMes.getText().toString().isEmpty()
                        || !etAnio.getText().toString().isEmpty()
                        || !etDesde.getText().toString().isEmpty()
                        || !etHasta.getText().toString().isEmpty();

                etDia.setEnabled(!anyOther);
            }
        };

        etMes.addTextChangedListener(rangoWatcher);
        etAnio.addTextChangedListener(rangoWatcher);
        etDesde.addTextChangedListener(rangoWatcher);
        etHasta.addTextChangedListener(rangoWatcher);


        // RecyclerView
        recyclerView = findViewById(R.id.recyclerGastos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GastosAdapter();
        recyclerView.setAdapter(adapter);

        UsuarioGastosVMFactory factory = new UsuarioGastosVMFactory(this);
        viewModel = new ViewModelProvider(this, factory).get(UsuarioGastosVM.class);

        viewModel.getGastos().observe(this, gastos -> {
            if (gastos != null) {
                adapter.setGastos(gastos);
            }
        });

        fabNuevoGasto = findViewById(R.id.fabNuevoGasto);
        fabNuevoGasto.setOnClickListener(v -> mostrarDialogoNuevoGasto());

        viewModel.cargarTodosLosGastos();
    }


    @Override
    protected void onResume() {
        super.onResume();
        viewModel.cargarTodosLosGastos();

    }

    private void cargarGastosMesActual() {
        Calendar calendar = Calendar.getInstance();
        int mes = calendar.get(Calendar.MONTH) + 1;
        int anio = calendar.get(Calendar.YEAR);
        viewModel.cargarGastosDelMes(mes, anio);
    }

    private class GastosAdapter extends RecyclerView.Adapter<GastosAdapter.GastoViewHolder> {
        private List<Gasto> gastos = new ArrayList<>();

        public void setGastos(List<Gasto> nuevosGastos) {
            this.gastos = nuevosGastos;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public GastoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_gasto, parent, false);
            return new GastoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GastoViewHolder holder, int position) {
            Gasto gasto = gastos.get(position);
            holder.tvDescripcion.setText(gasto.getDescripcion());
            holder.tvFecha.setText(gasto.getFecha());
            holder.tvCantidad.setText("€" + gasto.getCantidad());
            holder.btnEliminarGasto.setOnClickListener(v -> {
                new AlertDialog.Builder(UsuarioGastosActivity.this)
                        .setTitle("Eliminar gasto")
                        .setMessage("¿Seguro que deseas eliminar este gasto?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            UsuarioRepository repo = new UsuarioRepository(UsuarioGastosActivity.this);
                            Log.d(TAG, "onBindViewHolder: " + new Gson().toJson(gasto));
                            Log.d(TAG , "onBindViewHolder: ID Gasto: " + gasto.getId());
                            repo.eliminarGasto(gasto.getId(), new UsuarioRepository.OperacionCallback() {
                                @Override
                                public void onSuccess() {
                                    runOnUiThread(() -> {
                                        Toast.makeText(UsuarioGastosActivity.this, "Gasto eliminado", Toast.LENGTH_SHORT).show();
                                        viewModel.cargarTodosLosGastos(); // recarga
                                    });
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    runOnUiThread(() ->
                                            Toast.makeText(UsuarioGastosActivity.this, "Error al eliminar gasto", Toast.LENGTH_SHORT).show()
                                    );
                                }
                            });
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });

        }

        @Override
        public int getItemCount() {
            return gastos != null ? gastos.size() : 0;
        }

        class GastoViewHolder extends RecyclerView.ViewHolder {
            TextView tvDescripcion, tvFecha, tvCantidad;
            ImageView btnEliminarGasto;

            public GastoViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
                tvFecha = itemView.findViewById(R.id.tvFecha);
                tvCantidad = itemView.findViewById(R.id.tvCantidad);
                btnEliminarGasto = itemView.findViewById(R.id.btnEliminarGasto);

            }
        }
    }
    private void mostrarDialogoNuevoGasto() {
        // Si ya hay un diálogo mostrándose, no hacer nada
        if (dialogoNuevoGastoActivo) return;

        dialogoNuevoGastoActivo = true;

        UsuarioVMFactory factory = new UsuarioVMFactory(this);
        UsuarioVM usuarioVM = new ViewModelProvider(this, factory).get(UsuarioVM.class);

        usuarioVM.mascotasLD.observe(this, mascotas -> {
            if (mascotas != null && !mascotas.isEmpty()) {
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_nuevo_gasto, null);

                EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);
                EditText etCantidad = dialogView.findViewById(R.id.etCantidad);
                EditText etFecha = dialogView.findViewById(R.id.etFecha);
                Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);
                Spinner spinnerMascota = dialogView.findViewById(R.id.spinnerMascota);

                ArrayAdapter<TipoGasto> tipoAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        TipoGasto.values());
                spinnerTipo.setAdapter(tipoAdapter);

                Calendar calendar = Calendar.getInstance();
                etFecha.setOnClickListener(v -> {
                    new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        etFecha.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year));
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                });

                ArrayAdapter<String> mascotaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
                for (PetInfo m : mascotas) mascotaAdapter.add(m.getNombre());
                spinnerMascota.setAdapter(mascotaAdapter);

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Nuevo Gasto")
                        .setView(dialogView)
                        .setPositiveButton("Crear", (dialogInterface, which) -> {
                            String descripcion = etDescripcion.getText().toString();
                            String cantidadStr = etCantidad.getText().toString();
                            String fechaStr = etFecha.getText().toString();
                            TipoGasto tipo = (TipoGasto) spinnerTipo.getSelectedItem();
                            int posMascota = spinnerMascota.getSelectedItemPosition();

                            if (descripcion.isEmpty() || cantidadStr.isEmpty() || fechaStr.isEmpty() || posMascota < 0) {
                                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            double cantidad;
                            try {
                                cantidad = Double.parseDouble(cantidadStr);
                            } catch (NumberFormatException e) {
                                Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String[] partes = fechaStr.split("/");
                            String fechaISO = partes[2] + "-" + partes[1] + "-" + partes[0];
                            PetInfo mascotaSeleccionada = mascotas.get(posMascota);

                            Gasto nuevo = new Gasto();
                            nuevo.setDescripcion(descripcion);
                            nuevo.setCantidad(cantidad);
                            nuevo.setFecha(fechaISO);
                            nuevo.setTipo(tipo);
                            nuevo.setIdMascota(Long.parseLong(mascotaSeleccionada.getId()));

                            UsuarioRepository repo = new UsuarioRepository(this);
                            repo.crearGasto(nuevo, new UsuarioRepository.OperacionCallback() {
                                @Override
                                public void onSuccess() {
                                    runOnUiThread(() -> {
                                        Toast.makeText(UsuarioGastosActivity.this, "Gasto creado", Toast.LENGTH_SHORT).show();
                                        viewModel.cargarTodosLosGastos();
                                    });
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    runOnUiThread(() -> Toast.makeText(UsuarioGastosActivity.this, "Error al crear gasto", Toast.LENGTH_SHORT).show());
                                }
                            });
                        })
                        .setNegativeButton("Cancelar", null)
                        .create();

                fabNuevoGasto.setEnabled(false);
                fabNuevoGasto.setAlpha(0.5f);

                dialog.setOnDismissListener(d -> {
                    dialogoNuevoGastoActivo = false;
                    fabNuevoGasto.setEnabled(true);
                    fabNuevoGasto.setAlpha(1f); // vuelve a la opacidad normal
                });
                dialog.show();

            } else {
                Toast.makeText(this, "No tienes mascotas cargadas", Toast.LENGTH_LONG).show();
                dialogoNuevoGastoActivo = false;
            }
        });

        usuarioVM.cargarMascotas();
    }

    private void setupFechaPicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        editText.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                editText.setText(String.format("%02d/%02d/%04d", day, month + 1, year));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private String convertirFecha(String ddMMyyyy) {
        if (ddMMyyyy.isEmpty()) return null;
        try {
            String[] p = ddMMyyyy.split("/");
            return p[2] + "-" + p[1] + "-" + p[0];
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseEntero(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Clase auxiliar para simplificar la implementación de TextWatcher.
     * Solo requiere sobreescribir onTextChanged, ignorando beforeTextChanged y afterTextChanged.
     * Útil para manejar campos de texto reactivos como filtros.
     */
    private abstract class SimpleTextWatcher implements android.text.TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(android.text.Editable s) {}
    }


}
