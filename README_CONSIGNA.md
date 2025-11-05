# AEROLÍNEAS PC21 - Trabajo Práctico

## Enunciado
Una compañía aérea administra su flota de vuelos de cabotaje entre las ciudades: Buenos Aires, Córdoba, Mendoza, Bariloche, Santa Cruz, Santa Fe y Posadas.

Cada vuelo se realiza con una aeronave con 3 secciones (A, B, C), 10 asientos por sección (total 30 asientos). Los asientos se etiquetan A1–A10, B1–B10, C1–C10.

## 1. Reglas de Reservas y Asientos

### Asignación de Asientos
- Al confirmar una reserva, el sistema debe asignar un asiento aleatorio dentro de una sección, pero manteniendo el balance de ocupación por secciones (para balancear peso, aunque el peso no forma parte de los datos):
  - La diferencia de asientos ocupados entre cualesquiera dos secciones no debe superar 1.
  - Estrategia esperada: elegir la sección menos cargada (si hay empate, elegir una al azar), y dentro de esa sección, elegir aleatoriamente un asiento libre.

### Estructura de Datos para Reservas
- Las reservas de un vuelo deben guardarse en un Árbol AVL (por ejemplo, con clave el código de reserva o el id de asiento), soportando: insertar, buscar, eliminar (si se cancela), y un recorrido en orden para listar ocupación.

## 2. Precios y Reglas Dinámicas

Cada destino tiene un precio base (ver tabla). Ese precio puede variar según:
1. **+10%** si el vuelo alcanza ≥ 95% de ocupación.
2. **+20%** si el trayecto final es directo (sin trasbordo).

**Nota:** Si el alumno calcula una ruta con trasbordos (porque no hay vuelo directo o porque optimiza tiempo/costo), no aplica el +20% por directo. El +10% sí aplica si se llega a ≥95% de ocupación del vuelo (por tramo).

## 3. Red de Destinos (Grafo)

Los aeropuertos son nodos; las conexiones posibles son aristas con tiempo (horas) y precio base (ARS) por tramo. El sistema debe:
- Permitir recorridos BFS/DFS sobre la red (por ejemplo, "listar todos los aeropuertos alcanzables desde X", "detectar componentes").
- Calcular itinerarios mínimos con Dijkstra, optimizando tiempo total (criterio principal).
  - Empate de tiempo → minimiza precio total.
- Si existe vuelo directo al destino, puede elegirse directo; de lo contrario, calcular la ruta mínima.
- El precio final del itinerario es la suma de precios de los tramos, con los ajustes (+10% ocupación por cada tramo que cumpla ≥95%, y +20% si el itinerario es directo).

## DATOS DEL PROBLEMA

### A. Vuelos Directos desde Buenos Aires
(Estos tramos existen como aristas directas en el grafo.)

| Origen       | Destino     | Directo | Precio base (ARS) | Tiempo (h) |
|--------------|-------------|---------|-------------------|------------|
| Buenos Aires| Córdoba    | Sí      | 120 000          | 1.2        |
| Buenos Aires| Mendoza    | Sí      | 150 000          | 1.7        |
| Buenos Aires| Bariloche  | Sí      | 220 000          | 2.2        |
| Buenos Aires| Santa Fe   | Sí      | 100 000          | 1.0        |
| Buenos Aires| Posadas    | Sí      | 140 000          | 1.5        |
| Buenos Aires| Santa Cruz | No      | —                | —          |

**Nota:** Santa Cruz no tiene vuelo directo desde Buenos Aires en este modelo. Se llega por conexión (ver grafo).

### B. Otras Conexiones (para Trasbordos)
(Aristas adicionales del grafo; todos los tramos son bidireccionales con el mismo costo/tiempo.)

| Tramo                | Precio base (ARS) | Tiempo (h) |
|----------------------|-------------------|------------|
| Córdoba ↔ Mendoza   | 90 000           | 1.1        |
| Córdoba ↔ Santa Fe  | 70 000           | 0.8        |
| Mendoza ↔ Bariloche | 120 000          | 1.6        |
| Bariloche ↔ Santa Cruz | 160 000        | 2.0        |
| Mendoza ↔ Santa Cruz   | 170 000        | 2.6        |
| Santa Fe ↔ Posadas     | 80 000        | 1.2        |

Con esto, por ejemplo, Buenos Aires → Santa Cruz puede resolverse como:
- BUE→BRC→SCZ (2.2h + 2.0h = 4.2h)
- BUE→MDZ→SCZ (1.7h + 2.6h = 4.3h)

Dijkstra debe elegir la ruta mínima en tiempo, y ante empate, la de menor costo.

## REQUERIMIENTOS DE IMPLEMENTACIÓN

### 1. Grafo de Vuelos
- **Representación:** lista de adyacencia.
- **Operaciones:**
  - Cargar nodos y aristas (con precio y tiempo).
  - BFS y DFS desde una ciudad dada.
  - Dijkstra para ruta mínima en tiempo (peso principal = tiempo; si hay empate, decide por suma de precios).
  - Reconstrucción del itinerario (lista ordenada de ciudades y tramos).

### 2. Gestión de Vuelos/Aviones
- Cada vuelo tiene: código de vuelo, origen, destino (o ruta si hay trasbordos), aeronave con 3×10 asientos.
- **Asignación de asiento en reserva:**
  - Mantener contadores de ocupación por sección A/B/C.
  - Elegir la(s) sección(es) con menor ocupación (si hay varias, elegir una al azar).
  - Dentro de la sección elegida, asiento aleatorio entre los libres.
  - Garantizar que la diferencia de ocupación entre secciones ≤ 1 siempre (si una sección se llena, repartir entre las restantes).
- **Estructura obligatoria:** AVL para reservas del vuelo (clave sugerida: código de reserva o (sección, asiento)).
  - Operaciones mínimas: insertar, buscar, eliminar, inOrder().

### 3. Precio Final
- Suma de precios base de los tramos del itinerario.
- +20% si el itinerario es directo (único tramo).
- +10% por tramo cuya ocupación sea ≥ 95% en el momento de la compra (esto se evalúa por vuelo/tramo, no por itinerario completo).
- Mostrar detalle: precio base por tramo, recargos aplicados, total final.

### 4. Interfaz de Uso (Consola)
- Alta de vuelo(s).
- Consulta de ruta: origen, destino → mostrar ruta mínima (tiempo total, precio base total).
- Reserva de pasaje: origen, destino → calcula ruta, crea o asigna vuelo(s) por tramo, asigna asiento(s), imprime comprobante:
  - Código(s) de vuelo, tramos, asiento(s) asignado(s), precio final.
- Consulta de ocupación por vuelo: porcentaje global, ocupación por sección, listado de reservas (inOrder del AVL).

