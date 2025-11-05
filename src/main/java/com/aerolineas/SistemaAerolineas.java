package com.aerolineas;

import java.util.*;

/**
 * Sistema principal de la aerolínea
 */
public class SistemaAerolineas {
    private Grafo grafo;
    private Map<String, Vuelo> vuelos; // clave: código de vuelo
    private Map<String, Aeropuerto> aeropuertos; // clave: código de aeropuerto
    private int contadorReservas;
    private int contadorVuelos;

    public SistemaAerolineas() {
        this.grafo = new Grafo();
        this.vuelos = new HashMap<>();
        this.aeropuertos = new HashMap<>();
        this.contadorReservas = 1;
        this.contadorVuelos = 1;
        inicializarDatos();
    }

    /**
     * Inicializa los aeropuertos y conexiones según la consigna
     */
    private void inicializarDatos() {
        // Crear aeropuertos
        crearAeropuerto("BUE", "Buenos Aires");
        crearAeropuerto("COR", "Córdoba");
        crearAeropuerto("MDZ", "Mendoza");
        crearAeropuerto("BRC", "Bariloche");
        crearAeropuerto("SCZ", "Santa Cruz");
        crearAeropuerto("SFN", "Santa Fe");
        crearAeropuerto("PSS", "Posadas");

        // Vuelos directos desde Buenos Aires
        agregarConexion("BUE", "COR", 120000, 1.2);
        agregarConexion("BUE", "MDZ", 150000, 1.7);
        agregarConexion("BUE", "BRC", 220000, 2.2);
        agregarConexion("BUE", "SFN", 100000, 1.0);
        agregarConexion("BUE", "PSS", 140000, 1.5);

        // Conexiones adicionales
        agregarConexion("COR", "MDZ", 90000, 1.1);
        agregarConexion("COR", "SFN", 70000, 0.8);
        agregarConexion("MDZ", "BRC", 120000, 1.6);
        agregarConexion("BRC", "SCZ", 160000, 2.0);
        agregarConexion("MDZ", "SCZ", 170000, 2.6);
        agregarConexion("SFN", "PSS", 80000, 1.2);
    }

    private void crearAeropuerto(String codigo, String nombre) {
        Aeropuerto aeropuerto = new Aeropuerto(codigo, nombre);
        aeropuertos.put(codigo, aeropuerto);
        grafo.agregarAeropuerto(aeropuerto);
    }

    private void agregarConexion(String origen, String destino, double precio, double tiempo) {
        Aeropuerto aeroOrigen = aeropuertos.get(origen);
        Aeropuerto aeroDestino = aeropuertos.get(destino);
        Conexion conexion = new Conexion(aeroOrigen, aeroDestino, precio, tiempo);
        grafo.agregarConexion(conexion);
    }

    /**
     * Consulta la ruta mínima entre dos aeropuertos
     */
    public void consultarRuta(String origen, String destino) {
        Aeropuerto aeroOrigen = aeropuertos.get(origen.toUpperCase());
        Aeropuerto aeroDestino = aeropuertos.get(destino.toUpperCase());

        if (aeroOrigen == null || aeroDestino == null) {
            System.out.println("Error: Aeropuerto no encontrado");
            return;
        }

        Grafo.ResultadoRuta resultado = grafo.dijkstra(aeroOrigen, aeroDestino);

        if (!resultado.existeRuta()) {
            System.out.println("No existe ruta entre " + aeroOrigen + " y " + aeroDestino);
            return;
        }

        System.out.println("Ruta mínima de " + aeroOrigen + " a " + aeroDestino + ":");
        System.out.println("Tiempo total: " + String.format("%.1f horas", resultado.getTiempoTotal()));
        System.out.println("Precio base total: $" + String.format("%.0f", resultado.getPrecioTotal()));

        System.out.println("Tramos:");
        for (Conexion conexion : resultado.getConexiones()) {
            System.out.println("  " + conexion);
        }
    }

