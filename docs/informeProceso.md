# Informe de proceso proyecto final - El problema del riego óptimo 

- [`2.3. Calculando el tiempo de inicio de riego`](#23-calculando-el-tiempo-de-inicio-de-riego)
- [`2.4. Calculando costos`](#24-calculando-costos)
- [`2.5. Generando programaciones de riego`](#25-generando-programaciones-de-riego)
- [`2.6. Calculando una programación de riego óptima`](#26-calculando-una-programación-de-riego-óptima)
- [`3.1. Paralelizando el cálculo de los costos de riego y de movilidad`](#31-paralelizando-el-cálculo-de-los-costos-de-riego-y-de-movilidad)
- [`3.2. Paralelizando la generación de programaciones de riego`](#32-paralelizando-la-generación-de-programaciones-de-riego)
- [`3.3. Paralelizando la programación de riego óptima`](#33-paralelizando-la-programación-de-riego-óptima)


---

## 2.3. Calculando el tiempo de inicio de riego

### Implementación en Scala

### Definición

### Explicación paso a paso

#### Ejemplo de uso

### Diagrama de llamados de pila

---

## 2.4. Calculando costos

### Implementación en Scala

### Definición

### Explicación paso a paso

#### Ejemplo de uso

### Diagrama de llamados de pila


---

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

- El argumento `f` es un vector de tuplas, cada tupla formada por tres enteros.
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

    A["Rango de finca<br/> rangoFinca = Vector(0,1,2)"] --> B

    B["Generar matrizBase<br/>Recorrer rangoFinca × rangoFinca<br/>→ Vector(Vector(0,1,2), Vector(0,1,2), Vector(0,1,2))"] --> C

    C["foldLeft sobre matrizBase<br/>Valor inicial: Vector(Vector())"] --> D1

    D1["Paso 1<br/>Expansión 1<br/>acc = Vector(Vector(0), Vector(1), Vector(2))"] --> D2

    D2["Paso 2<br/>Expansión 2<br/>acc = 9 combinaciones<br/>(0,0), (0,1), (0,2)…"] --> D3

    D3["Paso 3<br/>Expansión final<br/>acc = 27 combinaciones<br/>3 × 3 × 3"] --> E

    E["Filtrado final<br/>Para cada vector v:<br/>v.distinct.length == f.length"] --> F[colección resultante]

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

---


## 2.6. Calculando una programación de riego óptima

### Implementación en Scala

### Definición

### Explicación paso a paso

#### Ejemplo de uso

### Diagrama de llamados de pila

---

## 3.1. Paralelizando el cálculo de los costos de riego y de movilidad

### 3.1.1. Paralelización cálculo costos de riego

#### Definición

La función costoRiegoFincaPar calcula el costo total de riego de una finca utilizando una programación
de riego Pi, dividiendo el rango en intervalor para cada tablón, calculando el costo de cada uno en paralelo, 
para sumar los costos parciales y retorna la suma total.

#### Implementación en Scala

````scala
def costoRiegoFincaPar(f : Finca, pi : ProgRiego) : Int = {
    // Devuelve el costo total de regar una finca f dada una
    // programacion de riego pi, calculando en paralelo
    def costoIntervalo(i: Int, j: Int): Int = {
      //calcula el costo total de rieggo para todos los tablones desde i hasta j
      if (i == j) //un solo tablon
        costoRiegoTablon(i, f, pi) //solo un tablon, costo directo
      // dos tablones
      else if (j - i == 1)
        costoRiegoTablon(i, f, pi) + costoRiegoTablon(j, f, pi) //costo directo de ambos
      // caso recursivo dividir intervalo en mitades y ejecutar en paralelo
      else {
        val div = (i + j) / 2
        val (c1, c2) = parallel(costoIntervalo(i, div), costoIntervalo(div + 1, j)) //ejecuta mitades en paralelo
        //suam de costos parciales
        c1 + c2
      }
    }
    //si la finca esta vacia no hay costo
    if (f.isEmpty) 0
    else costoIntervalo(0, f.length - 1)
  }
````

#### Explicación paso a paso

La función utiliza un patrón clásico de divide y vencerás, adecuado para paralelización:
Entradas

* `f`: finca (vector de tuplas)
* `pi`: programación de riego (permutación)
* Función interna: ``costoIntervalo()`` calcula el costo total de riego para todos los tablones entre i y j
* Caso base 1: un solo tablón. El costo es directo
* Caso base 2: dos tablones. Este caso 
* Caso recursivo: Se divide el intervalo en dos mitades, cada mitad se procesa en paralelo usando `common.parallel()`.
Finalmente se suman ambos resultados parciales, obteniendo el costo total.
* Caso general: si la finca esta vacía, el costo es 0, si no, se procesa el intervalo completo.


#### Ejemplo de uso

```scala
val finca : Finca = Vector(
    (10, 3, 4),
    (5, 3, 3),
    (2, 2, 1),
    (8, 1, 1),
    (6, 4, 2))
val progRiego : ProgRiego = Vector(0,1,4,2,3)
costoRiegoFincaPar(finca, progRiego)
```

* La función interna `costoIntervalo(i,j)` es la encargada de la recursividad y calcula el costo total
para los tablones. 

* Casos base: Si el subintervalo tiene 1 o 2 elementos, calcula el costo directo llamando a `costoRiegoTablon()`
lo que evita llamadas recursivas innecesarias y evita sobrecargas por demasiados tasks.

* Caso recursivo: Si tiene más elementos, como este ejemplo, divide en dos mitades y ejecuta ambas mitades en paralelo con `parallel(...)`

* En `costoIntervalo(i,j)` el indice inicial y final son 0 y 4 respectivamente.

* Llamada `costoIntervalo(0,4)`, calcula `j - i = 4 > 1` caso recursivo

  Divide: ``div = (0 + 4) / 2 = 2`` y entonces se ejecuta en paralelo:
  ``c1 = costoIntervalo(0,2)`` y ``c2 = costoIntervalo(3,4)``.

* Lamada lado izquierdo c1 `costoIntervalo(0,2)` igual que el anterior,
se genera `costoIntervalo(0,1)` y `costoIntervalo(2,2)`

* Llamada `costoIntervalo(0,1)` aqui `j - i == 1` caso base de dos tablones

  Calcula ``costoRiegoTablon(0, finca, pi) + costoRiegoTablon(1, finca, pi)``, cada una internamente.  
  llamada a ``tIR(finca, pi)`` y devuelve `Vector(0,3,10,12,6)`.
  Usa ``t = tIR(k)`` y`` ts, tr, p`` para aplicar la fórmula.
  Entonces: ``costoRiegoTablon(0) = 7`` y ``costoRiegoTablon(1) = 3`` se obtiene como resultado `7 + 3 = 10`

* Llamada ``costoIntervalo(2,2)`` aqui `j = i` caso base un único tablon: `costoRiegoTablon(2) = 10`
  Y suma con el resultado parcial obtenido anteriormente `10 + 10 = 20`. Por tanto:
  `costoIntervalo(0,2) = 20`

* Llamada lado derecho c2 `costoIntervalo(3,4)`, `j - i == 1` caso base de dos tablones.
  Calcula `costoRiegoTablon(3) = 5 + costoRiegoTablon(4) = 8`. Resultado: `5 + 8 = 13`

* Resultado final, la suma de los costos parciales. Por tanto `costoIntervalo(0,4) = 20 + 13 = 33`



#### Diagrama de llamados de pila

````mermaid
graph TD
  CostoTotal["costoIntervalo(0,4)"]
  CostoTotal --> L["costoIntervalo(0,2)"]
  CostoTotal --> R["costoIntervalo(3,4)"]

  L --> L1["costoIntervalo(0,1)"]
  L --> L2["costoIntervalo(2,2)"]

  L1 --> A0["costoRiegoTablon(0) = 7"]
  L1 --> A1["costoRiegoTablon(1) = 3"]

  L2 --> B2["costoRiegoTablon(2) = 10"]

  R --> R0["costoRiegoTablon(3) = 5"]
  R --> R1["costoRiegoTablon(4) = 8"]

  %% valores finales
  A0 --> SumA["7 + 3 = 10"]
  A1 --> SumA
  SumA --> ResL1["costoIntervalo(0,1) = 10"]
  B2 --> ResL2["costoIntervalo(2,2) = 10"]
  ResL1 --> SumL["10 + 10 = 20"]
  ResL2 --> SumL
  SumL --> ResL["costoIntervalo(0,2) = 20"]
  
  R0 --> SumR["5 + 8 = 13"]
  R1 --> SumR
  SumR --> ResR["costoIntervalo(3,4) = 13"]
  
  %% Total
  ResL --> Total["20 + 13 = 33"]
  ResR --> Total
  Total --> ResLR["costoIntervalo(0,4) = 33"]
````

---

### 3.1.2. Paralelización cálculo costos de movilidad

#### Implementación en Scala
````scala
def costoMovilidadPar(pi : ProgRiego , d : Distancia) : Int = {

  def calculoCostoMov(i : Int, j : Int) : Int = {
     if (j - i == 1) d(pi(i))(pi(j))
    
     else {
       val posicionCorte = (j - i) / 2 + i
       val (r1, r2) = parallel(calculoCostoMov(i, posicionCorte), calculoCostoMov(posicionCorte, j))
       r1 + r2
     }
   }
    calculoCostoMov(0, pi.length-1)
  }
````
#### Definición
Con esta versión del cálculo para el costo de movilidad se busca aplicar el concepto de paralelización
a lo anteriormente implementado en el punto [`2.4. Calculando costos`](#24-calculando-costos) de este mismo apartado
donde se aplica paralelización de tareas para ir ubicando los índices que se usaran como posiciones de fila y columna.
- ``pi`` es el vector de la programación de riego usada para conocer los índices en la matriz de distancias `d`.
- ``d`` es la matriz que contienen todos los costos asociados de moverse entre un tablón y otro.
- se retorna la sumatoria de los valores ubicados en la matriz.


#### Explicación paso a paso

#### Ejemplo de uso
````scala
val prog : ProgRiego = Vector(0,1,4,2,3)
val matrizMovi : Distancia = Vector(Vector(0,2,2,4,4), Vector(2,0,4,2,6), Vector(2,4,0,2,2), Vector(4,2,2,0,4), Vector(4,6,2,4,0))
costoMovilidadPar(prog, matrizMovi)
````
- Para el cálculo se emplea la función recursiva auxiliar ``calculoCostoMov``, donde se envía el índice inicial y el final de
los elementos contenidos en la programación de riego ``pi`` que para este caso sería 0 y 4.
  - primer nivel llamada recursiva: el paso entre i (índice inicial) y j (índice final) no es igual a 1, se pasa al caso recursivo;
    en este se busca el punto de corte para dividir en dos tareas la búsqueda de los índices. empleando ``(j - i) / 2 + i`` (`posicionCorte = 2`)
    se divide el cálculo de costos en dos caminos empleando el método ``parallel(...)`` y los resultados de estas tareas se 
    almacenan en ``r1`` y `r2`, y cuya suma es la que se retorna.
  - segundo nivel llamadas recursivas: siguiendo la lógica explicada anteriormente se tiene:
    - 2-1 ``calculoCostoMov(0,2)`` lo cual vuelve y llama al caso recursivo y se genera un tercer nivel con `posicionCorte = 1`.
    - 2-2 ``calculoCostoMov(2,4)`` lo cual vuelve y llama al caso recursivo y se genera un tercer nivel con `posicionCorte = 3`.
  - tercer nivel de llamadas recursivas:
    - 3-1 ``calculoCostoMov(0,1)`` cumple con el caso base por lo cual los índices se evalúan en `pi` dando así el valor de la fila = 0 y la columna = 1
    para ubicar el costo de mover desde el tablón 0 hasta el tablón 1. Por lo tanto, retorna 2.
    - 3-2 ``calculoCostoMov(1,2)`` cumple con el caso base por lo cual los índices se evalúan en `pi` dando así el valor de la fila = 1 y la columna = 4
      para ubicar el costo de mover desde el tablón 1 hasta el tablón 4. Por lo tanto, retorna 6.
    - 3-3 ``calculoCostoMov(2,3)`` cumple con el caso base por lo cual los índices se evalúan en `pi` dando así el valor de la fila = 4 y la columna = 2
    para ubicar el costo de mover desde el tablón 4 hasta el tablón 2. Por lo tanto, retorna 2.
    - 3-4 ``calculoCostoMov(3,4)`` cumple con el caso base por lo cual los índices se evalúan en `pi` dando así el valor de la fila = 2 y la columna = 4
      para ubicar el costo de mover desde el tablón 2 hasta el tablón 3. Por lo tanto, retorna 2.
- Al contraerse el proceso el resultado de los hilos se va sumando, 3-3 y 3-4 devuelven 4 que será el restultado retornado por 2-2; 
el resultado de 3-1 y 3-2 es 8 siendo este el resultado de 2-1. Finalmente, se tiene como resultado en el primer nivel la sum de 2-1 y 2-2
lo cual es 12.
- por lo tanto, se devuelve 12 en la llamada de ``costoMovilidadPar(prog, matrizMovi)``


#### Diagrama de llamados de pila

````mermaid
flowchart TD

    A["Llamada inicial<br/>`calculoCostoMov(0,4)`"] --> A1

    A1["Caso recursivo<br/>`posicionCorte = (4 - 0)/2 + 0 = 2`"] -->|parallel| B1
    A1 -->|parallel| B2

    subgraph Nivel2["Segundo nivel recursivo"]
        B1["2-1<br/>`calculoCostoMov(0,2)`<br/>Caso recursivo<br/>`posicionCorte = 1`"]
        B2["2-2<br/>`calculoCostoMov(2,4)`<br/>Caso recursivo<br/>`posicionCorte = 3`"] 
    end

    subgraph Nivel3["Tercer nivel (casos base)"]
      B1 -->|parallel| C1
      B1 -->|parallel| C2
        C1["3-1<br/>`calculoCostoMov(0,1)`<br/>pi(0)=0, pi(1)=1<br/>costo = 2"] --> D1
        C2["3-2<br/>`calculoCostoMov(1,2)`<br/>pi(1)=1, pi(2)=4<br/>costo = 6"] --> D1

      B2 -->|parallel| C4
      B2 -->|parallel| C3
        C3["3-3<br/>`calculoCostoMov(2,3)`<br/>pi(2)=4, pi(3)=2<br/>costo = 2"] --> D2
        C4["3-4<br/>`calculoCostoMov(3,4)`<br/>pi(3)=2, pi(4)=4<br/>costo = 2"] --> D2
    end

    D1["Resultado 2-1<br/>`2 + 6 = 8`"] --> E
    D2["Resultado 2-2<br/>`2 + 2 = 4`"] --> E
    
    E["Resultado nivel 1<br/>`8 + 4 = 12`"] --> F["Retorno final<br/>`costoMovilidadPar = 12`"]
````

---

## 3.2. Paralelizando la generación de programaciones de riego

### Definición

La función `generarProgramacionesRiegoPar` tiene como objetivo construir todas las posibles programaciones de riego
para una finca `F`. Dado que cada programación es una permutación de los índices de los tablones, el número total de programaciones es `n!`,
donde `n` es la cantidad de tablones.

La versión paralela divide el vector de factores en mitades, calcula recursivamente los productos de cada mitad en paralelo
(usando parallel del paquete common) y los combina con un for (concatenación).

### Implementación en Scala

````scala
def generarProgramacionesRiegoPar(f : Finca) : Vector[ProgRiego] = {
    // Genera las programaciones posibles de manera paralela
    val rangoFincas : Vector[Int] = f.indices.toVector
    val matrizBase : Vector[Vector[Int]] = rangoFincas.map(_ => rangoFincas)
  
    def productoCartesiano(vs: Vector[Vector[Int]]): Vector[Vector[Int]] = {
      if (vs.length == 1)
        vs.head.map(Vector(_))
      else {
        val (izq, der) = vs.splitAt(vs.length / 2)
        val (ci, cd) = parallel(productoCartesiano(izq),productoCartesiano(der))
        for {
          a <- ci
          b <- cd
        } yield a ++ b
      }
    }
    productoCartesiano(matrizBase).filter(x => x.distinct.length == f.length)
  }
````

### Explicación paso a paso

#### Construcción de los datos base

```scala
val rangoFincas : Vector[Int] = f.indices.toVector
val matrizBase : Vector[Vector[Int]] = rangoFincas.map(_ => rangoFincas)
```
`rangoFincas` contiene los índices: $[0,1,2,...,n-1]$
`matrizBase` replica ese vector $n$ veces. Esta matriz es el punto de partida para generar todas las combinaciones posibles.


#### Ejemplo de uso

````scala
val tabFinca : Finca = fincaAlAzar(3)
val progGenerada : Vector[ProgRiego] = generarProgramacionesRiegoPar(tabFinca)
````
* `rangoFinca` para este caso obtiene `Vector(0,1,2)`.

* `matrizBase` genera una matriz con el vector `rangoFinca`: `Vector(Vector(0, 1, 2), Vector(0, 1, 2), Vector(0, 1, 2))`.

* `productoCartesiano` función cuyo objetivo es combinar todos los elementos de todas las listas, formando todas
las combinaciones posibles.

  1. Caso base: `vs.length == 1` valida si solo queda un vector: `Vector(0, 1, 2)` se transforma en `Vector(Vector(0), Vector(1), Vector(2))`

  2. Caso recursivo: para mas vectores, como este ejemplo: se divide la lista en dos mitades `izq: Vector(Vector(0,1,2))` y `der = Vector(Vector(0,1,2), Vector(0,1,2))`. 

  3. Calcula `ci = productoCartesiano(izq)` y `cd = productoCartesiano(der)` en paralelo. Donde del lado izquierdo se obtiene 
  `Vector(Vector(0),Vector(1),Vector(2))` y del lado derecho `Vector(Vector(0,1,2),Vector(0,1,2))`

  4. El lado derecho nuevamente hace la rescursión, se divide en `izq: Vector(Vector(0,1,2))` y `der = Vector(Vector(0,1,2))`
  llegando ambos al caso base.
  
  5. `productoCartesiano()` transforma ambos lados en`Vector(Vector(0), Vector(1), Vector(2))`. 
  Cada mitad se procesa en paralelo usando `parallel` del paquete common.
  El resultado final es el producto cartesiano de los dos subconjuntos.
  
  6. Se realiza la primera combinacion: 

  `Vector(
  Vector(0,0), Vector(0,1), Vector(0,2),
  Vector(1,0), Vector(1,1), Vector(1,2),
  Vector(2,0), Vector(2,1), Vector(2,2))`

  7. Segunda combinación: 

  `Vector(
  Vector(0,0,0), Vector(0,0,1), Vector(0,0,2),
  Vector(0,1,0), Vector(0,1,1), Vector(0,1,2),
  Vector(0,2,0), Vector(0,2,1), Vector(0,2,2),
  Vector(1,0,0), Vector(1,0,1), Vector(1,0,2),
  Vector(1,1,0), Vector(1,1,1), Vector(1,1,2),
  Vector(1,2,0), Vector(1,2,1), Vector(1,2,2),
  Vector(2,0,0), Vector(2,0,1), Vector(2,0,2),
  Vector(2,1,0), Vector(2,1,1), Vector(2,1,2),
  Vector(2,2,0), Vector(2,2,1), Vector(2,2,2))`

   8. Como se generan $n^n$ combinaciones, es decir 27 vectores de longitud `f.length = 3`, incluidas las que se repiten tablones, 
   por eso se filtra usando `.filter(x => x.distinct.length == f.length)` y solo quedan los vectores que contienen todos los tablones sin repetir.
   `Vector(Vector(0,1,2),Vector(0,2,1),Vector(1,0,2),Vector(1,2,0),Vector(2,0,1),Vector(2,1,0))` 6 vectores con 3 elementos distintos.


Vector(Vector(0, 1, 2), Vector(0, 1, 2), Vector(0, 1, 2))`.

### Diagrama de llamados de pila

````mermaid
graph TD
  Root["productoCartesiano(Vector(Vector(0,1,2),Vector(0,1,2),Vector(0,1,2)))"]
  
  Root --> IZQ["productoCartesiano(Vector(0,1,2))"]
  Root --> DER["productoCartesiano(Vector(0,1,2), Vector(0,1,2))"]

  DER --> DER1["productoCartesiano(Vector(0,1,2))"]
  DER --> DER2["productoCartesiano(Vector(0,1,2))"]

  IZQ --> BaseIZQ["[0], [1], [2]"]
  DER1 --> BaseDER1["[0], [1], [2]"]
  DER2 --> BaseDER2["[0], [1], [2]"]

  BaseDER1 --> CombDER["combina → 9 resultados (3^2)"]
  BaseDER2 --> CombDER

  CombDER --> CombROOT["combina con IZQ → 27 tuplas (3^3)"]
  BaseIZQ --> CombROOT

  CombROOT --> Filter["filtrar distinct(length == 3) → 6 permutaciones"]

````

---

## 3.3. Paralelizando la programación de riego óptima

### Implementación en Scala

### Definición

### Explicación paso a paso

#### Ejemplo de uso

### Diagrama de llamados de pila