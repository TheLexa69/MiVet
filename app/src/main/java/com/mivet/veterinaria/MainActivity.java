package com.mivet.veterinaria;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.mivet.veterinaria.auth.LoginActivity;
import com.mivet.veterinaria.notificaciones.GestorNotificaciones;
import com.mivet.veterinaria.protectora.ProtectoraMenuActivity;
import com.mivet.veterinaria.usuario.UsuarioMenuActivity;
import com.mivet.veterinaria.auth.AuthActivity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String LANG = "LANG";
    private static final int REQ_NOTIFICACIONES = 1001;

    private SharedPreferences sharedPreferences;
    private String currentLanguage;
    private Button btnIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentLanguage = Locale.getDefault().getLanguage();
        btnIniciar = findViewById(R.id.btnIniciar);

        GestorNotificaciones.crearCanal(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQ_NOTIFICACIONES
                );
            }
        }


        // Configuración de EncryptedSharedPreferences
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        logSharedPreferences();
        String tipoUsuario = sharedPreferences.getString("TIPO_USUARIO", "").trim();

        // Verificar si existen las preferencias necesarias
        if (sharedPreferences != null &&
                sharedPreferences.contains("USER_ID") &&
                sharedPreferences.contains("ROL") &&
                sharedPreferences.contains("TOKEN") &&
                sharedPreferences.contains("TIPO_USUARIO")
        ) {
            verificarToken();

            Log.d(TAG, "Preferencias encontradas. Redirigiendo a UsuarioMenuActivity.");

            Intent intent;

            if ("protectora".equalsIgnoreCase(tipoUsuario.trim())) {
                intent = new Intent(MainActivity.this, ProtectoraMenuActivity.class);
            } else {
                intent = new Intent(MainActivity.this, UsuarioMenuActivity.class);
            }

            startActivity(intent);
            finish();
            return;
        } else {
//            redirigirALogin();
        }

        // Cargar el idioma guardado
        String savedLanguage = sharedPreferences.getString(LANG, "es");
        setLocale(savedLanguage);

        Spinner languageSpinner = findViewById(R.id.language_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Configurar el spinner para que seleccione el idioma correcto por defecto
        Log.d(TAG, "Idioma guardado: " + savedLanguage);
        if (savedLanguage.equals("en")) {
            languageSpinner.setSelection(0);
        } else if (savedLanguage.equals("es")) {
            languageSpinner.setSelection(1);
        } else if (savedLanguage.equals("pt")) {
            languageSpinner.setSelection(2);
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = "";
                switch (position) {
                    case 0:
                        selectedLang = "en";
                        Log.d(TAG, "Idioma seleccionado: Inglés");
                        break;
                    case 1:
                        selectedLang = "es";
                        Log.d(TAG, "Idioma seleccionado: Español");
                        break;
                    case 2:
                        selectedLang = "pt";
                        Log.d(TAG, "Idioma seleccionado: Portugués");
                        break;
                }

                if (!selectedLang.equals(currentLanguage)) {
                    currentLanguage = selectedLang;
                    sharedPreferences.edit()
                            .putString(LANG, selectedLang)
                            .apply();
                    setLocale(selectedLang);
                    recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "Ningún idioma seleccionado en el spinner");
            }
        });

        btnIniciar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(intent);
        });
    }

    // Método para cambiar el idioma
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.setLocale(locale);
        resources.updateConfiguration(config, dm);
    }

    private void logSharedPreferences() {
        if (sharedPreferences != null) {
            Log.d(TAG, "🔐 Datos guardados en EncryptedSharedPreferences:");
            Map<String, ?> allEntries = sharedPreferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d(TAG, "Clave: " + entry.getKey() + ", Valor: " + entry.getValue());
            }
        } else {
            Log.d(TAG, "❌ EncryptedSharedPreferences no está inicializado.");
        }
    }

    private void verificarToken() {
        String token = sharedPreferences.getString("TOKEN", null);
        if (token == null) {
            redirigirALogin();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("http://13.48.85.87:8080/api/mascotas");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Log.d("TokenCheck", "Token válido");
                    // Continuar normalmente
                } else {
                    Log.d("TokenCheck", "Token inválido o expirado");
//                    runOnUiThread(this::redirigirALogin);
                }
            } catch (Exception e) {
                Log.e("TokenCheck", "Error al verificar token", e);
                runOnUiThread(this::redirigirALogin);
            }
        }).start();
    }

    private void redirigirALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

//    private void redirigirAMain() {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
//
//    }
//

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_NOTIFICACIONES) {
            // Aquí podrías informar al usuario si rechaza, pero no es obligatorio
        }
    }

}
