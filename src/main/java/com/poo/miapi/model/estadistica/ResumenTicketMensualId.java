package com.poo.miapi.model.estadistica;

import java.io.Serializable;
import java.util.Objects;

public class ResumenTicketMensualId implements Serializable {
    private Integer anio;
    private Integer mes;

    public ResumenTicketMensualId() {
    }

    public ResumenTicketMensualId(Integer anio, Integer mes) {
        this.anio = anio;
        this.mes = mes;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ResumenTicketMensualId that = (ResumenTicketMensualId) o;
        return Objects.equals(anio, that.anio) && Objects.equals(mes, that.mes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anio, mes);
    }
}
