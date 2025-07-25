package com.poo.miapi.util;

public class PasswordHelper {
    /**
     * Genera la contraseña por defecto a partir del apellido.
     * 
     * @param apellido El apellido del usuario
     * @return La contraseña por defecto (apellido+123)
     */
    public static String generarPasswordPorDefecto(String apellido) {
        return apellido + "123";
    }
}
