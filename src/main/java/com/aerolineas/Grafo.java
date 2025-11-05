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
     * Agrega una conexión entre dos aeropuertos
     * @param conexion La conexión a agregar
     * @param bidireccional Si es true, agrega también la conexión inversa
     */
    public void agregarConexion(Conexion conexion, boolean bidireccional) {
        agregarAeropuerto(conexion.getOrigen());
        agregarAeropuerto(conexion.getDestino());

        adyacencias.get(conexion.getOrigen()).add(conexion);

        if (bidireccional) {
            adyacencias.get(conexion.getDestino()).add(conexion.getInversa());
        }
    }

    /**
     * Agrega una conexión bidireccional entre dos aeropuertos (método legacy)
     */
    public void agregarConexion(Conexion conexion) {
        agregarConexion(conexion, true);
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
     * ALGORITMO DE DIJKSTRA - Ruta mínima en red de aerolíneas
     *
     * En este sistema de aerolíneas, Dijkstra encuentra la ruta óptima considerando:
     * - CRITERIO PRINCIPAL: Tiempo total mínimo (prioridad máxima)
     * - CRITERIO SECUNDARIO: Precio total mínimo (solo en caso de empate de tiempo)
     *
     * EJEMPLO: De Buenos Aires (BUE) a Santa Cruz (SCZ)
     * Ruta óptima: BUE → BRC → SCZ (4.2h) vs BUE → MDZ → SCZ (4.3h)
     * Dijkstra elige la primera porque es 0.1h más rápida
     *
     * @param origen Aeropuerto de salida
     * @param destino Aeropuerto de llegada
     * @return ResultadoRuta con la ruta óptima encontrada
     */
    public ResultadoRuta dijkstra(Aeropuerto origen, Aeropuerto destino) {
        // MAPAS PARA ALMACENAR COSTOS MÍNIMOS
        // tiempoMinimo: costo de tiempo acumulado para llegar a cada aeropuerto
        // precioMinimo: costo de precio acumulado para llegar a cada aeropuerto
        Map<Aeropuerto, Double> tiempoMinimo = new HashMap<>();
        Map<Aeropuerto, Double> precioMinimo = new HashMap<>();

        // MAPAS PARA RECONSTRUIR LA RUTA
        // predecesor: aeropuerto anterior en la ruta óptima
        // conexionPredecesor: conexión usada para llegar al aeropuerto
        Map<Aeropuerto, Aeropuerto> predecesor = new HashMap<>();
        Map<Aeropuerto, Conexion> conexionPredecesor = new HashMap<>();

        // INICIALIZACIÓN: Todos los aeropuertos empiezan con costo infinito
        // excepto el origen que tiene costo 0
        for (Aeropuerto aeropuerto : getAeropuertos()) {
            tiempoMinimo.put(aeropuerto, Double.MAX_VALUE);
            precioMinimo.put(aeropuerto, Double.MAX_VALUE);
        }
        tiempoMinimo.put(origen, 0.0);  // Tiempo desde origen a origen = 0
        precioMinimo.put(origen, 0.0);  // Precio desde origen a origen = 0

        // COLA DE PRIORIDAD: Ordena por TIEMPO PRIMERO, luego PRECIO
        // Ejemplo: Si dos aeropuertos tienen mismo tiempo, el más barato va primero
        PriorityQueue<Aeropuerto> pq = new PriorityQueue<>(
            Comparator.<Aeropuerto>comparingDouble(tiempoMinimo::get)  // Primero por tiempo
                .thenComparingDouble(precioMinimo::get)               // Luego por precio
        );
        pq.add(origen);  // Comenzamos exploración desde el aeropuerto origen

        // BUCLE PRINCIPAL: Exploración de aeropuertos por orden de prioridad
        while (!pq.isEmpty()) {
            // EXTRAER aeropuerto con MENOR costo de tiempo (y precio si hay empate)
            Aeropuerto actual = pq.poll();

            // OPTIMIZACIÓN: Si ya llegamos al destino, podemos terminar
            if (actual.equals(destino)) break;

            // EXPLORAR TODAS LAS CONEXIONES desde el aeropuerto actual
            for (Conexion conexion : getConexiones(actual)) {
                Aeropuerto vecino = conexion.getDestino();

                // CALCULAR COSTOS ACUMULADOS para llegar al vecino a través de esta conexión
                double nuevoTiempo = tiempoMinimo.get(actual) + conexion.getTiempoHoras();
                double nuevoPrecio = precioMinimo.get(actual) + conexion.getPrecioBase();

                // DECISIÓN: ¿Es esta una mejor ruta hacia el vecino?
                boolean mejorRuta = false;

                // CRITERIO 1: ¿Es más rápida que la mejor ruta conocida?
                if (nuevoTiempo < tiempoMinimo.get(vecino)) {
                    mejorRuta = true;
                }
                // CRITERIO 2: ¿Mismo tiempo pero más barato? (empate de tiempo)
                else if (nuevoTiempo == tiempoMinimo.get(vecino) &&
                          nuevoPrecio < precioMinimo.get(vecino)) {
                    mejorRuta = true;
                }

                // SI ENCONTRAMOS UNA MEJOR RUTA: Actualizar costos y predecesores
                if (mejorRuta) {
                    // ACTUALIZAR COSTOS MÍNIMOS conocidos
                    tiempoMinimo.put(vecino, nuevoTiempo);
                    precioMinimo.put(vecino, nuevoPrecio);

                    // REGISTRAR cómo llegamos aquí (para reconstruir la ruta)
                    predecesor.put(vecino, actual);
                    conexionPredecesor.put(vecino, conexion);

                    // ACTUALIZAR PRIORITY QUEUE
                    // Remover y re-insertar para mantener orden correcto
                    pq.remove(vecino);
                    pq.add(vecino);
                }
            }
        }

        // Reconstruir ruta
        return reconstruirRuta(origen, destino, predecesor, conexionPredecesor,
                              tiempoMinimo.get(destino), precioMinimo.get(destino));
    }

    /**
     * RECONSTRUCCIÓN DE LA RUTA ÓPTIMA
     *
     * Una vez que Dijkstra encuentra los costos mínimos, este método
     * reconstruye la ruta específica desde el destino hacia el origen
     * usando los mapas de predecesores.
     *
     * EJEMPLO: Si la ruta óptima fue BUE → COR → MDZ
     * Los predecesores serían: MDZ->COR, COR->BUE
     * Se reconstruye desde MDZ hacia atrás: MDZ, COR, BUE
     * Luego se invierte para obtener: BUE, COR, MDZ
     */
    private ResultadoRuta reconstruirRuta(Aeropuerto origen, Aeropuerto destino,
                                         Map<Aeropuerto, Aeropuerto> predecesor,
                                         Map<Aeropuerto, Conexion> conexionPredecesor,
                                         double tiempoTotal, double precioTotal) {
        List<Conexion> ruta = new ArrayList<>();
        Aeropuerto actual = destino;

        // RECORRER HACIA ATRÁS desde el destino hasta el origen
        // usando el mapa de predecesores
        while (!actual.equals(origen)) {
            Aeropuerto prev = predecesor.get(actual);

            // ERROR: No hay ruta posible (aeropuerto no alcanzable)
            if (prev == null) {
                return new ResultadoRuta(null, Double.MAX_VALUE, Double.MAX_VALUE);
            }

            // AGREGAR LA CONEXIÓN USADA al INICIO de la lista
            // (porque vamos hacia atrás, luego invertiremos)
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
