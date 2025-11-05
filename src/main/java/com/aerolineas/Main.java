package com.aerolineas;

import java.util.Scanner;

/**
 * Clase principal con interfaz de consola para el sistema de aerolíneas
 */
public class Main {
    private static SistemaAerolineas sistema = new SistemaAerolineas();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        mostrarMenu();
    }

    private static void mostrarMenu() {
        while (true) {
            System.out.println("\n=== AEROLÍNEAS PC21 ===");
            System.out.println("1. Consultar ruta mínima entre aeropuertos");
            System.out.println("2. Realizar reserva de pasaje");
            System.out.println("3. Consultar ocupación de vuelo");
            System.out.println("4. Cancelar reserva");
            System.out.println("5. Listar aeropuertos alcanzables");
            System.out.println("6. Ejecutar BFS desde aeropuerto");
            System.out.println("7. Ejecutar DFS desde aeropuerto");
            System.out.println("8. Limpiar consola");
            System.out.println("9. Listar todas las reservas");
            System.out.println("10. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine().trim());

                switch (opcion) {
                    case 1:
                        consultarRuta();
                        break;
                    case 2:
                        realizarReserva();
                        break;
                    case 3:
                        consultarOcupacion();
                        break;
                    case 4:
                        cancelarReserva();
                        break;
                    case 5:
                        listarAlcanzables();
                        break;
                    case 6:
                        ejecutarBFS();
                        break;
                    case 7:
                        ejecutarDFS();
                        break;
                    case 8:
                        limpiarConsola();
                        break;
                    case 9:
                        listarTodasLasReservas();
                        break;
                    case 10:
                        System.out.println("¡Hasta luego!");
                        return;
                    default:
                        System.out.println("Opción inválida");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido");
            }
        }
    }

    private static void consultarRuta() {
        System.out.println("\n=== CONSULTA DE RUTA ===");
        System.out.println("Aeropuertos disponibles:");
        System.out.println("BUE (Buenos Aires), COR (Córdoba), MDZ (Mendoza), BRC (Bariloche)");
        System.out.println("SCZ (Santa Cruz), SFN (Santa Fe), PSS (Posadas)");

        System.out.print("Origen (código): ");
        String origen = scanner.nextLine().trim();

        System.out.print("Destino (código): ");
        String destino = scanner.nextLine().trim();

        sistema.consultarRuta(origen, destino);
    }

    private static void realizarReserva() {
        System.out.println("\n=== REALIZAR RESERVA ===");
        System.out.println("Aeropuertos disponibles:");
        System.out.println("BUE (Buenos Aires), COR (Córdoba), MDZ (Mendoza), BRC (Bariloche)");
        System.out.println("SCZ (Santa Cruz), SFN (Santa Fe), PSS (Posadas)");

        System.out.print("Origen (código): ");
        String origen = scanner.nextLine().trim();

        System.out.print("Destino (código): ");
        String destino = scanner.nextLine().trim();

        System.out.print("Nombre del pasajero: ");
        String pasajero = scanner.nextLine().trim();

        if (pasajero.isEmpty()) {
            System.out.println("Error: El nombre del pasajero no puede estar vacío");
            return;
        }

        sistema.realizarReserva(origen, destino, pasajero);
    }

    private static void consultarOcupacion() {
        System.out.println("\n=== CONSULTA DE OCUPACIÓN ===");
        System.out.print("Código de vuelo: ");
        String codigoVuelo = scanner.nextLine().trim();

        sistema.consultarOcupacion(codigoVuelo);
    }

    private static void cancelarReserva() {
        System.out.println("\n=== CANCELAR RESERVA ===");
        System.out.print("Código de reserva: ");
        String codigoReserva = scanner.nextLine().trim();

        sistema.cancelarReserva(codigoReserva);
    }

    private static void listarAlcanzables() {
        System.out.println("\n=== AEROPUERTOS ALCANZABLES ===");
        System.out.println("Aeropuertos disponibles:");
        System.out.println("BUE (Buenos Aires), COR (Córdoba), MDZ (Mendoza), BRC (Bariloche)");
        System.out.println("SCZ (Santa Cruz), SFN (Santa Fe), PSS (Posadas)");

        System.out.print("Origen (código): ");
        String origen = scanner.nextLine().trim();

        sistema.listarAlcanzables(origen);
    }

    private static void ejecutarBFS() {
        System.out.println("\n=== EJECUTAR BFS ===");
        System.out.println("Aeropuertos disponibles:");
        System.out.println("BUE (Buenos Aires), COR (Córdoba), MDZ (Mendoza), BRC (Bariloche)");
        System.out.println("SCZ (Santa Cruz), SFN (Santa Fe), PSS (Posadas)");

        System.out.print("Origen (código): ");
        String origen = scanner.nextLine().trim();

        sistema.ejecutarBFS(origen);
    }

    private static void ejecutarDFS() {
        System.out.println("\n=== EJECUTAR DFS ===");
        System.out.println("Aeropuertos disponibles:");
        System.out.println("BUE (Buenos Aires), COR (Córdoba), MDZ (Mendoza), BRC (Bariloche)");
        System.out.println("SCZ (Santa Cruz), SFN (Santa Fe), PSS (Posadas)");

        System.out.print("Origen (código): ");
        String origen = scanner.nextLine().trim();

        sistema.ejecutarDFS(origen);
    }

    private static void limpiarConsola() {
        try {
            // Para Windows
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            // En caso de error, intentar con múltiples líneas en blanco
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
            System.out.println("Consola limpiada");
        }
    }

    private static void listarTodasLasReservas() {
        System.out.println("\n=== TODAS LAS RESERVAS ===");
        sistema.listarTodasLasReservas();
    }
}