    /**
     * Realiza una reserva de pasaje
     */
    public void realizarReserva(String origen, String destino, String pasajero) {
        Aeropuerto aeroOrigen = aeropuertos.get(origen.toUpperCase());
        Aeropuerto aeroDestino = aeropuertos.get(destino.toUpperCase());

        if (aeroOrigen == null || aeroDestino == null) {
            System.out.println("Error: Aeropuerto no encontrado");
            return;
        }

        Grafo.ResultadoRuta ruta = grafo.dijkstra(aeroOrigen, aeroDestino);

        if (!ruta.existeRuta()) {
            System.out.println("No existe ruta entre " + aeroOrigen + " y " + aeroDestino);
            return;
        }

        // Crear vuelos por cada tramo si no existen
        List<Vuelo> vuelosItinerario = new ArrayList<>();
        double precioFinal = 0;

        for (Conexion conexion : ruta.getConexiones()) {
            // Buscar si ya existe un vuelo para esta conexión
            Vuelo vuelo = buscarVueloPorConexion(conexion);
            if (vuelo == null) {
                // Si no existe, crear uno nuevo con código único
                String codigoVuelo = generarCodigoVuelo(conexion);
                vuelo = new Vuelo(codigoVuelo, conexion.getOrigen(), conexion.getDestino());
                vuelos.put(codigoVuelo, vuelo);
            }

            // Calcular precio del tramo
            double precioTramo = conexion.getPrecioBase();

            // Aplicar +10% si vuelo casi lleno
            if (vuelo.estaCasiLleno()) {
                precioTramo *= 1.10;
                System.out.println("Recargo +10% aplicado al tramo " +
                    conexion.getOrigen().getCodigo() + "-" + conexion.getDestino().getCodigo() +
                    " (ocupación ≥95%)");
            }

            precioFinal += precioTramo;
            vuelosItinerario.add(vuelo);
        }

        // Aplicar +20% si es directo
        if (ruta.esDirecto()) {
            precioFinal *= 1.20;
            System.out.println("Recargo +20% aplicado (vuelo directo)");
        }

        // Realizar reservas en cada vuelo
        String codigoReservaBase = "RES" + String.format("%04d", contadorReservas++);
        List<Reserva> reservasRealizadas = new ArrayList<>();

        for (int i = 0; i < vuelosItinerario.size(); i++) {
            Vuelo vuelo = vuelosItinerario.get(i);
            String codigoReserva = codigoReservaBase + "-" + (i + 1);

            Reserva reserva = vuelo.realizarReserva(codigoReserva, pasajero);
            if (reserva == null) {
                System.out.println("Error: No hay asientos disponibles en el vuelo " + vuelo.getCodigoVuelo());
                // Cancelar reservas anteriores
                for (Reserva r : reservasRealizadas) {
                    String vueloCode = extraerCodigoVuelo(r.getCodigoReserva());
                    vuelos.get(vueloCode).cancelarReserva(r.getCodigoReserva());
                }
                return;
            }
            reservasRealizadas.add(reserva);
        }

        // Imprimir comprobante
        imprimirComprobante(ruta, reservasRealizadas, precioFinal);
    }

    private String generarCodigoVuelo(Conexion conexion) {
        return conexion.getOrigen().getCodigo() + conexion.getDestino().getCodigo() +
               String.format("%02d", contadorVuelos++);
    }

    private String extraerCodigoVuelo(String codigoReserva) {
        // RES0001-1 -> buscar vuelo correspondiente
        for (Vuelo vuelo : vuelos.values()) {
            if (vuelo.buscarReserva(codigoReserva) != null) {
                return vuelo.getCodigoVuelo();
            }
        }
        return null;
    }

    /**
     * Busca si ya existe un vuelo para una conexión específica
     */
    private Vuelo buscarVueloPorConexion(Conexion conexion) {
        for (Vuelo vuelo : vuelos.values()) {
            if (vuelo.getOrigen().equals(conexion.getOrigen()) &&
                vuelo.getDestino().equals(conexion.getDestino())) {
                return vuelo;
            }
        }
        return null;
    }

    private void imprimirComprobante(Grafo.ResultadoRuta ruta, List<Reserva> reservas, double precioFinal) {
        System.out.println("\n=== COMPROBANTE DE RESERVA ===");
        System.out.println("Ruta: " + ruta.getConexiones().get(0).getOrigen() +
                          " -> " + ruta.getConexiones().get(ruta.getConexiones().size()-1).getDestino());
        System.out.println("Tiempo total: " + String.format("%.1f horas", ruta.getTiempoTotal()));

        System.out.println("\nVuelos asignados:");
        for (int i = 0; i < reservas.size(); i++) {
            Reserva reserva = reservas.get(i);
            Conexion conexion = ruta.getConexiones().get(i);
            String codigoVuelo = generarCodigoVuelo(conexion);
            System.out.println("  " + reserva.getCodigoReserva() + ": Vuelo " + codigoVuelo +
                             " (" + conexion.getOrigen().getCodigo() + "-" + conexion.getDestino().getCodigo() + ")" +
                             " - Asiento " + reserva.getAsiento());
        }

        System.out.println("\nPrecio final: $" + String.format("%.0f", precioFinal));
        System.out.println("============================\n");
    }

