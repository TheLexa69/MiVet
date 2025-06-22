package com.mivet.veterinaria.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.repository.MascotaRepository;
import com.mivet.veterinaria.API.repository.UsuarioRepository;

import java.util.List;

public class MascotaVM extends ViewModel {

    public MutableLiveData<PetInfo> mascotaLD = new MutableLiveData<>();
    public MutableLiveData<List<PetInfo>> mascotasAdopcionLD = new MutableLiveData<>();
    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;

    public MascotaVM(Context context) {
        this.mascotaRepository = new MascotaRepository(context);
        this.usuarioRepository = new UsuarioRepository(context);
    }

    public void cargarMascotaPorId(Long id) {
        mascotaRepository.getMascotaPorId(id, new MascotaRepository.MascotaCallback() {
            @Override
            public void onSuccess(PetInfo mascota) {
                mascotaLD.postValue(mascota);
            }

            @Override
            public void onFailure(Throwable t) {
                // Aquí puedes logear o añadir MutableLiveData<Throwable>
            }
        });
    }

    public void actualizarMascota(Long id, String nombre, String raza, String fechaNac, UsuarioRepository.OperacionCallback callback) {
        mascotaRepository.getMascotaPorId(id, new MascotaRepository.MascotaCallback() {
            @Override
            public void onSuccess(PetInfo pet) {
                pet.setNombre(nombre);
                pet.setRaza(raza);
                pet.setFechaNac(fechaNac);

                // Usa el repositorio ya inyectado
                usuarioRepository.actualizarMascota(id, pet, callback);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void eliminarMascota(Long id, MascotaRepository.OperacionCallback callback) {
        mascotaRepository.eliminarMascota(id, callback);
    }

    public void cargarMascotasProtectoras() {
        mascotaRepository.getTodasLasMascotasAdopcion(new MascotaRepository.MascotasCallback() {
            @Override
            public void onSuccess(List<PetInfo> mascotas) {
                Log.d("ADOPCION_DEBUG", "Mascotas recibidas: " + (mascotas != null ? mascotas.size() : 0));
                mascotasAdopcionLD.postValue(mascotas);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ADOPCION_DEBUG", "Error al cargar mascotas", t);
                mascotasAdopcionLD.postValue(null);
            }
        });
    }

}
