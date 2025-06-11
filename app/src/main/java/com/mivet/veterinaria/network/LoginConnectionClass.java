package com.mivet.veterinaria.network;

import android.util.Log;

import com.mivet.veterinaria.API.dto.PetInfo;
import com.mivet.veterinaria.API.models.Usuario;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginConnectionClass {

    private static final String TAG = "LoginDebug";
    private static final String BASE_URL = "https://mivet-login-api.luachea.es";

    public static JSONObject login(String email, String contrasena) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(BASE_URL + "/login");
            Log.d(TAG, "Llamando a URL: " + url);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(10000); // 10 segundos
            conn.setReadTimeout(10000);    // 10 segundos
            conn.setDoOutput(true);

            // Crear JSON
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("contrasena", contrasena);

            Log.d(TAG, "JSON enviado: " + json.toString());

            // Enviar JSON
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Leer respuesta
            int code = conn.getResponseCode();
            Log.d(TAG, "Código de respuesta: " + code);

            try (InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
                 BufferedReader in = new BufferedReader(new InputStreamReader(is))) {

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line.trim());
                }

                String raw = response.toString();
                Log.d(TAG, "Respuesta raw: " + raw);

                if (raw.isEmpty()) {
                    return createErrorResponse("Respuesta vacía del servidor");
                }

                return new JSONObject(raw);
            }

        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage(), e);
            return createErrorResponse("Error de conexión: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Excepción inesperada: ", e);
            return createErrorResponse("Error inesperado: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static JSONObject createErrorResponse(String message) {
        JSONObject error = new JSONObject();
        try {
            error.put("success", false);
            error.put("message", message);
        } catch (Exception e) {
            Log.e(TAG, "Error al crear el JSON de error", e);
        }
        return error;
    }

    public static JSONObject register(Usuario usuario) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URL + "/register");
            Log.d(TAG, "Llamando a URL: " + url);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            // Crear JSON del usuario y mascotas
            JSONObject json = new JSONObject();
            json.put("nombre", usuario.getNombre());
            json.put("correo", usuario.getCorreo());
            json.put("contrasena", usuario.getContrasena());
            json.put("tipo_usuario", "privado");
            json.put("rol", "usuario");

            // Array de mascotas
            org.json.JSONArray mascotasArray = new org.json.JSONArray();
            for (PetInfo mascota : usuario.getMascotas()) {
                JSONObject m = new JSONObject();
                m.put("nombre", mascota.getNombre());
                m.put("tipo", mascota.getTipo().toLowerCase());
                m.put("raza", mascota.getRaza());
                m.put("fecha_nac", mascota.getFechaNac());
                mascotasArray.put(m);
            }

            json.put("mascotas", mascotasArray);
            Log.d(TAG, "JSON a enviar: " + json.toString());

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            Log.d(TAG, "Código de respuesta: " + code);

            try (InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
                 BufferedReader in = new BufferedReader(new InputStreamReader(is))) {

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line.trim());
                }

                String raw = response.toString();
                Log.d(TAG, "Respuesta raw: " + raw);

                if (raw.isEmpty()) {
                    return createErrorResponse("Respuesta vacía del servidor");
                }

                return new JSONObject(raw);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al registrar: ", e);
            return createErrorResponse("Error al registrar: " + e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

}
