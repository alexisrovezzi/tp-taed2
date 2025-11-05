# Aerolíneas PC21 - Trabajo Práctico

Implementación completa del sistema de aerolíneas en Java según las especificaciones del trabajo práctico.

## Estructura del Proyecto

```
src/main/java/com/aerolineas/
├── Aeropuerto.java          # Representa aeropuertos
├── Conexion.java            # Conexiones entre aeropuertos
├── Grafo.java              # Grafo con algoritmos BFS/DFS/Dijkstra
├── ArbolAVL.java           # Árbol AVL genérico
├── Asiento.java            # Representa asientos
├── Aeronave.java           # Gestión de asientos con balanceo
├── Reserva.java            # Reservas de pasajes
├── Vuelo.java              # Vuelos con reservas
├── SistemaAerolineas.java  # Lógica principal del sistema
└── Main.java               # Interfaz de consola
```

## Funcionalidades Implementadas

### 1. Grafo de Vuelos
- **Lista de adyacencia** para representar conexiones
- **BFS/DFS** para recorridos
- **Dijkstra** optimizando tiempo (criterio principal), precio (empate)

### 2. Gestión de Asientos
- **3 secciones** (A, B, C) con 10 asientos cada una
- **Asignación balanceada**: diferencia máxima de 1 asiento entre secciones
- Estrategia: sección menos cargada → asiento aleatorio

### 3. Árbol AVL para Reservas
- **Inserción, búsqueda, eliminación**
- **Recorrido inorder** para listar ocupación
- Claves: código de reserva y asiento

### 4. Cálculo de Precios Dinámicos
- **Precio base** según tabla de vuelos
- **+10%** por tramo con ≥95% ocupación
- **+20%** si itinerario es directo (único tramo)

### 5. Interfaz de Consola
- Consulta de rutas mínimas
- Reserva de pasajes con asignación automática
- Consulta de ocupación por vuelo
- Cancelación de reservas
- Recorridos BFS/DFS
- Lista de aeropuertos alcanzables

## Datos Iniciales

### Aeropuertos
- **BUE**: Buenos Aires
- **COR**: Córdoba
- **MDZ**: Mendoza
- **BRC**: Bariloche
- **SCZ**: Santa Cruz
- **SFN**: Santa Fe
- **PSS**: Posadas

### Vuelos Directos desde Buenos Aires
*(Unidireccionales - solo salida desde BUE)*
| Destino | Precio Base | Tiempo |
|---------|-------------|--------|
| Córdoba | $120,000 | 1.2h |
| Mendoza | $150,000 | 1.7h |
| Bariloche | $220,000 | 2.2h |
| Santa Fe | $100,000 | 1.0h |
| Posadas | $140,000 | 1.5h |

**Nota:** Los vuelos directos desde Buenos Aires son unidireccionales. Para viajar desde Córdoba/Mendoza/etc. hacia Buenos Aires, se deben usar las conexiones bidireccionales de la sección siguiente.

### Conexiones Adicionales
- Córdoba <-> Mendoza: $90,000, 1.1h
- Córdoba <-> Santa Fe: $70,000, 0.8h
- Mendoza <-> Bariloche: $120,000, 1.6h
- Bariloche <-> Santa Cruz: $160,000, 2.0h
- Mendoza <-> Santa Cruz: $170,000, 2.6h
- Santa Fe <-> Posadas: $80,000, 1.2h

## Algoritmo de Dijkstra - Ruta Óptima

### Cómo funciona Dijkstra en este sistema

El algoritmo de **Dijkstra** encuentra la ruta óptima entre aeropuertos considerando una **jerarquía de criterios**:

#### 🎯 **Criterio Principal: TIEMPO MÍNIMO**
- **Prioridad máxima**: Minimizar horas totales del viaje
- **Ejemplo**: BUE→BRC→SCZ (4.2h) vs BUE→MDZ→SCZ (4.3h) → Elige la primera

#### 💰 **Criterio Secundario: PRECIO MÍNIMO**
- **Solo en empate de tiempo**: Si dos rutas duran igual, elige la más barata
- **Ejemplo**: Dos rutas de 3.0h → Elige la de menor precio total

### Implementación Técnica

#### **Estructuras de Datos**
- **`tiempoMinimo`**: Mapa con costo de tiempo acumulado a cada aeropuerto
- **`precioMinimo`**: Mapa con costo de precio acumulado a cada aeropuerto
- **`predecesor`**: Mapa para reconstruir la ruta (aeropuerto anterior)
- **`conexionPredecesor`**: Mapa con la conexión específica usada

#### **Cola de Prioridad (PriorityQueue)**
```java
PriorityQueue<Aeropuerto> pq = new PriorityQueue<>(
    Comparator.comparingDouble(tiempoMinimo::get)  // Primero tiempo
        .thenComparingDouble(precioMinimo::get)   // Luego precio
);
```

#### **Lógica de Decisión**
```java
// ¿Es más rápida?
if (nuevoTiempo < tiempoMinimo.get(vecino)) {
    mejorRuta = true;
}
// ¿Mismo tiempo pero más barato?
else if (nuevoTiempo == tiempoMinimo.get(vecino) &&
          nuevoPrecio < precioMinimo.get(vecino)) {
    mejorRuta = true;
}
```

