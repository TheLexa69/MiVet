package com.mivet.veterinaria.protectora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.mivet.veterinaria.MainActivity;
import com.mivet.veterinaria.R;

public class ProtectoraMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protectora_menu);

        // Card 1 - Adopciones
        View cardAdopciones = findViewById(R.id.cardAdopciones);
        ImageView iconAdopciones = cardAdopciones.findViewById(R.id.imgCardIcon);
        TextView titleAdopciones = cardAdopciones.findViewById(R.id.tvCardTitle);
        iconAdopciones.setImageResource(R.drawable.opcionnotificacion);
        titleAdopciones.setText("Adopciones");

        // Card 2 - Animales
        View cardAnimales = findViewById(R.id.cardAnimales);
        ImageView iconAnimales = cardAnimales.findViewById(R.id.imgCardIcon);
        TextView titleAnimales = cardAnimales.findViewById(R.id.tvCardTitle);
        iconAnimales.setImageResource(R.drawable.opcionhuella2);
        titleAnimales.setText("Animales");

        // Card 3 - Perfil
        View cardPerfil = findViewById(R.id.cardPerfil);
        ImageView iconPerfil = cardPerfil.findViewById(R.id.imgCardIcon);
        TextView titlePerfil = cardPerfil.findViewById(R.id.tvCardTitle);
        iconPerfil.setImageResource(R.drawable.opcionperfil);
        titlePerfil.setText("Perfil");

        // Card 4 - Logout
        View cardLogout = findViewById(R.id.cardLogout);
        ImageView iconLogout = cardLogout.findViewById(R.id.imgCardIcon);
        TextView titleLogout = cardLogout.findViewById(R.id.tvCardTitle);
        iconLogout.setImageResource(R.drawable.opcionsalir);
        titleLogout.setText("Cerrar sesión");

        // Logout logic
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

                securePrefs.edit().clear().apply();

                startActivity(new Intent(this, MainActivity.class));
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
