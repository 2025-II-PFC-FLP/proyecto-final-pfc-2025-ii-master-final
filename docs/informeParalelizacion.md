# Informe de paralelización proyecto final - El problema del riego óptimo

En este informe se describe el proceso de paralelización de las funciones descritas a continuación:

- [`Calculando costos de riego`](#calculando-costos-de-riego)
- [`Calculando costos de movilidad`](#calculando-costos-de-movilidad)
- [`Generando programaciones de riego`](#generando-programaciones-de-riego)
- [`Calculando una programación de riego óptima`](#calculando-una-programación-de-riego-óptima)


Todas se definen por alguna o varias de las TADs `Finca`, `Tablon`, `Distancia`, `TiempoInicioRiego` o `ProgRiego` de modo que
puedan ejecutar parte de su trabajo en paralelo utilizando la plantilla de paralelismo en common basada en 
ForkJoinPool y tareas recursivas. Además, se miden los tiempos de ejecución en 
Nexton.scala y NextonParalela.scala mediante ScalaMeter y se compara el rendimiento entre ambas versiones.

La paralelización se implementa mediante el método ``parallel(a, b)`` del paquete common.
````scala
def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
scheduler.value.parallel(taskA, taskB)
}
````
Este método crea dos tareas independientes, permitiendo que cada subexpresión se ejecute en paralelo cuando es posible.

---

# Benchmarking

El proceso de benchmarking tuvo como propósito evaluar el rendimiento de la paralelización de las funciones anteriores,
en el archivo `Benchmarking.scala`.
Se compararon los tiempos de ejecución de ambas versiones aplicando tres o cuatro expresiones de complejidad creciente.
Las mediciones se realizaron utilizando la librería ScalaMeter donde se registra el tiempo en milisegundos.

```scala
class Benchmarking {
  
  def benchmarkingGenerarProgramacionRiego(): Unit = { ... }
  def benchmarkingCostosRiego(): Unit = { ... }
  def benchmarkingCostosMovilidad(): Unit = { ... }
  def benchmarkingProgramacionOptima(): Unit = { ... }
```
La razón entre tiempos secuenciales y paralelos se reporta como aceleración, donde:

$$
\text{Aceleracion} = \frac{\text{T}_{\text{secuencial}}}{\text{T}_{\text{paralelo}}}
$$

Una aceleración mayor que 1 indica mejora y una aceleración menor que 1 indica pérdida de rendimiento.

---

## Calculando costos de riego

Las configuraciones de entrada fueron:
````scala
fincaPeque = fincaAlAzar(2)
fincaMed   = fincaAlAzar(6)
fincaGigante = fincaAlAzar(8)
````
Donde ``fincaAlAzar(n)`` genera una finca con $n$ tablones, y por tanto la programación debe tener $n$ valores:
````bash
Para n = 2: Vector(0,1)
Para n = 6: Vector(2,1,4,3,0,5)
Para n = 8: Vector(2,1,5,4,3,6,7,0)
````

### Resultado
| Finca            | Secuencial (ms) | Paralela (ms) | Aceleración |
|------------------|-----------------|---------------|-------------|
| 2 tablones + pi1 | 0.0905          | 0.0296        | 3.0574      |
| 6 tablones + pi2 | 0.2019          | 0.1474        | 1.3697      |
| 8 tablones + pi3 | 0.0456          | 0.0911        | 0.5005      |

Los tiempos obtenidos muestran que el rendimiento de la versión paralela depende del tamaño del problema, este comportamiento 
es consistente con la Ley de Amdahl que establece que la aceleración máxima de un algoritmo paralelo está limitada por la 
fracción de la tarea que no puede paralelizarse. En el primer caso pi1 se obtuvo una aceleración de aproximadamente 3.0x, 
lo cual indica que para pi pequeñas, la descomposición y el paralelismo resulta ser es muy eficiente. En el caso de pi2, 
la acelaración disminuye a 1.37x aunque sigue siendo buena, muestra que la implementación de tareas paralelas empieza a 
reducir el beneficio. En el tercer caso pi3, se presenta el peor rendimiento, aunque la finca es más grande, el coste de 
cada operación individual es tan pequeño que el trabajo que realmente se paraleliza es poco haciendo que el overhead del 
ForkJoinPool, supere el beneficio frente la versión secuencial que termina ejecutándose más rápido.


### Conclusión
Esto demuestra que la función paralela es correcta pero es eficiente cuando el trabajo por tarea es suficientemente 
grande para compensar el overhead, de lo contrario, el paralelismo resulta ineficiente.

---

## Calculando costos de movilidad

Realiza una sumatoria de los costos sobre la movilización entre tablones según la programación de riego dada.
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
Para esta versión que paraleliza el proceso mencionado, se aplica recursión y en cada paso se generan dos nodos, que al llegar
al paso base se generan los valores para cada movilización, lo cuales serán parte de la sumatoría a entregar.


### Resultado

| Expresión | Secuencial (ms) | Paralela (ms) | Aceleración |
|-------|-----------------|---------------|-------------|
| e1    | 0.0781          | 0.071         | 1.1         |
| e2    | 0.0569          | 0.077         | 0.73896     |
| e3    | 0.0558          | 0.065         | 0.8584      |
| e4    | 0.0531          | 0.0784        | 0.677295    |

Para este caso se realizaron 4 comparativos, e1 tiene una programación de riego con 5 elementos y, por lo tanto, una matriz de 5x5,
e2 maneja 6 elementos, e3 7 y e4 8. Si bien e1 muestra una buena aceleración el resto de las pruebas no, ya que por, un lado afecta
que el árbol no esté balanceado, y por el otro que se genera ``overhead`` es decir, que se gasta más tiempo en la gestión de los hilos
que en la solución de los problemas, la causa de esto es haber escogido un enfoque de paralelización de tareas.


---

## Generando programaciones de riego
Genera las permutaciones posibles en una programación de riego a partir de la cantidad de `tablones` presentes en una `finca`.
````scala
def generarProgramacionesRiegoPar(f : Finca) : Vector[ProgRiego] = {
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
Para esta versión se aplica una recursión que genera un árbol binario aplicando el principio de, divide y vencerás.


### Resultado

| Expresión | Secuencial (ms) | Paralela (ms) | Aceleración |
|----|-----------------|---------------|-------------|
| e1 | 0.0531          | 0.0923        | 0.57529     |
| e2 | 5.8645          | 6.129         | 0.9568      |
| e3 | 2785.971        | 3569.5492     | 0.7804      |

Para este caso se aplicó paralelización de tareas lo cual es evidenciable en el rendimiento de los casos escogidos,
puesto que se tienen falencias notorias en el rendimiento a causa del ``overhead``, y esto es en gran medida a que se ha granulado
mucho el ejercicio y que son tareas muy cortas de ejecutar en contraste de lo que puede llegar a tener la gestión del mismo hilo.

---

## Calculando una programación de riego óptima


Este informe incluye los datos reales que obtuviste y el análisis correspondiente.

### Estrategia de Paralelización
Se implementó una estrategia de **paralelismo de tareas** sobre la colección de datos (Data Parallelism) utilizando una estructura recursiva de *Divide y Vencerás*.

1.  **Descomposición:** El vector de permutaciones se divide recursivamente.
2.  **Control de Granularidad:** Se usa un `umbral = 200`. Esto evita crear hilos para trabajos triviales donde el costo de gestión del hilo superaría el tiempo de cómputo útil.
3.  **Métrica de Rendimiento:** Se utilizó `scalameter` para medir el tiempo de ejecución y calcular la aceleración ($S = T_{sec} / T_{par}$).

### Benchmarking y Análisis de Resultados
Las pruebas se realizaron con fincas de tamaño 5, 6 y 7. Los resultados de aceleración obtenidos son:

| Tamaño de la finca (Tablones) | Total Permutaciones ($N!$) | Speedup (Aceleración) |
| :--- | :--- | :--- |
| **5** | 120 | 1.02 |
| **6** | 720 | 0.95 |
| **7** | 5,040 | 0.70 |

### Análisis del Rendimiento
Los resultados muestran un comportamiento particular:

1.  **Tamaño 5 (120 permutaciones):** La aceleración es $\approx 1.0$. Esto es esperado porque 120 es menor que el `umbral` (200), por lo que el algoritmo paralelo decide ejecutarse secuencialmente, comportándose igual que la versión original (el ligero 1.02 es varianza estadística).

2.  **Tamaño 6 y 7 (Speedup < 1):** Se observa una desaceleración (0.95 y 0.70).
    * **Causa:** El costo computacional de calcular el riego para 6 o 7 tablones es muy bajo en términos absolutos (milisegundos). Al paralelizar, el **overhead** (sobrecarga) de crear las tareas paralelas, gestionar el contexto de ejecución y realizar el *context switching* en la CPU es mayor que el tiempo ahorrado por dividir el trabajo.
    * **Ley de Amdahl:** La fracción estrictamente secuencial (gestión de la recursión y combinación) domina sobre la fracción paralela para cargas de trabajo tan ligeras.

**Conclusión:** Para este problema específico, el paralelismo comienza a ser efectivo solo con cargas de trabajo masivas (Fincas > 8 o 9 tablones) donde el tiempo de cómputo supere significativamente al tiempo de gestión de hilos.

