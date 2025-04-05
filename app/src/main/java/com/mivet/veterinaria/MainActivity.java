package com.mivet.veterinaria;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String LANG = "LANG";

    private SharedPreferences sharedPreferences;
    private String currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentLanguage = Locale.getDefault().getLanguage();

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

        // Cargar el idioma guardado
        String savedLanguage = sharedPreferences.getString(LANG, "es");
        setLocale(savedLanguage);

        Spinner languageSpinner = findViewById(R.id.language_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Configurar el spinner para que seleccione el idioma correcto por defecto
        Log.d("TAG", "Saved language: " + savedLanguage);
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
                        Log.d("TAG", "Selected language: English");
                        break;
                    case 1:
                        selectedLang = "es";
                        Log.d("TAG", "Selected language: Spanish");
                        break;
                    case 2:
                        selectedLang = "pt";
                        Log.d("TAG", "Selected language: Portuguese");
                        break;
                }

                // Solo cambiar el idioma si es diferente al actual
                if (!selectedLang.equals(currentLanguage)) {
                    currentLanguage = selectedLang;
                    // Guardar el idioma en SharedPreferences
                    sharedPreferences.edit()
                            .putString(LANG, selectedLang)
                            .apply();
                    setLocale(selectedLang);
                    recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "Nothing selected on spinner");
            }
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
        // No necesitamos recreate() aquí, ya que solo aplicamos el idioma
        // La actividad ya reflejará los cambios de idioma automáticamente
    }
}