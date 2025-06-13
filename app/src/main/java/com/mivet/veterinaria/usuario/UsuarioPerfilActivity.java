package com.mivet.veterinaria.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mivet.veterinaria.API.models.Usuario;
import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.dialogs.CambiarContrasenaDialog;
import com.mivet.veterinaria.helpers.SesionUtils;

public class UsuarioPerfilActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo;
    private Button btnGuardarPerfil, btnCambiarContrasena;
    private UsuarioRepository usuarioRepository;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private Usuario usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_perfil);

        // Toolbar + menú lateral
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
                // ya estamos aquí
            } else if (id == R.id.action_mascotas) {
                startActivity(new Intent(this, UsuarioMascotasActivity.class));
            } else if (id == R.id.action_configuracion) {
                startActivity(new Intent(this, UsuarioMenuActivity.class));
            } else if (id == R.id.action_cerrar_sesion) {
                SesionUtils.cerrarSesion(this);
            }
            return true;
        });

        // Init views
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        btnCambiarContrasena = findViewById(R.id.btnCambiarContrasena);

        usuarioRepository = new UsuarioRepository(this);

        // Obtener perfil actual
        cargarPerfil();

        btnGuardarPerfil.setOnClickListener(v -> guardarCambios());

        btnCambiarContrasena.setOnClickListener(v -> {
            new CambiarContrasenaDialog().show(getSupportFragmentManager(), "CambiarContrasena");
        });

    }

    private void cargarPerfil() {
        usuarioRepository.getPerfil(new UsuarioRepository.UsuarioCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                usuarioActual = usuario;
                runOnUiThread(() -> {
                    etNombre.setText(usuario.getNombre());
                    etCorreo.setText(usuario.getCorreo());
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("UsuarioPerfil", "Error al obtener perfil", t);
                runOnUiThread(() ->
                        Toast.makeText(UsuarioPerfilActivity.this, "Error al cargar perfil", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void guardarCambios() {
        String nuevoNombre = etNombre.getText().toString().trim();
        String nuevoCorreo = etCorreo.getText().toString().trim();

        if (nuevoNombre.isEmpty() || nuevoCorreo.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        usuarioActual.setNombre(nuevoNombre);
        usuarioActual.setCorreo(nuevoCorreo);

        usuarioRepository.actualizarPerfil(usuarioActual, new UsuarioRepository.OperacionCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() ->
                        Toast.makeText(UsuarioPerfilActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("UsuarioPerfil", "Error al actualizar", t);
                runOnUiThread(() ->
                        Toast.makeText(UsuarioPerfilActivity.this, "Error al guardar cambios", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
