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

La paralelización se implementa mediante el método parallel(a, b) del paquete common.
````scala
def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
scheduler.value.parallel(taskA, taskB)
}
````
Este método crea dos tareas independientes, permitiendo que cada subexpresión se ejecute en paralelo cuando es posible.

## Benchmarking

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

Un valor mayor que 1 indica mejora y un valor menor que 1 indica pérdida de rendimiento.


## Calculando costos de riego


### Resultado


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



## Calculando una programación de riego óptima




### Resultado


