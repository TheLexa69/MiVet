package com.mivet.veterinaria.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FechaUtils {
    private static final String FORMATO_ISO = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String FORMATO_LEIBLE = "dd/MM/yyyy HH:mm";

    public static String convertirAFormatoISO(Date date) {
        return new SimpleDateFormat(FORMATO_ISO, Locale.getDefault()).format(date);
    }

    public static String convertirAFormatoLeible(String fechaISO) {
        try {
            Date date = new SimpleDateFormat(FORMATO_ISO, Locale.getDefault()).parse(fechaISO);
            return new SimpleDateFormat(FORMATO_LEIBLE, Locale.getDefault()).format(date);
        } catch (ParseException e) {
            return fechaISO;
        }
    }

    public static String convertirAFormatoLeibleDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(FORMATO_LEIBLE, new Locale("es", "ES")).format(date);
    }


    public static Date parsearDesdeISO(String fechaISO) {
        try {
            return new SimpleDateFormat(FORMATO_ISO, Locale.getDefault()).parse(fechaISO);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parsearDesdeLegible(String fechaLeible) {
        try {
            return new SimpleDateFormat(FORMATO_LEIBLE, Locale.getDefault()).parse(fechaLeible);
        } catch (ParseException e) {
            return null;
        }
    }

}
