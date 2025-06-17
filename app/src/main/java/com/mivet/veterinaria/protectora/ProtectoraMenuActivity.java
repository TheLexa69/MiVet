package com.mivet.veterinaria.protectora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.mivet.veterinaria.MainActivity;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.helpers.SesionUtils;
import com.mivet.veterinaria.viewmodels.ProtectoraVM;
import com.mivet.veterinaria.viewmodels.ProtectoraVMFactory;

public class ProtectoraMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protectora_menu);
        TextView tvWelcome = findViewById(R.id.tvWelcome);

        ProtectoraVMFactory factory = new ProtectoraVMFactory(this);
        ProtectoraVM vm = new ViewModelProvider(this, factory).get(ProtectoraVM.class);

        vm.protectoraLD.observe(this, dto -> {
            tvWelcome.setText("Bienvenida, " + dto.getNombre());
        });

        vm.cargarPerfil();

        // Card 1 - Adopciones
        View cardAdopciones = findViewById(R.id.cardAdopciones);
        ImageView iconAdopciones = cardAdopciones.findViewById(R.id.imgAnimal);
        TextView titleAdopciones = cardAdopciones.findViewById(R.id.tvNombreAnimal);
        iconAdopciones.setImageResource(R.drawable.opcionnotificacion);
        titleAdopciones.setText("Adopciones");

        // Card 2 - Animales
        View cardAnimales = findViewById(R.id.cardAnimales);
        ImageView iconAnimales = cardAnimales.findViewById(R.id.imgAnimal);
        TextView titleAnimales = cardAnimales.findViewById(R.id.tvNombreAnimal);
        iconAnimales.setImageResource(R.drawable.opcionhuella2);
        titleAnimales.setText("Animales");

        // Card 3 - Perfil
        View cardPerfil = findViewById(R.id.cardPerfil);
        ImageView iconPerfil = cardPerfil.findViewById(R.id.imgAnimal);
        TextView titlePerfil = cardPerfil.findViewById(R.id.tvNombreAnimal);
        iconPerfil.setImageResource(R.drawable.opcionperfil);
        titlePerfil.setText("Perfil");

        // Card 4 - Logout
        View cardLogout = findViewById(R.id.cardLogout);
        ImageView iconLogout = cardLogout.findViewById(R.id.imgAnimal);
        TextView titleLogout = cardLogout.findViewById(R.id.tvNombreAnimal);
        iconLogout.setImageResource(R.drawable.opcionsalir);
        titleLogout.setText("Cerrar sesión");

        // Logout logic
        cardLogout.setOnClickListener(v -> {
            SesionUtils.cerrarSesion(this);
        });

        cardAdopciones.setOnClickListener(v -> {
            startActivity(new Intent(this, ProtectoraAdopcionesActivity.class));
        });

        cardAnimales.setOnClickListener(v -> {
            startActivity(new Intent(this, ProtectoraMascotasActivity.class));
        });

        cardPerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, ProtectoraPerfilActivity.class));
        });
    }
}
