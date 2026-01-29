package com.poo.miapi.dto.historial;

import java.io.Serializable;

public class ProcesarSolicitudDevolucionRequestDto implements Serializable {
    private int idAdmin;
    private boolean aprobar;
    private String comentario;

    public int getIdAdmin() {
        return idAdmin;
    }
    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }
    public boolean isAprobar() {
        return aprobar;
    }
    public void setAprobar(boolean aprobar) {
        this.aprobar = aprobar;
    }
    public String getComentario() {
        return comentario;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}