# TFG_EHFMCP

Aplicación Java que implementa las clases Instance y Solution para el manejo de instancias y soluciones de problemas.

## Estructura del Proyecto

```
TFG_EHFMCP/
├── src/
│   └── main/
│       └── java/
│           └── tfg/
│               ├── Instance.java   # Clase que lee y procesa archivos de instancias
│               ├── Solution.java   # Clase que representa soluciones
│               └── Main.java       # Clase principal de demostración
├── instance.txt                    # Archivo de ejemplo de instancia
└── .gitignore                      # Archivos a ignorar en Git
```

## Clases Principales

### Instance

Clase que representa una instancia del problema. Lee un archivo, lo procesa y guarda la información en diferentes atributos.

**Características:**
- Constructor que acepta la ruta de un archivo
- Lee y procesa el contenido del archivo
- Almacena la información en múltiples atributos:
  - `fileName`: Nombre del archivo leído
  - `data`: Lista de líneas del archivo
  - `numberOfLines`: Número de líneas leídas
  - `content`: Contenido completo del archivo

**Ejemplo de uso:**
```java
Instance instance = new Instance("instance.txt");
System.out.println("Líneas leídas: " + instance.getNumberOfLines());
```

### Solution

Clase que representa una posible solución al problema. Incluye la instancia como atributo estático.

**Características:**
- Atributo estático `Instance` compartido por todas las soluciones
- Métodos para establecer y obtener la instancia
- Atributos para datos de solución y valor objetivo

**Ejemplo de uso:**
```java
Solution.setInstance(instance);
Solution solution = new Solution("Datos de solución", 42.5);
Instance inst = Solution.getInstance();
```

## Compilación y Ejecución

### Compilar el proyecto:
```bash
javac -d bin src/main/java/tfg/*.java
```

### Ejecutar el programa:
```bash
java -cp bin tfg.Main
```

## Requisitos

- Java 17 o superior
- Un archivo de instancia (por defecto: `instance.txt`)

## Ejemplo de Salida

```
Instancia cargada:
Instance{fileName='instance.txt', numberOfLines=10}
Número de líneas: 10

Solución creada:
Solution{solutionData='Solución de prueba', objectiveValue=42.5, instance=Instance{fileName='instance.txt', numberOfLines=10}}

Instancia desde Solution:
Instance{fileName='instance.txt', numberOfLines=10}
```