package com.mivet.veterinaria.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.Usuario;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.Usuario.UsuarioMenuActivity;
import com.mivet.veterinaria.network.LoginConnectionClass;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PetRegisterActivity extends AppCompatActivity {

    private List<String> animales;
    private List<String> datosUsuario;
    private int animalIndex = 0;

    private LinearLayout petContainer;
    private Button btnNext;
    private ImageButton btnPlus, btnMinus;
    private TextView tvCounter, tvTitle;

    private int petCount = 1; // Mínimo 1 por especie
    private final List<PetInfo> mascotasRegistradas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_register);

        animales = getIntent().getStringArrayListExtra("ANIMALES");
        datosUsuario = getIntent().getStringArrayListExtra("DATOS_USUARIO");

        petContainer = findViewById(R.id.petContainer);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        btnNext = findViewById(R.id.btnNext);
        tvCounter = findViewById(R.id.tvCounter);
        tvTitle = findViewById(R.id.tvTitle);

        mostrarTipoActual();

        btnPlus.setOnClickListener(v -> {
            petCount++;
            tvCounter.setText(String.valueOf(petCount));
            agregarTarjeta(petCount);
        });

        btnMinus.setOnClickListener(v -> {
            if (petCount > 1) {
                petCount--;
                tvCounter.setText(String.valueOf(petCount));
                eliminarUltimaTarjeta();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (validarCampos()) {
                guardarDatosDeTarjetas();
                avanzarOTerminar();
            }
        });
    }

    private void mostrarTipoActual() {
        petCount = 1;
        petContainer.removeAllViews();
        tvCounter.setText(String.valueOf(petCount));

        if (animalIndex < animales.size()) {
            String tipo = animales.get(animalIndex);
            String tipoTraducido = getTranslatedAnimalName(tipo);
            String titulo = getString(R.string.pet_info_title, tipoTraducido);
            tvTitle.setText(titulo);
            agregarTarjeta(1);
        }
    }

    private void agregarTarjeta(int numero) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.item_pet_card, petContainer, false);

        TextView tvPetIndex = cardView.findViewById(R.id.tvPetIndex);
        tvPetIndex.setText("Pet " + numero);

        EditText etBirth = cardView.findViewById(R.id.etBirth);
        etBirth.setFocusable(false);
        etBirth.setOnClickListener(v -> mostrarDatePicker(etBirth));

        petContainer.addView(cardView);
    }

    private void mostrarDatePicker(EditText etBirth) {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                PetRegisterActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String fecha = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etBirth.setText(fecha);
                },
                year, month, day
        );

        // ⛔️ Limitar fecha máxima a hoy
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        dialog.show();
    }

    private void eliminarUltimaTarjeta() {
        if (petContainer.getChildCount() > 0) {
            petContainer.removeViewAt(petContainer.getChildCount() - 1);
        }
    }

    private boolean validarCampos() {
        boolean todosValidos = true;

        for (int i = 0; i < petContainer.getChildCount(); i++) {
            View card = petContainer.getChildAt(i);

            EditText etName = card.findViewById(R.id.etName);
            EditText etBreed = card.findViewById(R.id.etBreed);
            EditText etBirth = card.findViewById(R.id.etBirth);

            String name = etName.getText().toString().trim();
            String breed = etBreed.getText().toString().trim();
            String birth = etBirth.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Obligatorio");
                todosValidos = false;
            }
            if (breed.isEmpty()) {
                etBreed.setError("Obligatorio");
                todosValidos = false;
            }
            if (birth.isEmpty()) {
                etBirth.setError("Obligatorio");
                todosValidos = false;
            }
        }

        return todosValidos;
    }

    private void guardarDatosDeTarjetas() {
        String tipo = animales.get(animalIndex);

        for (int i = 0; i < petContainer.getChildCount(); i++) {
            View card = petContainer.getChildAt(i);

            EditText etName = card.findViewById(R.id.etName);
            EditText etBreed = card.findViewById(R.id.etBreed);
            EditText etBirth = card.findViewById(R.id.etBirth);

            String name = etName.getText().toString().trim();
            String breed = etBreed.getText().toString().trim();
            String birth = etBirth.getText().toString().trim();

            mascotasRegistradas.add(new PetInfo(tipo, name, breed, birth));
        }
    }

    private void avanzarOTerminar() {
        animalIndex++;

        if (animalIndex < animales.size()) {
            mostrarTipoActual();
        } else {
            Usuario nuevoUsuario = new Usuario(
                    datosUsuario.get(0),
                    datosUsuario.get(1),
                    datosUsuario.get(2),
                    mascotasRegistradas
            );

            Log.d("Usuario", nuevoUsuario.toString());

            // Llamar al endpoint en un hilo separado
            new Thread(() -> {
                JSONObject response = LoginConnectionClass.register(nuevoUsuario);
                Log.d("Registro", "Respuesta del servidor: " + response.toString());

                boolean success = response.optBoolean("success", false);
                if (success) {
                    // Obtener datos del token, user_id y rol
                    String token = response.optString("token", "");
                    int userId = response.optInt("user_id", -1);
                    String rol = response.optString("rol", "");

                    // Guardar token y user_id en SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("MiVetPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("token", token);
                    editor.putInt("user_id", userId);
                    editor.putString("rol", rol);
                    editor.apply();

                    // Redirigir al menú de usuario
                    runOnUiThread(() -> {
                        Intent intent = new Intent(PetRegisterActivity.this, UsuarioMenuActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    String msg = response.optString("message", "Error en el registro");
                    Log.e("Registro", "Falló: " + msg);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registro fallido: " + msg, Toast.LENGTH_LONG).show();
                    });
                }
            }).start();
        }
    }

    private String getTranslatedAnimalName(String tipo) {
        switch (tipo) {
            case "Perro":
                return getString(R.string.animal_dog);
            case "Gato":
                return getString(R.string.animal_cat);
            case "Exotico":
                return getString(R.string.animal_exotic);
            default:
                return tipo;
        }
    }
}