#### **Reconstrucción de Ruta**
- **Desde el destino**: Retrocede usando `predecesor`
- **Lista de conexiones**: Se construye en orden inverso y se invierte
- **Resultado**: Lista ordenada de conexiones desde origen a destino

### Ejemplo Práctico: BUE → SCZ

**Rutas posibles:**
1. BUE → BRC → SCZ: 2.2h + 2.0h = **4.2h** total
2. BUE → MDZ → SCZ: 1.7h + 2.6h = **4.3h** total

**Dijkstra elige**: Ruta 1 (más rápida por 0.1h)

**Proceso paso a paso:**
1. Inicializa: BUE=0h, otros=∞
2. Explora BUE: encuentra BRC(2.2h) y MDZ(1.7h)
3. Explora MDZ(1.7h): encuentra SCZ vía MDZ(1.7+2.6=4.3h)
4. Explora BRC(2.2h): encuentra SCZ vía BRC(2.2+2.0=4.2h) ← ¡Mejor!
5. Ruta final: BUE → BRC → SCZ (4.2h)

## Menú de Opciones

El sistema ofrece las siguientes opciones en el menú principal:

1. **Consultar ruta mínima entre aeropuertos** - Muestra la ruta óptima con Dijkstra
2. **Realizar reserva de pasaje** - Crea reserva con asignación automática de asiento
3. **Consultar ocupación de vuelo** - Muestra estado del vuelo y reservas
4. **Cancelar reserva** - Elimina una reserva existente
5. **Listar aeropuertos alcanzables** - Muestra destinos accesibles desde un aeropuerto
6. **Ejecutar BFS desde aeropuerto** - Recorrido BFS del grafo
7. **Ejecutar DFS desde aeropuerto** - Recorrido DFS del grafo
8. **Limpiar consola** - Limpia la pantalla para mejor visualización
9. **Listar todas las reservas** - Muestra todas las reservas del sistema con estadísticas
10. **Salir** - Termina el programa

## Compilación y Ejecución

### Compilación
```bash
cd src/main/java
javac com/aerolineas/*.java
```

### Ejecución
```bash
cd src/main/java
java com.aerolineas.Main
```

### Script Windows (Recomendado)
```bash
run.bat
```
**Nota:** El script `run.bat` compila automáticamente todos los archivos Java antes de ejecutar el programa. Si hay errores de compilación, te informará y no ejecutará hasta que los corrijas.

## Ejemplos de Uso

### Consulta de Ruta
```
Origen: BUE
Destino: SCZ
```
Resultado: Ruta BUE->BRC->SCZ (4.2h, precio base total según ocupación)

### Reserva de Pasaje
```
Origen: BUE
Destino: COR
Pasajero: Juan Pérez
```
Resultado:
```
=== COMPROBANTE DE RESERVA ===
Ruta: Buenos Aires -> Córdoba
Tiempo total: 1.2 horas
Vuelos asignados:
  RES0001-1: Vuelo BUECOR01 (BUE-COR) - Asiento A4
    Precio base: $120000

Recargos aplicados:
  - +20% por vuelo directo (sin trasbordos)

Precio final: $144000
```
Asigna asiento automáticamente, calcula precio con recargos si aplican.

**Cálculo de precios:**
- **Precio base**: Costo individual de cada tramo según tabla
- **+10% por tramo**: Si ocupación del vuelo ≥95% (por tramo)
- **+20% total**: Si el itinerario es directo (único tramo)
- **Precio final**: Suma de precios con recargos aplicados

### Lista de Todas las Reservas
```
=== TODAS LAS RESERVAS ===

RESERVAS REALIZADAS:

Vuelo BUECOR01 (Buenos Aires -> Córdoba):
Ocupación: 2/30 asientos
  Reserva RES0001-1 - Juan Pérez: Asiento A1
  Reserva RES0001-2 - María García: Asiento A2

--- ESTADÍSTICAS TOTALES ---
Total de reservas: 2
Vuelos con reservas: 1
Asientos ocupados: 2
```
Muestra todas las reservas agrupadas por vuelo, con información de ocupación y estadísticas globales.

### Consulta de Ocupación
```
Código de vuelo: BUECOR01
```
Muestra porcentaje global, ocupación por sección y lista de reservas.

## Algoritmos Implementados

### Dijkstra con Doble Criterio
1. **Criterio principal**: Tiempo total mínimo
2. **Criterio secundario**: Precio total mínimo (en caso de empate de tiempo)

### Balanceo de Asientos
1. Contar ocupación actual por sección
2. Elegir sección(es) con menor ocupación
3. Si hay empate, elegir sección al azar
4. Dentro de sección, elegir asiento libre al azar
5. Garantizar diferencia ≤ 1 entre secciones

## Características Técnicas

- **Java puro** sin dependencias externas
- **Genéricos** para reutilización de código
- **Validación de entrada** en interfaz de consola
- **Manejo de errores** para casos edge
- **Documentación** completa en código
