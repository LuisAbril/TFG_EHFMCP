# Documento de funciones por clase

Este documento resume todas las funciones presentes en las clases Java del proyecto y describe su comportamiento en formato de pseudocódigo.

## `Main`

### `main(String[] args)`
Pseudocódigo:
1. Crear la carpeta de salidas si no existe.
2. Buscar todas las instancias disponibles en la carpeta `instances`.
3. Si no hay instancias, mostrar error y terminar.
4. Ordenar la lista de instancias.
5. Para cada instancia:
   1. Cargar el archivo CSV.
   2. Establecer la instancia global para `Solution`.
   3. Crear el buscador local.
   4. Repetir durante un tiempo fijo:
      1. Construir una solución inicial con GRASP.
      2. Mejorar la solución con VND.
      3. Calcular el CO2 de la solución obtenida.
      4. Guardar la mejor solución encontrada.
   5. Si existe una mejor solución, guardarla en archivo.
   6. Registrar el CO2 y el número de iteraciones.
6. Calcular el promedio de iteraciones por instancia.
7. Copiar el resumen final al portapapeles.

### `listAvailableInstances()`
Pseudocódigo:
1. Crear una lista vacía de nombres de instancias.
2. Verificar que la carpeta `instances` exista.
3. Buscar todos los archivos con extensión `.csv`.
4. Para cada archivo encontrado:
   1. Quitar la extensión.
   2. Añadir el nombre a la lista.
5. Devolver la lista.

## `GRASP`

### `GRASP(Instance instance)`
Pseudocódigo:
1. Guardar la instancia recibida.

### `run()`
Pseudocódigo:
1. Crear estructuras para rutas, cargas y nodos sin asignar.
2. Eliminar el depósito de la lista de nodos pendientes.
3. Inicializar las unidades disponibles de cada vehículo.
4. Mientras queden nodos por asignar:
   1. Calcular la calidad de cada nodo no asignado.
   2. Obtener la mejor y la peor calidad.
   3. Calcular el umbral GRASP.
   4. Construir la lista restringida de candidatos.
   5. Elegir aleatoriamente un nodo de esa lista.
   6. Buscar vehículos factibles para transportar su producción.
   7. Si no hay vehículos factibles, detener la construcción.
   8. Elegir un vehículo factible al azar.
   9. Asignar el nodo a la ruta del vehículo.
   10. Actualizar la carga acumulada.
   11. Si el vehículo ya alcanzó su capacidad, reducir unidades disponibles y volver al depósito.
   12. Si no, continuar desde el nodo seleccionado.
   13. Eliminar el nodo de la lista pendiente.
5. Crear una solución con las rutas construidas.
6. Evaluar la solución.
7. Devolver la solución.

### `calculateQuality(String currentPos, NodeData node)`
Pseudocódigo:
1. Buscar el nodo actual a partir de la posición recibida.
2. Si no existe, devolver un valor máximo.
3. Calcular la distancia euclidiana entre la posición actual y el nodo candidato.
4. Combinar distancia y producción para obtener una calidad.
5. Devolver ese valor.

## `Instance`

### `Instance(String filePath)`
Pseudocódigo:
1. Guardar el nombre del archivo.
2. Inicializar listas vacías de vehículos y nodos.
3. Leer el archivo CSV.
4. Parsear su contenido.

### `readFile(String filePath)`
Pseudocódigo:
1. Abrir el archivo CSV.
2. Leer todas las líneas no vacías.
3. Guardar el contenido completo como texto.
4. Llamar al parser CSV con las líneas leídas.

### `parseCSV(List<String> lines)`
Pseudocódigo:
1. Recorrer cada línea del archivo.
2. Detectar si la línea corresponde a cabecera de vehículos o de nodos.
3. Si la línea pertenece a la sección de vehículos, parsearla como vehículo.
4. Si la línea pertenece a la sección de nodos, parsearla como nodo.

### `parseVehicle(String[] headers, String[] parts)`
Pseudocódigo:
1. Asociar cada cabecera con su valor correspondiente.
2. Leer nombre, carga, número de unidades, `Ef` y `Eo`.
3. Convertir valores numéricos de forma segura.
4. Crear y devolver un objeto `Vehicle`.

### `parseNode(String[] headers, String[] parts)`
Pseudocódigo:
1. Asociar cada cabecera con su valor correspondiente.
2. Leer nombre, coordenadas y producción.
3. Convertir valores numéricos de forma segura.
4. Crear y devolver un objeto `NodeData`.

### `parseDoubleSafe(String value)`
Pseudocódigo:
1. Si el valor es nulo o vacío, devolver 0.
2. Intentar convertir el texto a número decimal.
3. Si falla, devolver 0.

### `parseIntSafe(String value)`
Pseudocódigo:
1. Si el valor es nulo o vacío, devolver 0.
2. Intentar convertir el texto a entero.
3. Si falla, devolver 0.

### `getFileName()`
Pseudocódigo:
1. Devolver el nombre del archivo asociado a la instancia.

