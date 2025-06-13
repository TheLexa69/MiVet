package com.mivet.veterinaria.usuario;

import static com.mivet.veterinaria.helpers.SesionUtils.cerrarSesion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.android.material.navigation.NavigationView;
import com.mivet.veterinaria.API.models.Usuario;
import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.MainActivity;
import com.mivet.veterinaria.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UsuarioMenuActivity extends AppCompatActivity {

    public static String USERNAME;
    public static String USEREMAIL;
    public static String USERTYPE;
    public static String USERROLE;
    private UsuarioRepository usuarioRepository;
    TextView tvWelcome;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_menu);

        // 1. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // 3. Toggle para el botón hamburguesa
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 4. Listener del menú lateral
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawers(); // Cierra el drawer tras clic

            if (id == R.id.action_perfil) {
                startActivity(new Intent(this, UsuarioPerfilActivity.class));
            } else if (id == R.id.action_mascotas) {
                startActivity(new Intent(this, UsuarioMascotasActivity.class));
            } else if (id == R.id.action_configuracion) {
                startActivity(new Intent(this, UsuarioMascotasActivity.class));
            } else if (id == R.id.action_cerrar_sesion) {
                cerrarSesion(this);
            }
            return true;
        });
        //FIN DEL TOOLBAR

        usuarioRepository = new UsuarioRepository(this);


        tvWelcome = findViewById(R.id.tvWelcome);

        getUserData();

        // Tarjeta 1
        View cardPerfil = findViewById(R.id.card1);
        ImageView icon1 = cardPerfil.findViewById(R.id.imgAnimal);
        TextView title1 = cardPerfil.findViewById(R.id.tvNombreAnimal);
        icon1.setImageResource(R.drawable.opcionperfil);
        title1.setText("Perfil");

        // Tarjeta 2 - Falta entrar en info mascota, editarla y borrarla
        View cardMisMascotas = findViewById(R.id.card2);
        ImageView icon2 = cardMisMascotas.findViewById(R.id.imgAnimal);
        TextView title2 = cardMisMascotas.findViewById(R.id.tvNombreAnimal);
        icon2.setImageResource(R.drawable.opcionhuella2);
        title2.setText("Mis mascotas");

        // Tarjeta 3
        View card3 = findViewById(R.id.card3);
        ImageView icon3 = card3.findViewById(R.id.imgAnimal);
        TextView title3 = card3.findViewById(R.id.tvNombreAnimal);
        icon3.setImageResource(R.drawable.opcioncitas);
        title3.setText("Citas");

        // Tarjeta 4
        View card4 = findViewById(R.id.card4);
        ImageView icon4 = card4.findViewById(R.id.imgAnimal);
        TextView title4 = card4.findViewById(R.id.tvNombreAnimal);
        icon4.setImageResource(R.drawable.opciongastos);
        title4.setText("Gastos");

        // Tarjeta 5
        View card5 = findViewById(R.id.card5);
        ImageView icon5 = card5.findViewById(R.id.imgAnimal);
        TextView title5 = card5.findViewById(R.id.tvNombreAnimal);
        icon5.setImageResource(R.drawable.opcionajustes);
        title5.setText("Adopción");

        // Tarjeta 6
        View card6 = findViewById(R.id.card6);
        ImageView icon6 = card6.findViewById(R.id.imgAnimal);
        TextView title6 = card6.findViewById(R.id.tvNombreAnimal);
        icon6.setImageResource(R.drawable.opcionajustes);
        title6.setText("Ajustes");


        //CARD DEL PERFIL
        cardPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(UsuarioMenuActivity.this, UsuarioPerfilActivity.class);
            startActivity(intent);
        });

        //CARD DE MIS MASCOTAS
        cardMisMascotas.setOnClickListener(v -> {
            Intent intent = new Intent(UsuarioMenuActivity.this, UsuarioMascotasActivity.class);
            startActivity(intent);
        });
    }

    //CONFIGURACIÓN DEL MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_perfil) {
            startActivity(new Intent(this, UsuarioPerfilActivity.class));
            return true;
        } else if (id == R.id.action_mascotas) {
            startActivity(new Intent(this, UsuarioMascotasActivity.class));
            return true;
        } else if (id == R.id.action_configuracion) {
            startActivity(new Intent(this, UsuarioMascotasActivity.class));
            return true;
        } else if (id == R.id.action_cerrar_sesion) {
            cerrarSesion(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //FIN DE CONFIGURACIÓN DEL MENU


    private void getUserData() {
        usuarioRepository.getPerfil(new UsuarioRepository.UsuarioCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                USERNAME = usuario.getNombre();
                USEREMAIL = usuario.getCorreo();
                USERTYPE = usuario.getTipoUsuario();
                USERROLE = usuario.getRol();

                runOnUiThread(() -> tvWelcome.setText("Bienvenido " + USERNAME + "!"));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("UsuarioMenuActivity", "Error al obtener perfil", t);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserData(); // Refresca los datos del usuario cada vez que se vuelve a esta pantalla
    }


}