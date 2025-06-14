package com.mivet.veterinaria.helpers;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mivet.veterinaria.R;
import com.mivet.veterinaria.usuario.UsuarioMascotasActivity;
import com.mivet.veterinaria.usuario.UsuarioPerfilActivity;
import static com.mivet.veterinaria.helpers.SesionUtils.cerrarSesion;

public class DrawerUtils {

    public static void configurarDrawerUsuario(Activity activity, Toolbar toolbar) {
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawerLayout);
        NavigationView navigationView = activity.findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            int id = item.getItemId();

            if (id == R.id.action_perfil) {
                activity.startActivity(new Intent(activity, UsuarioPerfilActivity.class));
            } else if (id == R.id.action_mascotas) {
                activity.startActivity(new Intent(activity, UsuarioMascotasActivity.class));
            } else if (id == R.id.action_configuracion) {
                activity.startActivity(new Intent(activity, UsuarioMascotasActivity.class)); // ¿es correcto?
            } else if (id == R.id.action_cerrar_sesion) {
                cerrarSesion(activity);
            }

            return true;
        });
    }
}
