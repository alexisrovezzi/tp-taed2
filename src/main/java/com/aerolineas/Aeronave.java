package com.aerolineas;

import java.util.*;

/**
 * Representa una aeronave con 3 secciones (A, B, C) de 10 asientos cada una
 */
public class Aeronave {
    private static final int ASIENTOS_POR_SECCION = 10;
    private static final char[] SECCIONES = {'A', 'B', 'C'};

    private Set<Asiento> asientosOcupados;
    private Map<Character, Integer> ocupacionPorSeccion;

    public Aeronave() {
        this.asientosOcupados = new HashSet<>();
        this.ocupacionPorSeccion = new HashMap<>();
        for (char seccion : SECCIONES) {
            ocupacionPorSeccion.put(seccion, 0);
        }
    }

    /**
     * Asigna un asiento aleatorio manteniendo el balance entre secciones
     * Regla: diferencia de asientos ocupados entre secciones ≤ 1
     */
    public Asiento asignarAsientoAleatorio() {
        // Encontrar las secciones menos ocupadas
        int minOcupacion = Collections.min(ocupacionPorSeccion.values());

        List<Character> seccionesCandidatas = new ArrayList<>();
        for (char seccion : SECCIONES) {
            if (ocupacionPorSeccion.get(seccion) == minOcupacion) {
                seccionesCandidatas.add(seccion);
            }
        }

        // Elegir sección al azar entre las menos ocupadas
        Random random = new Random();
        char seccionElegida = seccionesCandidatas.get(random.nextInt(seccionesCandidatas.size()));

        // Encontrar asientos libres en la sección elegida
        List<Asiento> asientosLibres = new ArrayList<>();
        for (int i = 1; i <= ASIENTOS_POR_SECCION; i++) {
            Asiento asiento = new Asiento(seccionElegida, i);
            if (!asientosOcupados.contains(asiento)) {
                asientosLibres.add(asiento);
            }
        }

        // Si no hay asientos libres en la sección, intentar otras secciones
        if (asientosLibres.isEmpty()) {
            // Buscar en todas las secciones respetando el balance
            for (char seccion : SECCIONES) {
                if (ocupacionPorSeccion.get(seccion) <= minOcupacion + 1) {
                    for (int i = 1; i <= ASIENTOS_POR_SECCION; i++) {
                        Asiento asiento = new Asiento(seccion, i);
                        if (!asientosOcupados.contains(asiento)) {
                            asientosLibres.add(asiento);
                        }
                    }
                }
            }
        }

        if (asientosLibres.isEmpty()) {
            return null; // Aeronave llena
        }

        // Elegir asiento al azar entre los libres
        Asiento asientoAsignado = asientosLibres.get(random.nextInt(asientosLibres.size()));
        asientosOcupados.add(asientoAsignado);
        ocupacionPorSeccion.put(asientoAsignado.getSeccion(),
                               ocupacionPorSeccion.get(asientoAsignado.getSeccion()) + 1);

        return asientoAsignado;
    }

    /**
     * Libera un asiento
     */
    public boolean liberarAsiento(Asiento asiento) {
        if (asientosOcupados.remove(asiento)) {
            ocupacionPorSeccion.put(asiento.getSeccion(),
                                   ocupacionPorSeccion.get(asiento.getSeccion()) - 1);
            return true;
        }
        return false;
    }

    /**
     * Obtiene el porcentaje de ocupación global
     */
    public double getPorcentajeOcupacion() {
        int totalAsientos = SECCIONES.length * ASIENTOS_POR_SECCION;
        return (double) asientosOcupados.size() / totalAsientos * 100.0;
    }

    /**
     * Obtiene la ocupación por sección
     */
    public Map<Character, Integer> getOcupacionPorSeccion() {
        return new HashMap<>(ocupacionPorSeccion);
    }

    /**
     * Verifica si está casi llena (≥95%)
     */
    public boolean estaCasiLlena() {
        return getPorcentajeOcupacion() >= 95.0;
    }

    /**
     * Obtiene el total de asientos ocupados
     */
    public int getTotalOcupados() {
        return asientosOcupados.size();
    }

    /**
     * Obtiene el total de asientos disponibles
     */
    public int getTotalAsientos() {
        return SECCIONES.length * ASIENTOS_POR_SECCION;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Aeronave - Ocupación: ").append(String.format("%.1f%%", getPorcentajeOcupacion())).append("\n");
        for (char seccion : SECCIONES) {
            sb.append("Sección ").append(seccion).append(": ")
              .append(ocupacionPorSeccion.get(seccion)).append("/").append(ASIENTOS_POR_SECCION).append("\n");
        }
        return sb.toString();
    }
}
