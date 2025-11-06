package com.aerolineas;

/**
 * Representa un vuelo entre dos aeropuertos
 */
public class Vuelo {
    private String codigoVuelo;
    private Aeropuerto origen;
    private Aeropuerto destino;
    private Aeronave aeronave;
    private ArbolAVL<String, Reserva> reservas; // clave: código de reserva
    private ArbolAVL<Asiento, Reserva> reservasPorAsiento; // clave: asiento

    public Vuelo(String codigoVuelo, Aeropuerto origen, Aeropuerto destino) {
        this.codigoVuelo = codigoVuelo;
        this.origen = origen;
        this.destino = destino;
        this.aeronave = new Aeronave();
        this.reservas = new ArbolAVL<>();
        this.reservasPorAsiento = new ArbolAVL<>();
    }

    /**
     * Realiza una reserva asignando un asiento automáticamente
     */
    public Reserva realizarReserva(String codigoReserva, String pasajero) {
        Asiento asiento = aeronave.asignarAsientoAleatorio();
        if (asiento == null) {
            return null; // Vuelo lleno
        }

        Reserva reserva = new Reserva(codigoReserva, pasajero, asiento);
        reservas.insertar(codigoReserva, reserva);
        reservasPorAsiento.insertar(asiento, reserva);

        return reserva;
    }

    /**
     * Cancela una reserva
     */
    public boolean cancelarReserva(String codigoReserva) {
        Reserva reserva = reservas.buscar(codigoReserva);
        if (reserva == null) {
            return false;
        }

        reservas.eliminar(codigoReserva);
        reservasPorAsiento.eliminar(reserva.getAsiento());
        aeronave.liberarAsiento(reserva.getAsiento());

        return true;
    }

    /**
     * Busca una reserva por código
     */
    public Reserva buscarReserva(String codigoReserva) {
        return reservas.buscar(codigoReserva);
    }

    /**
     * Verifica si el vuelo está casi lleno (≥95%)
     */
    public boolean estaCasiLleno() {
        return aeronave.estaCasiLlena();
    }

    /**
     * Lista todas las reservas (inorder)
     */
    public void listarReservas() {
        System.out.println("Reservas del vuelo " + codigoVuelo + ":");
        reservas.inorder((codigo, reserva) -> {
            System.out.println(reserva);
        });
    }

    public void mostrarArbolReservas() {
        System.out.println("Estructura del árbol AVL de reservas del vuelo " + codigoVuelo + ":");
        reservas.mostrarArbol();
    }

    /**
     * Muestra información del vuelo y ocupación
     */
    public void mostrarInformacion() {
        System.out.println("Vuelo: " + codigoVuelo);
        System.out.println("Ruta: " + origen + " -> " + destino);
        System.out.println(aeronave);
    }

    public String getCodigoVuelo() {
        return codigoVuelo;
    }

    public Aeropuerto getOrigen() {
        return origen;
    }

    public Aeropuerto getDestino() {
        return destino;
    }

    public Aeronave getAeronave() {
        return aeronave;
    }

    public ArbolAVL<String, Reserva> getReservas() {
        return reservas;
    }
}
