# TFG_EHFMCP

Proyecto Java para construcción y evaluación de soluciones de rutas (VRP) con cálculo de emisiones.

## Estructura
- `src/main/java/tfg/Instance.java`: Lector y parser de instancias CSV (vehículos y nodos).
- `src/main/java/tfg/Solution.java`: Representa una solución con rutas, distancia y CO2. Incluye `evaluate()`.
- `src/main/java/tfg/RandomConstructive.java`: Algoritmo constructivo aleatorio (`run()`).
- `src/main/java/tfg/Main.java`: Entrada del programa. Lista instancias y ejecuta `RandomConstructive`.
- `instances/`: Carpeta con archivos de instancias CSV (p.ej. `instance1.csv`).

## Formato de instancia CSV
Se divide en dos secciones.

Vehículos:
```
Vehicle,Load,Num_v,Ef,Eo
V1,16000,2,0.931,0.586
...
```
- `Vehicle`: ID del vehículo
- `Load`: capacidad máxima (kg)
- `Num_v`: cantidad de unidades disponibles
- `Ef`: emisiones lleno
- `Eo`: emisiones vacío

Nodos:
```
Node,coord_x,coord_y,prod,
P,30,33,0,
F1,23,15,2071,
...
```
- `Node`: ID del nodo (P es depósito)
- `coord_x`, `coord_y`: coordenadas
- `prod`: producción/demanda del nodo

## Ejecución
1. Coloca tus instancias CSV en `instances/`.
2. Ejecuta `Main`. El programa:
   - Lista las instancias disponibles
   - Solicita el nombre (sin extensión)
   - Construye una solución aleatoria y la evalúa

Salida de solución (formato):
```
V2		['F9', 'F1', 'F19', 'F11', 'F16', 'F18']
V2		['F15', 'F8', 'F10', 'F7', 'F20']
V3		['F12', 'F17', 'F13', 'F3', 'F6']
V3		['F2', 'F5', 'F14', 'F4']

CO2		110.2791827
Distance	246.042166
```

## Evaluación de emisiones
El método `Solution.evaluate()` calcula por tramo:

Emisiones tramo = `((Ef − Eo) / Load) * peso * distancia + Eo`
- `Ef`, `Eo`: del vehículo.
- `Load`: capacidad del vehículo.
- `peso`: se toma como `prod` del siguiente nodo del tramo. Para `P → primer nodo` se usa `prod` del primer nodo; para `último nodo → P` se usa 0.
- `distancia`: euclidiana entre coordenadas.

También se acumula la distancia total.

## Notas
- `RandomConstructive` asigna nodos aleatoriamente a las unidades de vehículos disponibles (según `Num_v`).
- Si deseas reproducibilidad, usa el constructor con semilla.

## Git (opcional)
Si Git no se reconoce, añade `C:\Program Files\Git\cmd` al PATH del usuario y reinicia VS Code.