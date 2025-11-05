package com.aerolineas;

import java.util.*;

/**
 * Grafo de aeropuertos con conexiones (lista de adyacencia)
 */
public class Grafo {
    private Map<Aeropuerto, List<Conexion>> adyacencias;

    public Grafo() {
        this.adyacencias = new HashMap<>();
    }

    /**
     * Agrega un aeropuerto al grafo
     */
    public void agregarAeropuerto(Aeropuerto aeropuerto) {
        adyacencias.putIfAbsent(aeropuerto, new ArrayList<>());
    }

    /**
     * Agrega una conexión bidireccional entre dos aeropuertos
     */
    public void agregarConexion(Conexion conexion) {
        agregarAeropuerto(conexion.getOrigen());
        agregarAeropuerto(conexion.getDestino());

        adyacencias.get(conexion.getOrigen()).add(conexion);
        adyacencias.get(conexion.getDestino()).add(conexion.getInversa());
    }

    /**
     * Obtiene todos los aeropuertos del grafo
     */
    public Set<Aeropuerto> getAeropuertos() {
        return adyacencias.keySet();
    }

    /**
     * Obtiene las conexiones de un aeropuerto
     */
    public List<Conexion> getConexiones(Aeropuerto aeropuerto) {
        return adyacencias.getOrDefault(aeropuerto, new ArrayList<>());
    }

    /**
     * BFS desde un aeropuerto origen
     */
    public List<Aeropuerto> bfs(Aeropuerto origen) {
        List<Aeropuerto> resultado = new ArrayList<>();
        Set<Aeropuerto> visitados = new HashSet<>();
        Queue<Aeropuerto> cola = new LinkedList<>();

        cola.add(origen);
        visitados.add(origen);

        while (!cola.isEmpty()) {
            Aeropuerto actual = cola.poll();
            resultado.add(actual);

            for (Conexion conexion : getConexiones(actual)) {
                Aeropuerto vecino = conexion.getDestino();
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    cola.add(vecino);
                }
            }
        }

        return resultado;
    }

    /**
     * DFS desde un aeropuerto origen
     */
    public List<Aeropuerto> dfs(Aeropuerto origen) {
        List<Aeropuerto> resultado = new ArrayList<>();
        Set<Aeropuerto> visitados = new HashSet<>();
        dfsRecursivo(origen, visitados, resultado);
        return resultado;
    }

    private void dfsRecursivo(Aeropuerto actual, Set<Aeropuerto> visitados, List<Aeropuerto> resultado) {
        visitados.add(actual);
        resultado.add(actual);

        for (Conexion conexion : getConexiones(actual)) {
            Aeropuerto vecino = conexion.getDestino();
            if (!visitados.contains(vecino)) {
                dfsRecursivo(vecino, visitados, resultado);
            }
        }
    }

    /**
     * Dijkstra para encontrar ruta mínima por tiempo (criterio principal)
     * En caso de empate, minimiza precio total
     */
    public ResultadoRuta dijkstra(Aeropuerto origen, Aeropuerto destino) {
        Map<Aeropuerto, Double> tiempoMinimo = new HashMap<>();
        Map<Aeropuerto, Double> precioMinimo = new HashMap<>();
        Map<Aeropuerto, Aeropuerto> predecesor = new HashMap<>();
        Map<Aeropuerto, Conexion> conexionPredecesor = new HashMap<>();

        // Inicializar
        for (Aeropuerto aeropuerto : getAeropuertos()) {
            tiempoMinimo.put(aeropuerto, Double.MAX_VALUE);
            precioMinimo.put(aeropuerto, Double.MAX_VALUE);
        }
        tiempoMinimo.put(origen, 0.0);
        precioMinimo.put(origen, 0.0);

        // Priority queue: tiempo principal, precio secundario
        PriorityQueue<Aeropuerto> pq = new PriorityQueue<>(
            Comparator.<Aeropuerto>comparingDouble(tiempoMinimo::get)
                .thenComparingDouble(precioMinimo::get)
        );
        pq.add(origen);

        while (!pq.isEmpty()) {
            Aeropuerto actual = pq.poll();

            if (actual.equals(destino)) break;

            for (Conexion conexion : getConexiones(actual)) {
                Aeropuerto vecino = conexion.getDestino();
                double nuevoTiempo = tiempoMinimo.get(actual) + conexion.getTiempoHoras();
                double nuevoPrecio = precioMinimo.get(actual) + conexion.getPrecioBase();

                // Comparar primero por tiempo, luego por precio
                boolean mejorRuta = false;
                if (nuevoTiempo < tiempoMinimo.get(vecino)) {
                    mejorRuta = true;
                } else if (nuevoTiempo == tiempoMinimo.get(vecino) &&
                          nuevoPrecio < precioMinimo.get(vecino)) {
                    mejorRuta = true;
                }

                if (mejorRuta) {
                    tiempoMinimo.put(vecino, nuevoTiempo);
                    precioMinimo.put(vecino, nuevoPrecio);
                    predecesor.put(vecino, actual);
                    conexionPredecesor.put(vecino, conexion);

                    // Actualizar priority queue
                    pq.remove(vecino);
                    pq.add(vecino);
                }
            }
        }

        // Reconstruir ruta
        return reconstruirRuta(origen, destino, predecesor, conexionPredecesor,
                              tiempoMinimo.get(destino), precioMinimo.get(destino));
    }

    private ResultadoRuta reconstruirRuta(Aeropuerto origen, Aeropuerto destino,
                                         Map<Aeropuerto, Aeropuerto> predecesor,
                                         Map<Aeropuerto, Conexion> conexionPredecesor,
                                         double tiempoTotal, double precioTotal) {
        List<Conexion> ruta = new ArrayList<>();
        Aeropuerto actual = destino;

        while (!actual.equals(origen)) {
            Aeropuerto prev = predecesor.get(actual);
            if (prev == null) {
                return new ResultadoRuta(null, Double.MAX_VALUE, Double.MAX_VALUE);
            }
            ruta.add(0, conexionPredecesor.get(actual));
            actual = prev;
        }

        return new ResultadoRuta(ruta, tiempoTotal, precioTotal);
    }

    /**
     * Clase para representar el resultado de Dijkstra
     */
    public static class ResultadoRuta {
        private List<Conexion> conexiones;
        private double tiempoTotal;
        private double precioTotal;

        public ResultadoRuta(List<Conexion> conexiones, double tiempoTotal, double precioTotal) {
            this.conexiones = conexiones;
            this.tiempoTotal = tiempoTotal;
            this.precioTotal = precioTotal;
        }

        public List<Conexion> getConexiones() {
            return conexiones;
        }

        public double getTiempoTotal() {
            return tiempoTotal;
        }

        public double getPrecioTotal() {
            return precioTotal;
        }

        public boolean existeRuta() {
            return conexiones != null && !conexiones.isEmpty();
        }

        public boolean esDirecto() {
            return conexiones != null && conexiones.size() == 1;
        }
    }
}
