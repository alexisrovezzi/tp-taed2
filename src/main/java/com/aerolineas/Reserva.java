package com.aerolineas;

/**
 * Representa una reserva de pasaje
 */
public class Reserva {
    private String codigoReserva;
    private String pasajero;
    private Asiento asiento;

    public Reserva(String codigoReserva, String pasajero, Asiento asiento) {
        this.codigoReserva = codigoReserva;
        this.pasajero = pasajero;
        this.asiento = asiento;
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public String getPasajero() {
        return pasajero;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    @Override
    public String toString() {
        return String.format("Reserva %s - %s: Asiento %s",
            codigoReserva, pasajero, asiento);
    }
}
