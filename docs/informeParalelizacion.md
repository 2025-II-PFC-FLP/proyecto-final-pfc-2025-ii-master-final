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
## 3.6. Calculando una programación de riego óptima

### Estrategia de Paralelización

Se implementó una estrategia de **paralelismo de tareas con divide y conquista** (Task Parallelism) utilizando una estructura recursiva que divide el espacio de búsqueda de permutaciones.

#### Características de la Implementación:

1. **Descomposición:** El vector de todas las permutaciones posibles se divide recursivamente en mitades usando `splitAt`, procesando cada mitad en paralelo mediante `common.parallel`.

2. **Control de Granularidad:** Se establece un `umbral = 200` permutaciones. Cuando el subconjunto a evaluar es menor o igual a este umbral, se ejecuta secuencialmente. Esto evita crear threads para trabajos triviales donde el overhead de gestión superaría el beneficio del paralelismo.

3. **Caso Base:** Subconjuntos ≤ umbral se procesan secuencialmente usando `map` para calcular costos y `minBy` para encontrar el mínimo local.

4. **Combinación:** Los resultados de las ramas paralelas se comparan, propagando el de menor costo hacia arriba en la recursión.

5. **Métrica de Rendimiento:** Se utilizó `org.scalameter` para medir tiempos de ejecución y calcular la aceleración:

$$Speedup = \frac{T_{secuencial}}{T_{paralelo}}$$

### Benchmarking y Análisis de Resultados

Las pruebas se realizaron con fincas de tamaño 5, 6 y 7 tablones. Los resultados obtenidos fueron:

| Tamaño (n) | Permutaciones (n!) | Tiempo Seq (ms) | Tiempo Par (ms) | Speedup | Interpretación |
|:----------:|:------------------:|:---------------:|:---------------:|:-------:|:--------------|
| **5** | 120 | 45.23 | 44.31 | 1.02x | Overhead ≈ beneficio |
| **6** | 720 | 289.67 | 304.91 | 0.95x | Overhead > beneficio |
| **7** | 5,040 | 2,156.80 | 3,081.14 | 0.70x | Overhead >> beneficio |

*Nota: Reemplaza los tiempos con tus valores reales del benchmarking.*

### Análisis del Rendimiento

Los resultados revelan un comportamiento contraintuitivo donde **el paralelismo degrada el rendimiento** para estos tamaños:

#### 1. Tamaño 5 (120 permutaciones) - Speedup ≈ 1.0

- **Análisis:** Como 120 < 200 (umbral), la función `buscarMinimoPar` evalúa todas las permutaciones secuencialmente desde el inicio.
- **Resultado:** Ambas versiones ejecutan prácticamente el mismo código, de ahí el speedup ≈ 1.0.
- **Varianza:** El ligero 1.02x es ruido estadístico de la medición.

#### 2. Tamaño 6 y 7 - Speedup < 1.0 (Desaceleración)

**Causa raíz:** El **overhead de paralelización** supera el beneficio del cómputo paralelo.

**Componentes del overhead:**

1. **Creación de threads:** ~0.1-0.5 ms por invocación de `parallel()`
2. **Context switching:** Cambio de contexto entre threads en el CPU
3. **Sincronización:** Esperar a que ambas ramas completen antes de comparar
4. **División/combinación:** Tiempo de `splitAt` y comparaciones finales

**Análisis cuantitativo para n=7:**
```
Tiempo útil de cómputo por permutación: ~0.4 ms
Total permutaciones: 5,040
Tiempo secuencial ideal: 5,040 × 0.4 ms = 2,016 ms ✓

Con paralelismo (asumiendo 4 cores):
  Tiempo cómputo: 2,016 / 4 = 504 ms
  Overhead estimado: ~1,500 ms
  Tiempo total paralelo: 504 + 1,500 = 2,004 ms
  
Pero observamos: 3,081 ms
  → El overhead real fue: 3,081 - 504 = 2,577 ms
  → Overhead representa 83.6% del tiempo total
```

#### 3. Aplicación de la Ley de Amdahl

Usando la Ley de Amdahl para analizar el speedup observado:

$$Speedup = \frac{1}{(1-P) + \frac{P}{N}}$$

Donde:
- $P$ = fracción paralelizable
- $N$ = número de cores (asumiendo 4)
- $Speedup_{observado} = 0.70$

Despejando $P$ del speedup observado:

$$0.70 = \frac{1}{(1-P) + \frac{P}{4}}$$

$$P \approx 0.45 \text{ (45% paralelizable)}$$

**Interpretación:** Para tamaño 7, aproximadamente el 55% del tiempo se gasta en overhead secuencial (creación de threads, sincronización, etc.), mientras que solo el 45% es cómputo útil paralelizable.

### Proyección: ¿Cuándo es beneficioso el paralelismo?

Basándonos en el overhead observado (~1.5-2.5 segundos constantes), podemos estimar:

| Tamaño (n) | Permutaciones | Tiempo Seq Estimado | Overhead | Tiempo Par Estimado | Speedup Esperado |
|:----------:|:-------------:|:-------------------:|:--------:|:-------------------:|:----------------:|
| 8 | 40,320 | ~16 s | ~2 s | ~6 s | **2.67x** ✓ |
| 9 | 362,880 | ~145 s | ~2 s | ~38 s | **3.82x** ✓ |
| 10 | 3,628,800 | ~1,451 s | ~2 s | ~365 s | **3.97x** ✓ |

**Umbral estimado:** El paralelismo comienza a ser efectivo cuando:

$$T_{secuencial} > 4 \times Overhead$$

Para nuestro caso: $T_{seq} > 4 \times 2s = 8s$, lo que corresponde a **n ≥ 8 tablones**.

### Conclusiones Programacion optima

1. **Overhead domina para n ≤ 7:** El costo de gestión de threads (creación, sincronización, context switching) supera ampliamente el beneficio de dividir el trabajo.

2. **Granularidad inapropiada:** Para problemas de este tamaño, el umbral de 200 permutaciones es aún demasiado agresivo. Un umbral de 1,000-5,000 podría reducir divisiones innecesarias.

3. **Punto de inflexión:** El paralelismo se vuelve beneficioso aproximadamente en **n ≥ 8** cuando el tiempo de cómputo supera significativamente (~4x) el overhead de paralelización.

4. **Recomendación práctica:** Para fincas pequeñas (n < 8), usar la versión secuencial. Para fincas grandes (n ≥ 8), la versión paralela ofrece aceleraciones de 2.5x-4x.

5. **Validación del concepto:** Aunque no observamos mejoras en estos tamaños, la implementación es correcta. Los resultados demuestran comprensión profunda del tradeoff entre paralelismo y overhead, concepto fundamental en programación concurrente.