### `getVehicles()`
Pseudocódigo:
1. Devolver una copia de la lista de vehículos.

### `getNodes()`
Pseudocódigo:
1. Devolver una copia de la lista de nodos.

### `getNumberOfVehicles()`
Pseudocódigo:
1. Devolver la cantidad de vehículos almacenados.

### `getNumberOfNodes()`
Pseudocódigo:
1. Devolver la cantidad de nodos almacenados.

### `getContent()`
Pseudocódigo:
1. Devolver el contenido textual completo del archivo.

### `toString()`
Pseudocódigo:
1. Construir una cadena con el nombre del archivo.
2. Añadir el número de vehículos.
3. Añadir el número de nodos.
4. Devolver el texto resultante.

## `LocalSearch`

### `applyVND(Solution solution)`
Pseudocódigo:
1. Tomar la solución actual como referencia.
2. Probar primero el vecindario de inserción.
3. Si mejora, reiniciar la búsqueda desde el primer vecindario.
4. Si no mejora, probar 2-Opt.
5. Repetir hasta agotar los vecindarios.
6. Devolver la mejor solución encontrada.

### `applyBothLocalSearch(Solution solution)`
Pseudocódigo:
1. Aplicar 2-Opt a la solución original.
2. Aplicar inserción a la solución original.
3. Comparar el CO2 total de ambas soluciones.
4. Si el CO2 es igual, usar la distancia total como criterio de desempate.
5. Devolver la mejor solución.

### `isBetter(Solution candidate, Solution reference)`
Pseudocódigo:
1. Comparar el CO2 de ambas soluciones.
2. Si el candidato tiene menor CO2, considerar que mejora.
3. Si el CO2 es prácticamente igual, comparar la distancia total.
4. Devolver verdadero o falso según el criterio.

### `applyInsertion(Solution solution)`
Pseudocódigo:
1. Obtener la instancia activa.
2. Construir un mapa de coordenadas de los nodos.
3. Para cada ruta de vehículo:
   1. Copiar la ruta.
   2. Mientras exista una mejora:
      1. Probar mover cada nodo a todas las posiciones posibles.
      2. Calcular el cambio de distancia.
      3. Guardar el mejor movimiento encontrado.
      4. Aplicar el mejor movimiento si mejora la ruta.
4. Crear una nueva solución con las rutas mejoradas.
5. Evaluarla.
6. Devolverla.

### `apply2Opt(Solution solution)`
Pseudocódigo:
1. Obtener la instancia activa.
2. Construir un mapa de coordenadas de los nodos.
3. Para cada ruta de vehículo:
   1. Copiar la ruta.
   2. Mientras haya mejoras:
      1. Probar todos los pares de aristas candidatos.
      2. Calcular el delta de intercambio 2-Opt.
      3. Si el cambio mejora la ruta, invertir el segmento.
4. Crear una nueva solución con las rutas mejoradas.
5. Evaluarla.
6. Devolverla.

### `buildCoordinatesMap(List<NodeData> nodes)`
Pseudocódigo:
1. Crear un mapa vacío.
2. Para cada nodo:
   1. Guardar su nombre como clave.
   2. Guardar sus coordenadas como valor.
3. Devolver el mapa.

### `getDepotNodeKey(Map<String, double[]> coords)`
Pseudocódigo:
1. Si existe el nodo `P`, devolver `P`.
2. Si existe el nodo `0`, devolver `0`.
3. Si no existe ninguno, lanzar un error.

### `routeDistance(List<String> route, Map<String, double[]> coords, String depot)`
Pseudocódigo:
1. Si la ruta está vacía, devolver 0.
2. Empezar desde el depósito.
3. Sumar la distancia entre cada par consecutivo de nodos.
4. Añadir el tramo de regreso al depósito.
5. Devolver la distancia total.

### `twoOptDelta(List<String> route, int i, int k, Map<String, double[]> coords)`
Pseudocódigo:
1. Identificar las dos aristas actuales afectadas por el intercambio.
2. Calcular el coste actual de esas aristas.
3. Calcular el coste propuesto tras invertir el segmento.
4. Devolver la diferencia entre ambos costes.

### `distance(double[] p1, double[] p2)`
Pseudocódigo:
1. Calcular la diferencia en x.
2. Calcular la diferencia en y.
3. Aplicar la fórmula de distancia euclidiana.
4. Devolver el resultado.

### `reverseSegment(List<String> route, int i, int k)`
Pseudocódigo:
1. Intercambiar los elementos en los extremos del segmento.
2. Mover los índices hacia el centro.
3. Repetir hasta invertir completamente el segmento.

## `NodeData`

### `NodeData(String name, double x, double y, double prod)`
Pseudocódigo:
1. Guardar el nombre del nodo.
2. Guardar sus coordenadas.
3. Guardar su producción.

### `getName()`
Pseudocódigo:
1. Devolver el nombre del nodo.

### `getX()`
Pseudocódigo:
1. Devolver la coordenada X.

### `getY()`
Pseudocódigo:
1. Devolver la coordenada Y.

