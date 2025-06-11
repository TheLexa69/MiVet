package com.mivet.veterinaria.usuario;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.viewmodels.UsuarioVM;
import com.mivet.veterinaria.viewmodels.UsuarioVMFactory;

public class UsuarioMascotasActivity extends AppCompatActivity {
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_mascotas);

        rv = findViewById(R.id.rvMascotas);

        UsuarioVMFactory factory = new UsuarioVMFactory(this); // this = Context
        UsuarioVM usuarioVM = new ViewModelProvider(this, factory).get(UsuarioVM.class);

        usuarioVM.mascotasLD.observe(this, mascotas -> {
            if (mascotas != null && !mascotas.isEmpty()) {
                rv.setLayoutManager(new LinearLayoutManager(this));
                rv.setAdapter(new RecyclerView.Adapter() {

                    class MascotaViewHolder extends RecyclerView.ViewHolder {
                        public TextView tvNombreAnimal, tvRazaAnimal, tvFechaNac;
                        public ImageView imgAnimal, imgIrInfoAnimal;
                        public ConstraintLayout cl;

                        public MascotaViewHolder(@NonNull View view) {
                            super(view);
                            tvNombreAnimal = view.findViewById(R.id.tvNombreAnimal);
                            tvRazaAnimal = view.findViewById(R.id.tvRazaAnimal);
                            tvFechaNac = view.findViewById(R.id.tvFechaNac);
                            imgAnimal = view.findViewById(R.id.imgAnimal);
                            imgIrInfoAnimal = view.findViewById(R.id.imgIrInfoAnimal);
                            cl = view.findViewById(R.id.animalCardConstraint);
                        }
                    }

                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_animal_card, parent, false);
                        return new MascotaViewHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                        PetInfo mascota = mascotas.get(position);
                        MascotaViewHolder mvh = (MascotaViewHolder) holder;
                        Log.d("UsuarioMascotasActivity", "Mascota: " + mascota);
                        mvh.tvNombreAnimal.setText(mascota.getNombre());
                        mvh.tvRazaAnimal.setText(mascota.getRaza());
                        mvh.tvFechaNac.setText(mascota.getFechaNac());
                    }

                    @Override
                    public int getItemCount() {
                        return mascotas.size();
                    }
                });
            }
        });

        usuarioVM.cargarMascotas();

    }
}