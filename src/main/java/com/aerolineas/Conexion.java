package com.aerolineas;

/**
 * Representa una conexión entre dos aeropuertos con precio y tiempo
 */
public class Conexion {
    private Aeropuerto origen;
    private Aeropuerto destino;
    private double precioBase;
    private double tiempoHoras;

    public Conexion(Aeropuerto origen, Aeropuerto destino, double precioBase, double tiempoHoras) {
        this.origen = origen;
        this.destino = destino;
        this.precioBase = precioBase;
        this.tiempoHoras = tiempoHoras;
    }

    public Aeropuerto getOrigen() {
        return origen;
    }

    public Aeropuerto getDestino() {
        return destino;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public double getTiempoHoras() {
        return tiempoHoras;
    }

    /**
     * Obtiene la conexión inversa (bidireccional)
     */
    public Conexion getInversa() {
        return new Conexion(destino, origen, precioBase, tiempoHoras);
    }

    @Override
    public String toString() {
        return String.format("%s -> %s: $%.0f, %.1fh",
            origen.getNombre(), destino.getNombre(), precioBase, tiempoHoras);
    }
}
