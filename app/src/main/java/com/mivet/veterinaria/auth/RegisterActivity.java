package com.mivet.veterinaria.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
    private final List<String> DATOS_USUARIO = new ArrayList<>();
    private ImageView imgPerro, imgGato, imgIguana;
    private EditText etNombre, etCorreo, etContrasena;

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
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etEmail);
        etContrasena = findViewById(R.id.etContra);




        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setEnabled(false);
        btnSiguiente.setOnClickListener(v -> {
            if (validarDatosUsuario()) {
                String nombre = etNombre.getText().toString().trim();
                String correo = etCorreo.getText().toString().trim();
                String contrasena = etContrasena.getText().toString().trim();

                DATOS_USUARIO.clear();
                DATOS_USUARIO.add(nombre);
                DATOS_USUARIO.add(correo);
                DATOS_USUARIO.add(contrasena);

                Intent intent = new Intent(RegisterActivity.this, PetRegisterActivity.class);
                intent.putStringArrayListExtra("ANIMALES", (ArrayList<String>) ANIMALES);
                intent.putStringArrayListExtra("DATOS_USUARIO", (ArrayList<String>) DATOS_USUARIO);
                startActivity(intent);
            }
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

    private boolean validarDatosUsuario() {
        boolean esValido = true;

        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("Campo obligatorio");
            esValido = false;
        }

        if (etCorreo.getText().toString().trim().isEmpty()) {
            etCorreo.setError("Campo obligatorio");
            esValido = false;
        }

        if (etContrasena.getText().toString().trim().isEmpty()) {
            etContrasena.setError("Campo obligatorio");
            esValido = false;
        }

        return esValido;
    }

}