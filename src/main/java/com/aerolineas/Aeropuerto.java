package com.aerolineas;

/**
 * Representa un aeropuerto en la red de vuelos
 */
public class Aeropuerto {
    private String codigo;
    private String nombre;

    public Aeropuerto(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Aeropuerto that = (Aeropuerto) obj;
        return codigo.equals(that.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return nombre + " (" + codigo + ")";
    }
}
