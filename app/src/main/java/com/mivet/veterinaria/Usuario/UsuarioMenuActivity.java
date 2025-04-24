package com.mivet.veterinaria.Usuario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.mivet.veterinaria.MainActivity;
import com.mivet.veterinaria.R;

public class UsuarioMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_menu);

        // Tarjeta 1
        View card1 = findViewById(R.id.card1);
        ImageView icon1 = card1.findViewById(R.id.imgCardIcon);
        TextView title1 = card1.findViewById(R.id.tvCardTitle);
        icon1.setImageResource(R.drawable.opcionperfil); // tu icono
        title1.setText("Perfil");

        // Tarjeta 2
        View card2 = findViewById(R.id.card2);
        ImageView icon2 = card2.findViewById(R.id.imgCardIcon);
        TextView title2 = card2.findViewById(R.id.tvCardTitle);
        icon2.setImageResource(R.drawable.opcionhuella2); // tu icono
        title2.setText("Mis mascotas");

        // Tarjeta 3
        View card3 = findViewById(R.id.card3);
        ImageView icon3 = card3.findViewById(R.id.imgCardIcon);
        TextView title3 = card3.findViewById(R.id.tvCardTitle);
        icon3.setImageResource(R.drawable.opcioncitas); // tu icono
        title3.setText("Citas");

        // Tarjeta 4
        View card4 = findViewById(R.id.card4);
        ImageView icon4 = card4.findViewById(R.id.imgCardIcon);
        TextView title4 = card4.findViewById(R.id.tvCardTitle);
        icon4.setImageResource(R.drawable.opciongastos); // tu icono
        title4.setText("Gastos");

        // Tarjeta 5
        View card5 = findViewById(R.id.card5);
        ImageView icon5 = card5.findViewById(R.id.imgCardIcon);
        TextView title5 = card5.findViewById(R.id.tvCardTitle);
        icon5.setImageResource(R.drawable.opcionajustes); // tu icono
        title5.setText("Adopción");

        // Tarjeta 6
        View card6 = findViewById(R.id.card6);
        ImageView icon6 = card6.findViewById(R.id.imgCardIcon);
        TextView title6 = card6.findViewById(R.id.tvCardTitle);
        icon6.setImageResource(R.drawable.opcionajustes); // tu icono
        title6.setText("Ajustes");

        // Tarjeta Logout
        View cardLogout = findViewById(R.id.cardLogout);
        ImageView iconLogout = cardLogout.findViewById(R.id.imgCardIcon);
        TextView titleLogout = cardLogout.findViewById(R.id.tvCardTitle);
        iconLogout.setImageResource(R.drawable.opcionhuella); // un icono de logout
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

    }
}