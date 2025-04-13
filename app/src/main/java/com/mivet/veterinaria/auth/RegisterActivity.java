package com.mivet.veterinaria.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mivet.veterinaria.API.models.Animal;
import com.mivet.veterinaria.MainActivity;
import com.mivet.veterinaria.R;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private int cantidadChecks = 0;
    private Button btnSiguiente;
//    private List<Animal> animales = new ArrayList<>();
    private final List<String> ANIMALES = new ArrayList<>();
    private ImageView imgPerro, imgGato, imgIguana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        // Inicialización de variables
        imgGato = findViewById(R.id.imgGato);
        imgPerro = findViewById(R.id.imgPerro);
        imgIguana = findViewById(R.id.imgIguana);

        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setEnabled(false);
        btnSiguiente.setOnClickListener(v -> {
            // Aquí puedes iniciar la siguiente actividad
            Intent intent = new Intent(RegisterActivity.this, PetRegisterActivity.class);
            // Pasar la lista de animales seleccionados a la siguiente actividad
            intent.putStringArrayListExtra("ANIMALES", (ArrayList<String>) ANIMALES);
            startActivity(intent);
        });
    }

    // Boton para volver
    public void onBackButtonClick(View view) {
        finish();
    }

    // Checkbox DE PERROS
    public void onCheckboxContainerClick1(View view) {
        CheckBox checkBox = view.findViewById(R.id.checkBox1);
        if (checkBox != null) {
            if (checkBox.isChecked()) {
                cantidadChecks--;
                if (cantidadChecks == 0) {
                    btnSiguiente.setEnabled(false);
                    btnSiguiente.setText("Continuar");
                } else {
                    btnSiguiente.setText("Continuar 1 / " + cantidadChecks);
                }
                ANIMALES.remove("Perro");
                imgPerro.setImageResource(R.drawable.perro);
            } else {
                cantidadChecks++;
                btnSiguiente.setEnabled(true);
                btnSiguiente.setText("Continuar 1 / " + cantidadChecks);
                ANIMALES.add("Perro");
                imgPerro.setImageResource(R.drawable.perrocolorido);
            }
            Log.d("TAG", "Cantidad de checkboxes seleccionados: " + cantidadChecks);
            Log.d("TAG", "ArrayList: " + ANIMALES);
            checkBox.setChecked(!checkBox.isChecked());
        }
    }

    // Checkbox DE GATOS
    public void onCheckboxContainerClick2(View view) {
        CheckBox checkBox = view.findViewById(R.id.checkBox2);
        if (checkBox != null) {
            if (checkBox.isChecked()) {
                cantidadChecks--;
                if (cantidadChecks == 0) {
                    btnSiguiente.setEnabled(false);
                    btnSiguiente.setText("Continuar");
                } else {
                    btnSiguiente.setText("Continuar 1 / " + cantidadChecks);
                }
                ANIMALES.remove("Gato");
                imgGato.setImageResource(R.drawable.gato);
            } else {
                cantidadChecks++;
                btnSiguiente.setEnabled(true);
                btnSiguiente.setText("Continuar 1 / " + cantidadChecks);
                ANIMALES.add("Gato");
                imgGato.setImageResource(R.drawable.gatocolorido);
            }
            Log.d("TAG", "Cantidad de checkboxes seleccionados: " + cantidadChecks);
            Log.d("TAG", "ArrayList: " + ANIMALES);
            checkBox.setChecked(!checkBox.isChecked());
        }
    }

    // Checkbox DE EXOTICOS
    public void onCheckboxContainerClick3(View view) {
        CheckBox checkBox = view.findViewById(R.id.checkBox3);
        if (checkBox != null) {
            if (checkBox.isChecked()) {
                cantidadChecks--;
                if (cantidadChecks == 0) {
                    btnSiguiente.setEnabled(false);
                    btnSiguiente.setText("Continuar");
                } else {
                    btnSiguiente.setText("Continuar 1 / " + cantidadChecks);
                }
                ANIMALES.remove("Exotico");
                imgIguana.setImageResource(R.drawable.iguana);
            } else {
                cantidadChecks++;
                btnSiguiente.setEnabled(true);
                btnSiguiente.setText("Continuar 1 / " + cantidadChecks);
                ANIMALES.add("Exotico");
                imgIguana.setImageResource(R.drawable.iguanacolorida);
            }
            Log.d("TAG", "Cantidad de checkboxes seleccionados: " + cantidadChecks);
            Log.d("TAG", "ArrayList: " + ANIMALES);
            checkBox.setChecked(!checkBox.isChecked());
        }
    }
}