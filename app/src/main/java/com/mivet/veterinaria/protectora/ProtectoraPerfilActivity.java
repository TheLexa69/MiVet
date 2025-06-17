package com.mivet.veterinaria.protectora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mivet.veterinaria.API.models.Protectora;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.viewmodels.ProtectoraVM;
import com.mivet.veterinaria.viewmodels.ProtectoraVMFactory;

public class ProtectoraPerfilActivity extends AppCompatActivity {

    private ProtectoraVM viewModel;

    private EditText etNombre, etCif, etTelefono, etWeb, etCodigoONG, etDireccion,
            etFacebook, etInstagram, etTiktok, etLinkedin;

    private Button btnEditar, btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protectora_perfil);

        // ViewModel
        ProtectoraVMFactory factory = new ProtectoraVMFactory(this);
        viewModel = new ViewModelProvider(this, factory).get(ProtectoraVM.class);

        // Referencias UI
        etNombre = findViewById(R.id.etNombre);
        etCif = findViewById(R.id.etCif);
        etTelefono = findViewById(R.id.etTelefono);
        etWeb = findViewById(R.id.etWeb);
        etCodigoONG = findViewById(R.id.etCodigoONG);
        etDireccion = findViewById(R.id.etDireccion);
        etFacebook = findViewById(R.id.etFacebook);
        etInstagram = findViewById(R.id.etInstagram);
        etTiktok = findViewById(R.id.etTiktok);
        etLinkedin = findViewById(R.id.etLinkedin);

        btnEditar = findViewById(R.id.btnEditar);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Desactivar edición inicialmente
        setEditable(false);

        // Observador del perfil
        viewModel.protectoraLD.observe(this, protectora -> {
            if (protectora != null) {
                cargarDatosEnFormulario(protectora);
            }
        });

        viewModel.cargarPerfil();

        btnEditar.setOnClickListener(v -> setEditable(true));

        btnGuardar.setOnClickListener(v -> {
            Protectora actualizada = new Protectora();

            actualizada.setNombre(etNombre.getText().toString().trim());
            actualizada.setCif(etCif.getText().toString().trim());
            actualizada.setTelefono(etTelefono.getText().toString().trim());
            actualizada.setWeb(etWeb.getText().toString().trim());
            actualizada.setCodigoONG(etCodigoONG.getText().toString().trim());
            actualizada.setDireccion(etDireccion.getText().toString().trim());
            actualizada.setFacebook(etFacebook.getText().toString().trim());
            actualizada.setInstagram(etInstagram.getText().toString().trim());
            actualizada.setTiktok(etTiktok.getText().toString().trim());
            actualizada.setLinkedin(etLinkedin.getText().toString().trim());

            viewModel.actualizarPerfil(actualizada,
                    () -> {
                        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        setEditable(false);
                    },
                    error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
            );
        });
    }

    private void setEditable(boolean editable) {
        etNombre.setEnabled(editable);
        etCif.setEnabled(editable);
        etTelefono.setEnabled(editable);
        etWeb.setEnabled(editable);
        etCodigoONG.setEnabled(editable);
        etDireccion.setEnabled(editable);
        etFacebook.setEnabled(editable);
        etInstagram.setEnabled(editable);
        etTiktok.setEnabled(editable);
        etLinkedin.setEnabled(editable);

        btnGuardar.setVisibility(editable ? View.VISIBLE : View.GONE);
        btnEditar.setVisibility(!editable ? View.VISIBLE : View.GONE);
    }

    private void cargarDatosEnFormulario(Protectora p) {
        etNombre.setText(p.getNombre());
        etCif.setText(p.getCif());
        etTelefono.setText(p.getTelefono());
        etWeb.setText(p.getWeb());
        etCodigoONG.setText(p.getCodigoONG());
        etDireccion.setText(p.getDireccion());
        etFacebook.setText(p.getFacebook());
        etInstagram.setText(p.getInstagram());
        etTiktok.setText(p.getTiktok());
        etLinkedin.setText(p.getLinkedin());
    }
}
