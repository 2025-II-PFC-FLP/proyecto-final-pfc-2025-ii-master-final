# Informe de proceso proyecto final - El problema del riego óptimo 

- [`2.3. Calculando el tiempo de inicio de riego`](#calculando-el-tiempo-de-inicio-de-riego)
- [`2.4. Calculando costos`](#calculando-costos)
- [`2.5. Generando programaciones de riego`](#generando-programaciones-de-riego)
- [`2.6. Calculando una programación de riego óptima`](#calculando-una-programación-de-riego-óptima)
- [`3.1. Paralelizando el cálculo de los costos de riego y de movilidad`](#paralelizando-el-cálculo-de-los-costos-de-riego-y-de-movilidad)
- [`3.2. Paralelizando la generación de programaciones de riego`](#paralelizando-la-generación-de-programaciones-de-riego)
- [`3.3. Paralelizando la programación de riego óptima`](#paralelizando-la-programación-de-riego-óptima)


## 2.3. Calculando el tiempo de inicio de riego

### Implementación en Scala

### Definición

### Explicación paso a paso

#### Ejemplo de uso

### Diagrama de llamados de pila



## 2.4. Calculando costos

### Implementación en Scala

### Definición

### Explicación paso a paso

#### Ejemplo de uso

### Diagrama de llamados de pila



## 2.5. Generando programaciones de riego

### Implementación en Scala


````scala
  def generarProgramacionesRiego(f : Finca) : Vector[ProgRiego] =
  {
    val rangoFinca : Vector[Int] = f.indices.toVector

    val matrizBase : Vector[Vector[Int]] = rangoFinca.map(_ => rangoFinca)

    val productoCartesiano : Vector[Vector[Int]] = matrizBase.foldLeft(Vector(Vector.empty[Int])){(acc, vector) =>
      for{
        prefijo <- acc
        sufijo <- vector
      }yield prefijo :+ sufijo
    }
    productoCartesiano.filter(x => x.distinct.length == f.length)
  }
````

### Definición

En esta función se busca conocer las diferentes programaciones de riego que se puedan tener entre los tablones de una `finca` `f` bajo el uso de permutaciones.  

- El argumento ``f`` es un vector de tuplas, cada tupla formada por tres enteros.
- Retorna las permutaciones que representan las diferentes formas de riego que hay para los tablones en `f`.


### Explicación paso a paso

#### Ejemplo de uso
````scala
val tabFinca : Finca = fincaAlAzar(3)
val progGenerada : Vector[ProgRiego] = generarProgramacionesRiego(tabFinca)
    
````
- Con el valor `rangoFinca` se busca tener un vector con los indices de cada tablon que compone `f`, para este caso se obtendría `Vector(0, 1, 2)`.
- `matrizBase` genera una matriz cuadrada con el vector `rangoFinca` al recorrer cada posición de esta y asociarlo con  el mismo vector, obteniendo así `Vector(Vector(0, 1, 2), Vector(0, 1, 2), Vector(0, 1, 2))`.
- `productoCartesiano` aplica la operación binaria foldLeft a `matrizBase` colocando como valor inicial una matriz vacia. Con una expresión for en la función anonima del foldLeft se busca recorrer cada vector en la matriz acc siendo este el prefijo, y el sufijo recorre cada entero dentro del vector que el foldLeft extrae de la matrizBase; con esto la matriz acc se va expandiendo logrando todas las combinaciones posibles con repetición de los indices conrrespondientes a los tablones de la finca dada. al final de cada `yield` se tendría:
    - paso 1: `acc =>  Vector(Vector(0), Vector(1), Vector(2))` 
    - paso 2: `acc =>  Vector(Vector(0, 0), Vector(0, 1), Vector(0, 2), Vector(1, 0), Vector(1, 1), Vector(1, 2), Vector(2, 0), Vector(2, 1), Vector(2, 2))` 
    - paso 2: `acc =>  Vector(Vector(0, 0, 0), Vector(0, 0, 1), Vector(0, 0, 2), Vector(0, 1, 0), Vector(0, 1, 1), Vector(0, 1, 2), Vector(0, 2, 0), Vector(0, 2, 1), Vector(0, 2, 2), Vector(1, 0, 0), Vector(1, 0, 1), Vector(1, 0, 2), Vector(1, 1, 0), Vector(1, 1, 1), Vector(1, 1, 2), Vector(1, 2, 0), Vector(1, 2, 1), Vector(1, 2, 2), Vector(2, 0, 0), Vector(2, 0, 1), Vector(2, 0, 2), Vector(2, 1, 0), Vector(2, 1, 1), Vector(2, 1, 2), Vector(2, 2, 0), Vector(2, 2, 1), Vector(2, 2, 2))`
- Finalmente se retorna el producto cartesiano al aplicar un filtrado a cada uno de sus vectores, donde se "eliminan" los valores repetidos en cada uno y se evalúa que su longitud en la función anónima sea igual a la longitud de tablones contenidos en `f`.

    - aplicando el distinct `productoCartesiano => Vector(Vector(0), Vector(0, 1), Vector(0, 2), Vector(1, 0), Vector(0, 1), Vector(0, 1, 2), Vector(2, 0), Vector(0, 2, 1), Vector(0, 2), Vector(1, 0), Vector(0, 1), Vector(1, 0, 2), Vector(1, 0), Vector(1), Vector(1, 2), Vector(1, 2, 0), Vector(1, 2), Vector(1, 2), Vector(2, 0), Vector(2, 0, 1), Vector(2, 0), Vector(2, 1, 0), Vector(2, 1), Vector(1, 2), Vector(2, 0), Vector(2, 1), Vector(2))`

    - si a lo anterior se le aplica la condición de que la cantidad de los elementos en los vectores sea igual a la cantidad de tablones se tiene `productoCartesiano => Vector( Vector(0, 1, 2), Vector(0, 2, 1), Vector(1, 0, 2), Vector(1, 2, 0), Vector(2, 0, 1), Vector(2, 1, 0))` 


Lo anterior se entrega como una visión general de como se va transformando el vector con cada función de alto orden integrada que se aplica. realmente se itera vector por vector, se le aplica distinct, se conoce su longitud y se aplica el filtro con la longitud de los tablones en `f`. como se puede ver a continuación.

- primer vector `Vector(0, 0, 0)`.
- distinct aplicado `Vector(0)`.
- longitud del vector `1`.
- comparación lógica igualdad `1 == 4` retorna false.
- el filtro no agrega `Vector(0, 0, 0)` en la nueva colección.
  
### Diagrama de llamados de pila

````mermaid
flowchart TD

    A["Rango de finca<br/>`rangoFinca = Vector(0,1,2)`"] --> B

    B["Generar `matrizBase`<br/>Recorrer `rangoFinca × rangoFinca`<br/>→ `Vector(Vector(0,1,2), Vector(0,1,2), Vector(0,1,2))`"] --> C

    C["`foldLeft` sobre `matrizBase`<br/>Valor inicial: `Vector(Vector())`"] --> D1

    D1["Paso 1<br/>Expansión 1<br/>`acc = Vector(Vector(0), Vector(1), Vector(2))`"] --> D2

    D2["Paso 2<br/>Expansión 2<br/>`acc = 9` combinaciones<br/>`(0,0), (0,1), (0,2)…`"] --> D3

    D3["Paso 3<br/>Expansión final<br/>`acc = 27` combinaciones<br/>`3 × 3 × 3`"] --> E

    E["Filtrado final<br/>Para cada vector `v`:<br/>`v.distinct.length == f.length`"] --> F[colección resultante]

    E -->|"false → descartar"| FIN["No se agrega a la colección final"]
````

````mermaid
flowchart TD
    subgraph "Ejemplo filtrado para `Vector(0,0,0)`<br/>"
        F1["Vector original: `(0,0,0)`"]
        F2["`distinct → Vector(0)`"]
        F3["longitud = `1`"]
        F4["comparación: `1 == 4` → false"]
    end
````




## 2.6. Calculando una programación de riego óptima

### Implementación en Scala

### Definición

### Implementación en Scala

### Explicación paso a paso

#### Ejemplo de uso

### Diagrama de llamados de pila



## 3.1. Paralelizando el cálculo de los costos de riego y de movilidad

### Implementación en Scala

### Definición

### Implementación en Scala

### Explicación paso a paso

#### Ejemplo de uso

### Diagrama de llamados de pila



## 3.2. Paralelizando la generación de programaciones de riego

### Implementación en Scala

### Definición

### Implementación en Scala

### Explicación paso a paso

#### Ejemplo de uso

### Diagrama de llamados de pila



## 3.3. Paralelizando la programación de riego óptima

### Implementación en Scala

### Definición

### Implementación en Scala

### Explicación paso a paso

#### Ejemplo de uso

### Diagrama de llamados de pila