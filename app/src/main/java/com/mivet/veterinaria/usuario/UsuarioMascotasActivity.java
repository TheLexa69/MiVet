package com.mivet.veterinaria.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.mascota.InfoMascotaActivity;
import com.mivet.veterinaria.viewmodels.UsuarioVM;
import com.mivet.veterinaria.viewmodels.UsuarioVMFactory;

import java.util.List;

public class UsuarioMascotasActivity extends AppCompatActivity {
    private RecyclerView rv;
    private UsuarioVM usuarioVM;
    private MascotasAdapter adapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_mascotas);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawers();

            if (id == R.id.action_perfil) {
                startActivity(new Intent(this, UsuarioPerfilActivity.class));
            } else if (id == R.id.action_mascotas) {
                //estamos aqui
            } else if (id == R.id.action_configuracion) {
                startActivity(new Intent(this, UsuarioMenuActivity.class)); // o ajustes
            } else if (id == R.id.action_cerrar_sesion) {
                com.mivet.veterinaria.helpers.SesionUtils.cerrarSesion(this);
            }
            return true;
        });


        rv = findViewById(R.id.rvMascotas);
        rv.setLayoutManager(new LinearLayoutManager(this));

        UsuarioVMFactory factory = new UsuarioVMFactory(this);
        usuarioVM = new ViewModelProvider(this, factory).get(UsuarioVM.class);

        adapter = new MascotasAdapter();
        rv.setAdapter(adapter);

        usuarioVM.mascotasLD.observe(this, mascotas -> {
            if (mascotas != null) {
                adapter.setMascotas(mascotas);
            }
        });

        usuarioVM.cargarMascotas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        usuarioVM.cargarMascotas();
    }

    private class MascotasAdapter extends RecyclerView.Adapter<MascotasAdapter.MascotaViewHolder> {
        private List<PetInfo> mascotas;

        public void setMascotas(List<PetInfo> mascotas) {
            this.mascotas = mascotas;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_animal_card, parent, false);
            return new MascotaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
            PetInfo mascota = mascotas.get(position);
            holder.tvNombreAnimal.setText(mascota.getNombre());
            holder.tvRazaAnimal.setText(mascota.getRaza());
            holder.tvFechaNac.setText(mascota.getFechaNac());

            holder.imgIrInfoAnimal.setOnClickListener(v -> {
                Intent intent = new Intent(UsuarioMascotasActivity.this, InfoMascotaActivity.class);
                intent.putExtra("ID_MASCOTA", Long.parseLong(mascota.getId()));
                Log.d("UsuarioMascotasActivity", mascota.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return (mascotas != null) ? mascotas.size() : 0;
        }

        class MascotaViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombreAnimal, tvRazaAnimal, tvFechaNac;
            ImageView imgAnimal, imgIrInfoAnimal;
            ConstraintLayout cl;

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
    }
}