    /**
     * Consulta la ocupación de un vuelo
     */
    public void consultarOcupacion(String codigoVuelo) {
        Vuelo vuelo = vuelos.get(codigoVuelo.toUpperCase());
        if (vuelo == null) {
            System.out.println("Error: Vuelo no encontrado");
            return;
        }

        vuelo.mostrarInformacion();
        System.out.println("Reservas:");
        vuelo.listarReservas();
    }

    /**
     * Cancela una reserva
     */
    public void cancelarReserva(String codigoReserva) {
        for (Vuelo vuelo : vuelos.values()) {
            if (vuelo.cancelarReserva(codigoReserva)) {
                System.out.println("Reserva " + codigoReserva + " cancelada exitosamente");
                return;
            }
        }
        System.out.println("Error: Reserva no encontrada");
    }

    /**
     * Lista aeropuertos alcanzables desde uno dado
     */
    public void listarAlcanzables(String origen) {
        Aeropuerto aeroOrigen = aeropuertos.get(origen.toUpperCase());
        if (aeroOrigen == null) {
            System.out.println("Error: Aeropuerto no encontrado");
            return;
        }

        List<Aeropuerto> alcanzables = grafo.bfs(aeroOrigen);
        System.out.println("Aeropuertos alcanzables desde " + aeroOrigen + ":");
        for (Aeropuerto aeropuerto : alcanzables) {
            if (!aeropuerto.equals(aeroOrigen)) {
                System.out.println("  " + aeropuerto);
            }
        }
    }

    /**
     * Ejecuta BFS desde un aeropuerto
     */
    public void ejecutarBFS(String origen) {
        Aeropuerto aeroOrigen = aeropuertos.get(origen.toUpperCase());
        if (aeroOrigen == null) {
            System.out.println("Error: Aeropuerto no encontrado");
            return;
        }

        List<Aeropuerto> recorrido = grafo.bfs(aeroOrigen);
        System.out.println("Recorrido BFS desde " + aeroOrigen + ":");
        for (Aeropuerto aeropuerto : recorrido) {
            System.out.print(aeropuerto.getCodigo() + " ");
        }
        System.out.println();
    }

    /**
     * Ejecuta DFS desde un aeropuerto
     */
    public void ejecutarDFS(String origen) {
        Aeropuerto aeroOrigen = aeropuertos.get(origen.toUpperCase());
        if (aeroOrigen == null) {
            System.out.println("Error: Aeropuerto no encontrado");
            return;
        }

        List<Aeropuerto> recorrido = grafo.dfs(aeroOrigen);
        System.out.println("Recorrido DFS desde " + aeroOrigen + ":");
        for (Aeropuerto aeropuerto : recorrido) {
            System.out.print(aeropuerto.getCodigo() + " ");
        }
        System.out.println();
    }

    /**
     * Lista todas las reservas del sistema
     */
    public void listarTodasLasReservas() {
        final boolean[] hayReservas = {false};

        for (Vuelo vuelo : vuelos.values()) {
            // Verificar si el vuelo tiene reservas
            final boolean[] tieneReservas = {false};

            vuelo.getReservas().inorder((codigo, reserva) -> {
                if (!hayReservas[0]) {
                    System.out.println("\nRESERVAS REALIZADAS:");
                    hayReservas[0] = true;
                }
                if (!tieneReservas[0]) {
                    System.out.println("\nVuelo " + vuelo.getCodigoVuelo() + " (" +
                                     vuelo.getOrigen().getNombre() + " -> " +
                                     vuelo.getDestino().getNombre() + "):");
                    System.out.println("Ocupación: " + vuelo.getAeronave().getTotalOcupados() + "/" +
                                     vuelo.getAeronave().getTotalAsientos() + " asientos");
                    tieneReservas[0] = true;
                }
                System.out.println("  " + reserva);
            });
        }

        if (!hayReservas[0]) {
            System.out.println("No hay reservas realizadas en el sistema.");
        } else {
            // Calcular estadísticas totales
            final int[] totalReservas = {0};
            final int[] totalVuelosConReservas = {0};
            final int[] totalAsientosOcupados = {0};

            for (Vuelo vuelo : vuelos.values()) {
                final int[] reservasVuelo = {0};
                vuelo.getReservas().inorder((codigo, reserva) -> reservasVuelo[0]++);
                if (reservasVuelo[0] > 0) {
                    totalVuelosConReservas[0]++;
                    totalReservas[0] += reservasVuelo[0];
                    totalAsientosOcupados[0] += vuelo.getAeronave().getTotalOcupados();
                }
            }

            System.out.println("\n--- ESTADÍSTICAS TOTALES ---");
            System.out.println("Total de reservas: " + totalReservas[0]);
            System.out.println("Vuelos con reservas: " + totalVuelosConReservas[0]);
            System.out.println("Asientos ocupados: " + totalAsientosOcupados[0]);
        }
    }
}
