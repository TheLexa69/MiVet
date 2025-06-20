package com.mivet.veterinaria.helpers;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.usuario.UsuarioMascotasActivity;
import com.mivet.veterinaria.usuario.UsuarioMensajesActivity;
import com.mivet.veterinaria.usuario.UsuarioMenuActivity;
import com.mivet.veterinaria.usuario.UsuarioPerfilActivity;
import static com.mivet.veterinaria.helpers.SesionUtils.cerrarSesion;

public class DrawerUtils {

    public static void configurarDrawerUsuario(Activity activity, Toolbar toolbar) {
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawerLayout);
        NavigationView navigationView = activity.findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(activity, android.R.color.white));


        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            int id = item.getItemId();

            if (id == R.id.action_perfil) {
                activity.startActivity(new Intent(activity, UsuarioPerfilActivity.class));
            } else if (id == R.id.action_mensajes) {
                activity.startActivity(new Intent(activity, UsuarioMensajesActivity.class));
            }else if (id == R.id.action_inicio) {
                    activity.startActivity(new Intent(activity, UsuarioMenuActivity.class));
            } else if (id == R.id.action_mascotas) {
                activity.startActivity(new Intent(activity, UsuarioMascotasActivity.class));
            } else if (id == R.id.action_cerrar_sesion) {
                cerrarSesion(activity);
            }

            return true;
        });
    }
}
