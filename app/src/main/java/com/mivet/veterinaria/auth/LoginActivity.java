package com.mivet.veterinaria.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.mivet.veterinaria.R;
import com.mivet.veterinaria.Usuario.UsuarioMenuActivity;
import com.mivet.veterinaria.network.LoginConnectionClass;
import com.mivet.veterinaria.helpers.UIHelper;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    EditText etxtEmail, etxtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicialización de variables.
        btnLogin = findViewById(R.id.btnIniciarSesion);
        etxtEmail = findViewById(R.id.etEmailLogin);
        etxtPassword = findViewById(R.id.etPasswordLogin);

        btnLogin.setOnClickListener(v -> {
            Log.d("LoginActivity", "Iniciando sesión...");

            String correo = etxtEmail.getText().toString().trim();
            String contrasena = etxtPassword.getText().toString().trim();

            if (!validarDatosUsuario(correo, contrasena)) {
                UIHelper.mostrarAlerta(
                        LoginActivity.this,
                        "Campos incompletos",
                        "Por favor, completa todos los campos antes de continuar.",
                        getResources().getColor(R.color.btn_principal)
                );

                return;
            }

            iniciarSesion(correo, contrasena);
        });
    }

    private boolean validarDatosUsuario(String correo, String contrasena) {
        return !(correo.isEmpty() || contrasena.isEmpty());
    }

    private void iniciarSesion(String correo, String contrasena) {
        new Thread(() -> {
            JSONObject response = LoginConnectionClass.login(correo, contrasena);
            Log.d("Login", "Respuesta del servidor: " + response.toString());

            boolean success = response.optBoolean("success", false);
            if (success) {
                String token = response.optString("token", "");
                int userId = response.optInt("user_id", -1);
                String rol = response.optString("rol", "");

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
                    editor.putString("TOKEN", token);
                    editor.putInt("USER_ID", userId);
                    editor.putString("ROL", rol);
                    editor.apply();

                    Log.d("Login", "Sesión guardada correctamente en EncryptedSharedPreferences");

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        com.mivet.veterinaria.helpers.UIHelper.mostrarAlerta(
                                LoginActivity.this,
                                "Error interno",
                                "No se pudo guardar la sesión de forma segura.",
                                getResources().getColor(R.color.error_red)
                        );
                    });
                }

                runOnUiThread(() -> {
                    Intent intent = new Intent(LoginActivity.this, UsuarioMenuActivity.class);
                    startActivity(intent);
                    finish();
                });

            } else {
                String msg = response.optString("message", "Correo o contraseña incorrectos.");
                Log.e("Inicio de sesión", "Falló: " + msg);
                runOnUiThread(() -> {
                    com.mivet.veterinaria.helpers.UIHelper.mostrarAlerta(
                            LoginActivity.this,
                            "Error de autenticación",
                            msg,
                            getResources().getColor(R.color.error_red)
                    );
                });
            }
        }).start();
    }
}
