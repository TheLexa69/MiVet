package com.mivet.veterinaria.usuario;

import static com.mivet.veterinaria.helpers.SesionUtils.cerrarSesion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.Mensaje;
import com.mivet.veterinaria.API.models.Usuario;
import com.mivet.veterinaria.API.repository.UsuarioRepository;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.viewmodels.MascotaVM;
import com.mivet.veterinaria.viewmodels.MascotaVMFactory;

import java.util.List;

public class UsuarioMenuActivity extends AppCompatActivity {

    public static String USERNAME;
    public static String USEREMAIL;
    public static String USERTYPE;
    public static String USERROLE;
    private UsuarioRepository usuarioRepository;
    private ImageView userNotificationImage;
    private TextView tvWelcome;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_menu);

        // 1. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // 3. Toggle para el botón hamburguesa
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 4. Listener del menú lateral
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawers(); // Cierra el drawer tras clic

            if (id == R.id.action_perfil) {
                startActivity(new Intent(this, UsuarioPerfilActivity.class));
            } else if (id == R.id.action_mascotas) {
                startActivity(new Intent(this, UsuarioMascotasActivity.class));
            } else if (id == R.id.action_configuracion) {
                startActivity(new Intent(this, UsuarioMascotasActivity.class));
            } else if (id == R.id.action_cerrar_sesion) {
                cerrarSesion(this);
            }
            return true;
        });
        //FIN DEL TOOLBAR

        usuarioRepository = new UsuarioRepository(this);

        userNotificationImage = findViewById(R.id.userNotificationImage);
        tvWelcome = findViewById(R.id.tvWelcome);

        //NOTIFICACIONES
        userNotificationImage.setOnClickListener(v -> {
            Intent intent = new Intent(UsuarioMenuActivity.this, UsuarioMensajesActivity.class);
            startActivity(intent);
        });

        getUserData();

        cargarMensajes();



        // Tarjeta 1
        View cardPerfil = findViewById(R.id.card1);
        ImageView icon1 = cardPerfil.findViewById(R.id.imgAnimal);
        TextView title1 = cardPerfil.findViewById(R.id.tvNombreAnimal);
        icon1.setImageResource(R.drawable.opcionperfil);
        title1.setText("Perfil");

        // Tarjeta 2 - Falta entrar en info mascota, editarla y borrarla
        View cardMisMascotas = findViewById(R.id.card2);
        ImageView icon2 = cardMisMascotas.findViewById(R.id.imgAnimal);
        TextView title2 = cardMisMascotas.findViewById(R.id.tvNombreAnimal);
        icon2.setImageResource(R.drawable.opcionhuella2);
        title2.setText("Mis mascotas");

        // Tarjeta 3
        View cardMisCitas = findViewById(R.id.card3);
        ImageView icon3 = cardMisCitas.findViewById(R.id.imgAnimal);
        TextView title3 = cardMisCitas.findViewById(R.id.tvNombreAnimal);
        icon3.setImageResource(R.drawable.opcioncitas);
        title3.setText("Citas");

        // Tarjeta 4
        View cardMisGastos = findViewById(R.id.card4);
        ImageView icon4 = cardMisGastos.findViewById(R.id.imgAnimal);
        TextView title4 = cardMisGastos.findViewById(R.id.tvNombreAnimal);
        icon4.setImageResource(R.drawable.opciongastos);
        title4.setText("Gastos");

        // Tarjeta 5
        View card5 = findViewById(R.id.card5);
        ImageView icon5 = card5.findViewById(R.id.imgAnimal);
        TextView title5 = card5.findViewById(R.id.tvNombreAnimal);
        icon5.setImageResource(R.drawable.opcionajustes);
        title5.setText("Adopción");

        // Tarjeta 6
        View card6 = findViewById(R.id.card6);
        ImageView icon6 = card6.findViewById(R.id.imgAnimal);
        TextView title6 = card6.findViewById(R.id.tvNombreAnimal);
        icon6.setImageResource(R.drawable.opcionajustes);
        title6.setText("Ajustes");


        //CARD DEL PERFIL
        cardPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(UsuarioMenuActivity.this, UsuarioPerfilActivity.class);
            startActivity(intent);
        });

        //CARD DE MIS MASCOTAS
        cardMisMascotas.setOnClickListener(v -> {
            Intent intent = new Intent(UsuarioMenuActivity.this, UsuarioMascotasActivity.class);
            startActivity(intent);
        });

        //CARD DE MIS CITAS
        cardMisCitas.setOnClickListener(v -> {
            Intent intent = new Intent(UsuarioMenuActivity.this, UsuarioCitasActivity.class);
            startActivity(intent);
        });

        //CARD DE GASTOS
        cardMisGastos.setOnClickListener(v -> {
            Intent intent = new Intent(UsuarioMenuActivity.this, UsuarioGastosActivity.class);
            startActivity(intent);
        });

        RecyclerView rvAdopcion = findViewById(R.id.rvAdopcion);
        MascotasAdopcionAdapter adopcionAdapter = new MascotasAdopcionAdapter();
        rvAdopcion.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAdopcion.setAdapter(adopcionAdapter);

        // observar el LiveData del VM
        MascotaVMFactory factory = new MascotaVMFactory(this);
        MascotaVM mascotaVM = new ViewModelProvider(this, factory).get(MascotaVM.class);
        mascotaVM.mascotasAdopcionLD.observe(this, adopcionAdapter::setMascotas);
        mascotaVM.cargarMascotasProtectoras();


    }

    //CONFIGURACIÓN DEL MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_perfil) {
            startActivity(new Intent(this, UsuarioPerfilActivity.class));
            return true;
        } else if (id == R.id.action_mascotas) {
            startActivity(new Intent(this, UsuarioMascotasActivity.class));
            return true;
        } else if (id == R.id.action_configuracion) {
            startActivity(new Intent(this, UsuarioMascotasActivity.class));
            return true;
        } else if (id == R.id.action_cerrar_sesion) {
            cerrarSesion(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //FIN DE CONFIGURACIÓN DEL MENU


    private void getUserData() {
        usuarioRepository.getPerfil(new UsuarioRepository.UsuarioCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                USERNAME = usuario.getNombre();
                USEREMAIL = usuario.getCorreo();
                USERTYPE = usuario.getTipoUsuario();
                USERROLE = usuario.getRol();

                runOnUiThread(() -> tvWelcome.setText("Bienvenido " + USERNAME + "!"));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("UsuarioMenuActivity", "Error al obtener perfil", t);
            }
        });
    }

    private void cargarMensajes() {
        usuarioRepository.getMensajes(new UsuarioRepository.MensajesCallback() {
            @Override
            public void onSuccess(List<Mensaje> mensajes) {
                boolean hayNoLeidos = false;

                for (Mensaje mensaje : mensajes) {
                    Log.d("MENSAJE_USUARIO", "ID: " + mensaje.getId() +
                            ", Usuario: " + mensaje.getIdUsuario() +
                            ", Titulo: " + mensaje.getTitulo() +
                            ", Leído: " + mensaje.isLeido() +
                            ", Fecha: " + mensaje.getFechaEnvio() +
                            ", Contenido: " + mensaje.getCuerpo());

                    if (!mensaje.isLeido()) {
                        hayNoLeidos = true;
                    }
                }

                int icono = hayNoLeidos ? R.drawable.opcionnotificacionon : R.drawable.opcionnotificacion;
                runOnUiThread(() -> userNotificationImage.setImageResource(icono));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("MENSAJE_USUARIO", "Error al obtener mensajes", t);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        getUserData(); // Esto refresca los datos del usuario cada vez que se vuelve a esta pantalla
        cargarMensajes(); // Esto refresca los mensajes cada vez que se vuelve a esta pantalla
    }

    private class MascotasAdopcionAdapter extends RecyclerView.Adapter<MascotasAdopcionAdapter.MascotaViewHolder> {
        private List<PetInfo> mascotas;

        public void setMascotas(List<PetInfo> mascotas) {
            this.mascotas = mascotas;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mascota_adopcion, parent, false);
            return new MascotaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
            PetInfo mascota = mascotas.get(position);
            holder.tvNombre.setText(mascota.getNombre());
            holder.tvRaza.setText(mascota.getRaza());
            holder.tvTipo.setText(mascota.getTipo());
            holder.tvFechaNac.setText(mascota.getFechaNac());
            holder.tvDescripcion.setText(mascota.getDescripcion() != null ? mascota.getDescripcion() : "");

            switch (mascota.getTipo()) {
                case "gato":
                    holder.imgAnimal.setImageResource(R.drawable.gatocolorido);
                    break;
                case "exotico":
                    holder.imgAnimal.setImageResource(R.drawable.iguanacolorida);
                    break;
                default:
                    holder.imgAnimal.setImageResource(R.drawable.perrocolorido);
                    break;
            }

            holder.btnSolicitarAdopcion.setOnClickListener(v -> {
                Toast.makeText(UsuarioMenuActivity.this, "Solicitud de adopción pendiente", Toast.LENGTH_SHORT).show();
                // LOGICA DEL BOTON DE ADOPCIÓN, SE VA ABRIR UN MODEL PARA PONER EL TEXTO DE SOLICITUD
            });
        }

        @Override
        public int getItemCount() {
            return mascotas != null ? mascotas.size() : 0;
        }

        class MascotaViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombre, tvRaza, tvTipo, tvFechaNac, tvDescripcion;
            ImageView imgAnimal;
            Button btnSolicitarAdopcion;

            public MascotaViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombre = itemView.findViewById(R.id.tvNombre);
                tvRaza = itemView.findViewById(R.id.tvRaza);
                tvTipo = itemView.findViewById(R.id.tvTipo);
                tvFechaNac = itemView.findViewById(R.id.tvFechaNac);
                tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
                imgAnimal = itemView.findViewById(R.id.imgAnimal);
                btnSolicitarAdopcion = itemView.findViewById(R.id.btnSolicitarAdopcion);
            }
        }
    }


}