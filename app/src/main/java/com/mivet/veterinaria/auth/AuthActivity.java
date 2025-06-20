package com.mivet.veterinaria.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mivet.veterinaria.R;

public class AuthActivity extends AppCompatActivity {
    private Button btnRegister, btnLogin, btnGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);


        //Inicialización de variables.
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);

        // Configuración del botón de registro
        btnRegister.setOnClickListener(v -> {
            // Iniciar la actividad de registro
            Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {;
            // Iniciar la actividad de inicio de sesión
            Intent intent = new Intent(AuthActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        
        btnGoogle.setOnClickListener(v -> {
            //Inicializar la autenticación con Google desactivada temporalmente
            Toast.makeText(this, "Botón desactivado temporalmente", Toast.LENGTH_SHORT).show();
        });

    }
}