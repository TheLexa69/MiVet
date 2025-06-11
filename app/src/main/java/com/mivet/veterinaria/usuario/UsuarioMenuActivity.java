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


        tvWelcome = findViewById(R.id.tvWelcome);

        getUserData();

        // Tarjeta 1
        View cardPerfil = findViewById(R.id.card1);
        ImageView icon1 = cardPerfil.findViewById(R.id.imgAnimal);
        TextView title1 = cardPerfil.findViewById(R.id.tvNombreAnimal);
        icon1.setImageResource(R.drawable.opcionperfil); // tu icono
        title1.setText("Perfil");

        // Tarjeta 2
        View cardMisMascotas = findViewById(R.id.card2);
        ImageView icon2 = cardMisMascotas.findViewById(R.id.imgAnimal);
        TextView title2 = cardMisMascotas.findViewById(R.id.tvNombreAnimal);
        icon2.setImageResource(R.drawable.opcionhuella2); // tu icono
        title2.setText("Mis mascotas");

        // Tarjeta 3
        View card3 = findViewById(R.id.card3);
        ImageView icon3 = card3.findViewById(R.id.imgAnimal);
        TextView title3 = card3.findViewById(R.id.tvNombreAnimal);
        icon3.setImageResource(R.drawable.opcioncitas); // tu icono
        title3.setText("Citas");

        // Tarjeta 4
        View card4 = findViewById(R.id.card4);
        ImageView icon4 = card4.findViewById(R.id.imgAnimal);
        TextView title4 = card4.findViewById(R.id.tvNombreAnimal);
        icon4.setImageResource(R.drawable.opciongastos); // tu icono
        title4.setText("Gastos");

        // Tarjeta 5
        View card5 = findViewById(R.id.card5);
        ImageView icon5 = card5.findViewById(R.id.imgAnimal);
        TextView title5 = card5.findViewById(R.id.tvNombreAnimal);
        icon5.setImageResource(R.drawable.opcionajustes); // tu icono
        title5.setText("Adopción");

        // Tarjeta 6
        View card6 = findViewById(R.id.card6);
        ImageView icon6 = card6.findViewById(R.id.imgAnimal);
        TextView title6 = card6.findViewById(R.id.tvNombreAnimal);
        icon6.setImageResource(R.drawable.opcionajustes); // tu icono
        title6.setText("Ajustes");

        // Tarjeta Logout
        View cardLogout = findViewById(R.id.cardLogout);
        ImageView iconLogout = cardLogout.findViewById(R.id.imgAnimal);
        TextView titleLogout = cardLogout.findViewById(R.id.tvNombreAnimal);
        iconLogout.setImageResource(R.drawable.opcionsalir); // un icono de logout
        titleLogout.setText("Cerrar sesión");

        // Acción del logout
        cardLogout.setOnClickListener(v -> {
            try {
                MasterKey masterKey = new MasterKey.Builder(this)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();

                SharedPreferences securePrefs = EncryptedSharedPreferences.create(
                        this,
                        "secure_prefs",
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );

                SharedPreferences.Editor editor = securePrefs.edit();
                editor.clear();
                editor.apply();

                // Ir a pantalla principal
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //CARD DEL PERFIL
        cardPerfil.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    MasterKey masterKey = new MasterKey.Builder(this)
                            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                            .build();

                    SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                            this,
                            "secure_prefs",
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );

                    String token = sharedPreferences.getString("TOKEN", null);

                    if (token == null) {
                        Log.d("GETRequest", "Token no encontrado en SharedPreferences");
                        return;
                    }

                    URL url = new URL("http://13.48.85.87:8080/api/usuario/perfil");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        Log.d("GETRequest", "Respuesta: " + response.toString());
                        // Procesamiento de la respuesta JSON y actualizar la UI si es necesario
//                        Intent intent = new Intent(UsuarioMenuActivity.this, UsuarioPerfilActivity.class);


                    } else {
                        Log.d("GETRequest", "Error en la solicitud. Código de respuesta: " + responseCode);
                    }
                } catch (Exception e) {
                    Log.e("GETRequest", "Error al realizar la solicitud GET", e);
                }
            }).start();
        });

        //CARD DE MIS MASCOTAS
        cardMisMascotas.setOnClickListener(v -> {
            // Aquí puedes iniciar la actividad de Mis Mascotas
            Intent intent = new Intent(UsuarioMenuActivity.this, UsuarioMascotasActivity.class);
            startActivity(intent);

            // Si quieres hacer una solicitud GET como ejemplo, descomenta el siguiente código
//            new Thread(() -> {
//                try {
//                    MasterKey masterKey = new MasterKey.Builder(this)
//                            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//                            .build();
//
//                    SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
//                            this,
//                            "secure_prefs",
//                            masterKey,
//                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//                    );
//
//                    String token = sharedPreferences.getString("TOKEN", null);
//
//                    if (token == null) {
//                        Log.d("GETRequest", "Token no encontrado en SharedPreferences");
//                        return;
//                    }
//
//                    URL url = new URL("http://13.48.85.87:8080/api/usuario/mascotas");
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.setRequestProperty("Authorization", "Bearer " + token);
//                    conn.connect();
//
//                    int responseCode = conn.getResponseCode();
//                    if (responseCode == 200) {
//                        InputStream inputStream = conn.getInputStream();
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                        StringBuilder response = new StringBuilder();
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            response.append(line);
//                        }
//                        reader.close();
//                        Log.d("GETRequest", "Respuesta: " + response.toString());
//                    } else {
//                        Log.d("GETRequest", "Error en la solicitud. Código de respuesta: " + responseCode);
//                    }
//                } catch (Exception e) {
//                    Log.e("GETRequest", "Error al realizar la solicitud GET", e);
//                }
//            }).start();
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
        new Thread(() -> {
            try {
                MasterKey masterKey = new MasterKey.Builder(this)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();

                SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                        this,
                        "secure_prefs",
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );

                String token = sharedPreferences.getString("TOKEN", null);

                if (token == null) {
                    Log.d("GETRequest", "Token no encontrado en SharedPreferences");
                    return;
                }

                URL url = new URL("http://13.48.85.87:8080/api/usuario/perfil");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Procesar la respuesta JSON
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    int userId = jsonResponse.optInt("id", -1);
                    USERNAME = jsonResponse.optString("nombre", "Desconocido");
                    USEREMAIL = jsonResponse.optString("correo", "No disponible");
                    USERTYPE = jsonResponse.optString("tipoUsuario", "No especificado");
                    USERROLE = jsonResponse.optString("rol", "No especificado");

//                    Log.d("UsuarioMenuActivity", "ID: " + userId + ", Nombre: " + USERNAME + ", Correo: " + USEREMAIL +
//                            ", Tipo de Usuario: " + USERTYPE + ", Rol: " + USERROLE);

                    runOnUiThread(() -> {
                        tvWelcome.setText("Bienvenido " + USERNAME + "!");
                    });

                } else {
                    Log.d("GETRequest", "Error en la solicitud. Código de respuesta: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("GETRequest", "Error al realizar la solicitud GET", e);
            }
        }).start();
    }


}