### `getProd()`
Pseudocódigo:
1. Devolver la producción del nodo.

## `RandomConstructive`

### `RandomConstructive(Instance instance)`
Pseudocódigo:
1. Guardar la instancia.
2. Inicializar un generador aleatorio sin semilla fija.

### `RandomConstructive(Instance instance, long seed)`
Pseudocódigo:
1. Guardar la instancia.
2. Inicializar un generador aleatorio con semilla fija.

### `run()`
Pseudocódigo:
1. Obtener nodos y vehículos de la instancia.
2. Filtrar los nodos cliente, excluyendo el depósito.
3. Crear una unidad por cada vehículo disponible.
4. Asociar a cada unidad su capacidad.
5. Repetir hasta encontrar una solución factible o agotar intentos:
   1. Crear una solución vacía.
   2. Mezclar aleatoriamente los nodos cliente.
   3. Para cada nodo:
      1. Buscar vehículos que aún tengan capacidad suficiente.
      2. Si no hay vehículos factibles, marcar la solución como inviable.
      3. Si hay vehículos factibles, elegir uno al azar.
      4. Añadir el nodo a su ruta.
      5. Actualizar la carga de ese vehículo.
   4. Si la solución es factible, evaluarla y devolverla.
6. Si no se encuentra solución, lanzar un error.

## `Solution`

### `Solution()`
Pseudocódigo:
1. Inicializar rutas vacías.
2. Inicializar distancia total en 0.
3. Inicializar CO2 total en 0.

### `Solution(Map<String, List<String>> vehicleRoutes, double totalDistance, double totalCO2)`
Pseudocódigo:
1. Copiar las rutas recibidas.
2. Guardar la distancia total.
3. Guardar el CO2 total.

### `setInstance(Instance inst)`
Pseudocódigo:
1. Guardar la instancia estática.

### `getInstance()`
Pseudocódigo:
1. Devolver la instancia estática.

### `addRoute(String vehicle, List<String> route)`
Pseudocódigo:
1. Copiar la ruta recibida.
2. Asociarla al vehículo indicado.

### `getVehicleRoutes()`
Pseudocódigo:
1. Devolver una copia del mapa de rutas.

### `setTotalDistance(double totalDistance)`
Pseudocódigo:
1. Guardar la nueva distancia total.

### `getTotalDistance()`
Pseudocódigo:
1. Devolver la distancia total almacenada.

### `setTotalCO2(double totalCO2)`
Pseudocódigo:
1. Guardar el nuevo CO2 total.

### `getTotalCO2()`
Pseudocódigo:
1. Devolver el CO2 total almacenado.

### `evaluate()`
Pseudocódigo:
1. Verificar que exista una instancia activa.
2. Obtener nodos y vehículos de la instancia.
3. Crear un mapa de coordenadas para acceso rápido.
4. Para cada ruta de vehículo:
   1. Buscar la información del vehículo asociado.
   2. Si existe la ruta:
      1. Empezar desde el depósito.
      2. Recorrer cada nodo de la ruta.
      3. Sumar la distancia entre el nodo previo y el actual.
      4. Calcular emisiones usando la carga actual antes de recoger producción.
      5. Incrementar la carga con la producción del nodo.
      6. Al final, sumar el regreso al depósito.
      7. Calcular las emisiones del retorno con la carga completa.
5. Guardar la distancia y el CO2 calculados.

### `calculateDistance(double[] point1, double[] point2)`
Pseudocódigo:
1. Calcular la diferencia entre las coordenadas.
2. Aplicar la fórmula euclidiana.
3. Devolver la distancia.

### `getNodeProd(String nodeName, List<NodeData> nodes)`
Pseudocódigo:
1. Buscar el nodo por nombre.
2. Si se encuentra, devolver su producción.
3. Si no existe, devolver 0.

### `saveToFile(String filePath)`
Pseudocódigo:
1. Abrir el archivo de salida.
2. Escribir cada ruta con su vehículo.
3. Dejar una línea en blanco.
4. Escribir el CO2 total con formato fijo.
5. Escribir la distancia total con formato fijo.

### `toString()`
Pseudocódigo:
1. Construir una cadena con todas las rutas.
2. Añadir el CO2 total.
3. Añadir la distancia total.
4. Devolver el texto completo.

## `Vehicle`

### `Vehicle(String name, double load, int numUnits, double ef, double eo)`
Pseudocódigo:
1. Guardar el nombre del vehículo.
2. Guardar su capacidad de carga.
3. Guardar el número de unidades.
4. Guardar el factor de emisión máximo.
5. Guardar el factor de emisión mínimo.

### `getName()`
Pseudocódigo:
1. Devolver el nombre del vehículo.

### `getLoad()`
Pseudocódigo:
1. Devolver la capacidad de carga.

### `getNumUnits()`
Pseudocódigo:
1. Devolver el número de unidades disponibles.

### `getEf()`
Pseudocódigo:
1. Devolver el valor `Ef`.

### `getEo()`
Pseudocódigo:
1. Devolver el valor `Eo`.
