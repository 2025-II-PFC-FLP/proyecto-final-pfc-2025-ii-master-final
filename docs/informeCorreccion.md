# Informe de corrección proyecto final - El problema del riego óptimo

- [`2.3. Calculando el tiempo de inicio de riego`](#23-calculando-el-tiempo-de-inicio-de-riego)
- [`2.4. Calculando costos`](#24-calculando-costos)
- [`2.5. Generando programaciones de riego`](#25-generando-programaciones-de-riego)
- [`2.6. Calculando una programación de riego óptima`](#26-calculando-una-programación-de-riego-óptima)
- [`3.1. Paralelizando el cálculo de los costos de riego y de movilidad`](#31-paralelizando-el-cálculo-de-los-costos-de-riego-y-de-movilidad)
- [`3.2. Paralelizando la generación de programaciones de riego`](#32-paralelizando-la-generación-de-programaciones-de-riego)
- [`3.3. Paralelizando la programación de riego óptima`](#33-paralelizando-la-programación-de-riego-óptima)



## 2.3. Calculando el tiempo de inicio de riego

```scala
  def tIR(f: Finca, pi: ProgRiego): TiempoInicioRiego = {
    val n = pi.length
    val (tInit, _) =
      pi.foldLeft((Vector.fill(n)(0), 0)) { case ((t, tiempoActual), tablon) =>
        val tNuevo = t.updated(tablon, tiempoActual)
        val nuevoTiempoActual = tiempoActual + treg(f, tablon)
        (tNuevo, nuevoTiempoActual)
      }
    tInit
  }
```

Sea una finca `F` con `n` tablones, como se puede observar:

$$
tr(i) \quad \text{para } i = 0,1,\dots,n-1
$$

Y donde se propone un orden de riego tal que asi:

$$
\pi = (\pi_0, \pi_1, \dots, \pi_{n-1})
$$

Se busca satisfacer la necesidad de calcular el tiempo en el que se inicia
al regar un vector mediante la función `def tIR(f: Finca, pi: ProgRiego): TiempoInicioRiego`,
donde el vector se establece como: `T = (t0, t1, t2..., t[n-1])`

Con esto en cuenta, tenemos el bloque inicial del codigo:

```scala
val (tInit, _) =
  pi.foldLeft((Vector.fill(n)(0), 0)) { case ((t, tiempoActual), tablon) =>
    val tNuevo = t.updated(tablon, tiempoActual)
    val nuevoTiempoActual = tiempoActual + treg(f, tablon)
    (tNuevo, nuevoTiempoActual)
  }
```

Donde `tiempoActual` busca satisfacer y llevar el tiempo acumulado por cada
iteración de los tablones, en otras palabras:

$$
\text{tiempoActual}_{\text{nuevo}} = \text{tiempoActual} + tr(\pi_j)
$$

Por ende, al final, el vector resultante esta dado como `T[i]` donde `i` es la
posición inicial de riego del programa, o en este caso, donde aparece la programación pi.

$$
T_{\pi_0} = 0
$$

$$
T_{\pi_j} = \sum_{m=0}^{j-1} tr(\pi_m)
\quad \text{para } j = 1, 2, \dots, n-1
$$

Esto se puede verificar aun más si decidimos ir calculando los tiempos uno por uno como se
hara a continuación:

## 1er calculo:

$$
T_{\pi_0} = T_0 = 0
$$

$$
\text{tiempoActual} = 0 + tr(0) = 3
$$

## 2do calculo:

$$
T_{\pi_1} = T_1 = 3
$$

$$
\text{tiempoActual} = 3 + tr(1) = 6
$$

## 3er calculo:

$$
T_{\pi_2} = T_4 = 6
$$

$$
\text{tiempoActual} = 6 + tr(4) = 10
$$

## 4to calculo:

$$
T_{\pi_3} = T_2 = 10
$$

$$
\text{tiempoActual} = 10 + tr(2) = 12
$$

## 5to calculo:

$$
T_{\pi_4} = T_3 = 12
$$

$$
\text{tiempoActual} = 12 + tr(3) = 13
$$

Y que finalmente nos daria el vector resultado, `T = (0, 3, 10, 12, 6)` como
prueba del correcto funcionamiento del codigo.

## 2.4. Calculando costos

```scala
  def costoRiegoTablon(i: Int, f: Finca, pi: ProgRiego): Int = {
    val tiempos = tIR(f, pi)
    val t = tiempos(i)
    val ts = tsup(f, i)
    val tr = treg(f, i)
    val p = prio(f, i)

    if (ts - tr >= t)
      ts - (t + tr)
    else
      p * ((t + tr) - ts)
  }

  def costoRiegoFinca(f: Finca, pi: ProgRiego): Int =
    (0 until f.length).foldLeft(0)((acum, i) =>
      acum + costoRiegoTablon(i, f, pi)
    )

  def costoMovilidad(f: Finca, pi: ProgRiego, d: Distancia): Int = {
    pi.sliding(2).foldLeft(0) { (acum, par) =>
      acum + d(par.head)(par.last)
    }
  }
```

Para la función `costoRiegoTablon` se busca satisfacer los siguientes parametros segun un tablon `i`

```scala
t = tiempo de inicio del tablon

tr = tiempo de riego

ts = tiempo de suministro (deadline)

p = prioridad del tablon (penalización por atraso)
```

Ademas, el riego termina en la expresión: (Teniendo en cuenta la condición `if (ts - tr >= t)`)

$$
t_{\text{fin}}(i) = t(i) + tr(i)
$$

Expresión que igualmente corresponde a:

$$
t(i) \le ts(i) - tr(i)
$$

Cuando esta condicion `if` especificada en el codigo se cumple, entonces
significa que el riego ha sido a tiempo:

$$
C_i = ts(i) - t_{\text{fin}}(i)
$$

De lo contrario, si este caso **NO** se llega a cumplir, significa que el riego
ha llegado tarde:

$$
C_i = p(i)\,\bigl(t_{\text{fin}}(i) - ts(i)\bigr)
$$

Que al final coincide con los parametros dados, simplemente apropiados como una condición
dentro del codigo.

Continuando con `costoRiegoFinca` tenemos un caso similar ya que su sustentación se trata
de una apropiación a un formato en forma de codigo proviniendo originalmente de una formula
matematica:

$$
C_{\text{riego}}(f, \pi) = \sum_{i=0}^{n-1} C_i
$$

Donde se recorren todos los `i` (osea, pi) y se acumulan como `costoRiegoTablon (i, f, pi)`

Finalmente en el apartado de `costoMovilidad` tenemos la formula:

$$
C_{\text{mov}}(f, \pi) = \sum_{k=0}^{n-2} d(\pi[k],\, \pi[k+1])
$$

Que en resumidas cuentas cumple con lo que se busca codificar teniendo el cuenta el codigo;
`pi.sliding(2)` se encarga de obtener las parejas, `d(a)(b)` siendo el equivalente a
`d(pi[k], pi[k-1])` de la formula con el foldLeft siendo el encargado de la acumulación para
coincidir con la formula.

## 2.5. Generando programaciones de riego

````scala
def generarProgramacionesRiego(f : Finca) : Vector[ProgRiego] =
{
  val rangoFinca : Vector[Int] = f.indices.toVector
  val matrizBase : Vector[Vector[Int]] = rangoFinca.map(_ => rangoFinca)
  val productoCartesiano : Vector[Vector[Int]] = matrizBase.foldLeft(Vector(Vector.empty[Int])){(acc, vector) =>
    for{
    ...
    }
  }
  ...
}

````
Llámese PR a la función que genera una matriz con las permutaciones de un vector basado en la cantidad de elementos
que posea el vector `f` siendo este el argumento con el que se trabaja y que es de tipo `Finca`.

$$f = \{t_1, t_2, t_3, ..., t_{n-1}\}$$

$$PR: Vector_{|f|} \Longrightarrow Matriz_{[cntper,|f|]}$$

Demostrar que para todo $\forall f \in Finca, \text{generarProgramacionesRiego}(f)$ devuelve una matriz cuyos elementos
son permutaciones de un $Vector_{|f|}$ cuyo tamaño depende de la cantidad de índices producidos por los elementos contenidos
en `f`. Este proceso se compone de cuatro partes:

- en primera instancia se obtiene el vector con base en los elementos contenidos en ``f``.
    ````scala
    val rangoFinca : Vector[Int] = f.indices.toVector
    ````
    sea $R$ una función que crea un rango desde `0` hasta `n-1` a partir de `n`, y $V$ una función que al pasarle una colección de elementos
   que sean iterables genere un vector con dichos elementos.


$n = |f| \cap \forall n \in \mathbb{N} || R(n) = 0 \to (n-1)$que equivale a f.indices.
 
    
$V(R(n)) = \{0,1,2,3,...,n-1\}$

- seguido, se tiene una función $M$ que genera una matriz cuadrada a partir de un vector.
    
    $M(V(R(n))) \Longrightarrow Matriz_{nn}$
  
- se ingresa dicha matriz a la función ``productoCartesiano`` que generará todas aquellas combinaciones generadas por esta
  y las almacenará en otra matriz con $n^n$ filas y $n$ columnas.
  
    $productoCartesiano(Matriz_{[nn]}) = (n \times n_1) \times n_2) \times n_3) \times ... \times n_n)  = Matriz_{n^n,n}$

- finalmente, el producto cartesiano obtenido se tratará, conservando solo aquellos elementos que se consideren como permutaciones
    del conjunto dado teniendo en cuenta que la cantidad de filas será el de las permutaciones acorde a $n!$
    $filtro(Matriz_{n^n,n}) = Matriz_{n!,n}$
    
## 2.6. Calculando una programación de riego óptima

### 1. Argumentación de corrección

La función ProgramacionRiegoOptimo recibe una finca , una matriz de distancias  y debe retornar la programación de riego  que minimiza el costo total:
$$
\Pi^\* = \arg\min_{\Pi} \left( CR_F^\Pi + CM_F^\Pi \right)
$$
Donde:

- es el costo total de riego, calculado como:

$$
CR_F^\Pi = \sum_{i=0}^{n-1} CR_F^\Pi[i]
$$

- es el costo de movilidad:
  $$
  CM_F^\Pi = \sum_{j=0}^{n-2} DF[\pi_j, \pi_{j+1}]
  $$
  La estrategia utilizada consiste en:

1. Generar todas las permutaciones de , mediante la función:

$$
generarProgramacionesRiego(F)
$$

El tiempo de inicio de riego

El costo de riego

El costo de movilidad


3. Finalmente, se selecciona la permutación con costo mínimo.



### Correctitud parcial (función devuelve un candidato válido)

Para toda permutación , la función cumple:

es una permutación válida porque proviene de generarProgramacionesRiego, que garantiza:

$$
\Pi \in S_n
$$
El cálculo de tiempos de inicio de riego sigue exactamente la definición:

$$
t^\Pi_{\pi_0} = 0, \quad
t^\Pi_{\pi_j} = t^\Pi_{\pi_{j-1}} + tr_{\pi_{j-1}}
$$

Esto garantiza que el algoritmo coincide con la definición formal dada en la sección 1.2.1 del enunciado.

- Los costos calculados usan las fórmulas exactas del documento del proyecto.


Por lo tanto, cada candidato calculado es correcto.

### Correctitud total (selección del mínimo)

Sea:
$$
C(\Pi) = CR_F^\Pi + CM_F^\Pi
$$

Si la función efectivamente revisa todas las permutaciones (por definición del punto 2.6), entonces:
$$
\forall \Pi \in S_n,\; \exists \text{ una evaluación } C(\Pi)
$$
```latex
\Pi^\* = \min_{\Pi\in S_n} C(\Pi)
```

La función selecciona exactamente el mínimo usando:

- Un fold,

- o minBy,

- o selección explícita en recursión.


Cualquier alternativa garantiza matemáticamente:
$$
C(\Pi^\*) \le C(\Pi),\;\forall \Pi\in S_n
$$
Por lo tanto, la función es correcta porque implementa una búsqueda exhaustiva sobre un dominio finito.


---

## 3.1. Paralelizando el cálculo de los costos de riego y de movilidad


### 3.1.1. Paralelización cálculo costos de riego

Sea $f = [t_0,t_Q,...,t_{n-1}]$ una finca, $c(i) = costoRiegotablon(i,f,pi)$

La función ``costoRiegoFincaPar`` calcula el costo total de riego de una finca, utilizando una 
programación de riego pi, sumando todos los costos individuales pero usando paralelización,
dividiendo el problema en rangos de tablones evaluados en paralelo. Implementada en Scala:

````scala
def costoRiegoFincaPar(f : Finca, pi : ProgRiego) : Int = {
    def costoIntervalo(i: Int, j: Int): Int = {
      ...
    }
    ...
  }
````
### Definición matemática
$$
C(f,pi) = \sum_{i = 0}^{n-1} {c(i)}
$$

### Especificación:

La función debe:
* Calcular el costo total del riego sumando el costo por cada tablón.
* Ser equivalente a la versión secuencial.
* Mantener el orden de evaluación en paralelo sin afectar el resultado.
* Resolver correctamente todos los casos borde.

### Demostración


### Caso base 1:

````scala
if (i == j)
  costoRiegoTablon(i, f, pi)
````
Corresponde a:
$$
\sum_{k = i}^{i} {c(k)}
$$

### Caso base 2:

````scala
if (j - i == 1)
  costoRiegoTablon(i, f, pi) + costoRiegoTablon(j, f, pi)
````
Corresponde a:
$$
\sum_{k = i}^{i+1} {c(k) = c(i) + c(i+1)}
$$

**El caso base es correcto por definicón**

### Caso recursivo

````scala
vs.length > 1
````
### Hipotesis inductiva

* ``costoIntervalo(i, div)`` calcula correctamente la suma entre i y div
$$
\sum_{k = i}^{div} {c(k)}
$$
* ``costoIntervalo(div+1, j)`` calcula correctamente la suma entre div+1 y j
$$
\sum_{k = div+1}^{j} {c(k)}
$$
* 
Entonces:

```scala
val div = (i + j) / 2
val (c1, c2) = parallel(costoIntervalo(i, div), costoIntervalo(div + 1, j))
c1 + c2
```
Matematicamente corresponde a:
$$
\sum_{k = i}^{div} {c(k)} + \sum_{k = div+1}^{j} {c(k)} = \sum_{k = i}^{j} {c(k)}
$$

**Por lo tanto, el paso recursivo mantiene la especificación por suma asociativa**

### Correctitud
La función es correcta porque:

* Recibe los mismos parámetros que la versión secuencial
* Descompone la suma sin alterar los valores
* Solo paraleliza la suma, no cambia los cálculos

### Terminación
la recursion siempre reduce el tamaño, lo que garantiza que siempre llegue al caso base.


### Conclusión:
La función `costoRiegoFincaPar(f,pi)` implementa correctamente la semántica especificada del costo total de riego,
aplicando paralelización sin alterar su significado matemático.

Queda demostrado que
$$
costoRiegoFincaPar(f,pi) = \sum_{i=0}^{n-1} {costoRiegoTablon(i,f,pi)}
$$

---

### 3.1.2. Paralelización cálculo costos de movilidad

````scala
  def costoMovilidadPar(pi : ProgRiego , d : Distancia) : Int = {
   def calculoCostoMov(i : Int, j : Int) : Int = {
    ...
   }
   ...
  }
````

Se tiene una función `CMP` que recibe dos argumentos, un vector `v` con las programaciones de riego y una matriz `m`  
con los costos asociados de moverse entre tablones usando recursión como retorno se tiene la sumatoria de los costos
de movilidad encontrados.

$$CMP: (v,m) \Longrightarrow \sum_{i = 0}^{|v|-2} M_{[v_i,v_{i+1}]}$$

### Caso base
````scala
if (j - i == 1) d(pi(i))(pi(j))
````

$$\forall i \in \mathbb(N),\forall i \in |v|; \\ M_{[v_i,v_{i+1}]}$$

### Caso inductivo
````scala
 else {
    val posicionCorte = (j - i) / 2 + i
    val (r1, r2) = parallel(calculoCostoMov(i, posicionCorte), calculoCostoMov(posicionCorte, j))
    r1 + r2
  }
````

Para este caso, en primera instancia se establece una posición de corte `c`.

$$c = \lfloor (j-i)/2 \rfloor + i$$

Luego se va ramificando en dos tareas que se sumaran luego; estas dos tareas a su vez se pueden dividir en otras dos,
y así sucesivamente, generando un árbol binario cuyas ramas serán los casos base encontrados, por lo tanto:

$$ \sum_{i = 0}^{c-1} M_{[v_i,v_{i+1}]} + \sum_{i = c}^{|v|-2} M_{[v_i,v_{i+1}]} = \sum_{i = 0}^{|v|-2} M_{[v_i,v_{i+1}]}$$

La implementación es adecuada, ya que permite generar tantos hilos como segmentos requeridos para encontrar
la sumatoria de los elementos ubicados en `M` mediante los pares (segmentos) dados en `v`.

---

## 3.2. Paralelizando la generación de programaciones de riego

Sea $f$ una finca con $n$ tablones, $R = \{0,1,2,...,n-1\}$ y $Perm(R)$ el conjunto de todas las
permutaciones de $R$ de tamaño $n!$

Sea $G$ la función $generarProgramacionesRiegoPar$ tiene como objetivo construir todas las posibles 
programaciones de riego para $f$. Dado que cada programación es una permutación de los índices $R$ 
de los tablones y el número total de programaciones es $n!$, donde $n$ es la cantidad de tablones.
Implementada en Scala:

```scala
def generarProgramacionesRiegoPar(f : Finca) : Vector[ProgRiego] = {
    ...
    def productoCartesiano(vs: Vector[Vector[Int]]): Vector[Vector[Int]] = {
      ...
    }
    ...
  }
```

### Definición matemática
$$
G : Finca \to Vector(ProgRiego)
$$
retorna 
$$
G(f) = Perm(R)
$$

### Especificación:

La función debe:
* Generar todas las permutaciones posibles de los índices de una finca $f$
* No repetir elementos.
* No perder elementos.
* Poroducir exactamente $n!$ resultados
* Utilizar paralelización sin alterar la lógica de la versión secuencial.

### Demostración

**Mostrar que la función cumple la especificación y termina en todos los casos.**

### Subproceso función interna `productoCartesiano`

Sea un vector de vectores:
$$
V = [A_1,A_2,...,A_k]
$$
Esta matriz es el punto de partida para generar todas las combinaciones posibles.

El producto cartesiano es: $A_1 \times A_2 \times ... \times A_k$ que genera todas las secuencias de
longitud $k$, donde el ememento $i-ésimo$ pertenece a $A_i$

En este caso: $A_i = R$, $\forall i$ ; por lo que el resultado tiene tamaño $n^n$, del cual posteriormente
se filtran las permutaciones.

### Caso base

````scala
if (vs.length == 1) 
  vs.head.map(Vector(_))
````
Esto corresponde a: 
$$
A_1 \to \{[a] \text{ si } a \in A_1\}
$$
Lo cual es exactamente el producto cartesiano de un solo conjunto.

Por lo tanto, se demuestra que:
* Cada elemento contiene un único elemento
* No hay repeticiones
* Cada elemento pertenece al dominio de indices
* No se omite ningún elemento

**Conclusión: El caso base esta bien implementado y cumple la especificación**

### Caso recursivo

````scala
vs.length > 1
````
Entonces:

```scala
val (izq, der) = vs.splitAt(vs.length / 2)
val (ci, cd) = parallel(productoCartesiano(izq),productoCartesiano(der))
for {
  a <- ci;
  b <- cd
} yield a ++ b
```
### Hipotesis inductiva

Suponer que:
* ``productoCartesiano(izq)`` devuelve el producto cartesiano correcto de los subconjuntos del lado izquierdo.
* ``productoCartesiano(der)`` devuelve el producto cartesiano correcto de los subconjuntos del lado derecho.

Este enfoque divide el problema en subproblemas independientes, ideales para paralelizar. Donde,
* `a` representa todas las combinaciones parciales de la primera mitad
* `b` representa todas las combinaciones parciales de la segunda mitad
* `a ++ b` concatena columnas correspondientes, preservando el orden
* No se repiten elementos dentro de un vector porque cada columna proviene de la matriz base
donde todas las columnas son iguales

Matemáticamente, corresponde a:
$$
(ci \times cd) = \{ a ++ b \text{ , si } a \in ci, b \ in cd \}
$$
Esto genera exactamente el producto cartesiano de los dos subconjuntos:
$$
(A×B×C×D)=(A×B)×(C×D)
$$
Por lo tanto, la combinación cumple exactamente la definición del producto cartesiano completo.

Posteriormente se filtra y elimina todas las combinaciones con repetición y deja solo permutacoines:

````scala
productoCartesiano(matrizBase).filter(x => x.distinct.length == f.length)
````
implementación equivalente a:
$$
Perm(R) = \{s \in R^n \text{ , si } \text{distinct}(s) = n \}
$$

**Por lo tanto, se muestra que el conjunto final contiene exactamente todas las permutaciones posibles.**

### Correctitud
La función es correcta porque:

* El producto cartesiano genera todas las combinaciones posibles
* El filtrado elimina las que no son permutaciones
* La paralelización no cambia el resultado, solo la velocidad

### Terminación
la recursion siempre reduce el tamaño, lo que garantiza que siempre llegue al caso base.


### Conclusión:
``generarProgramacionesRiegoPar`` es ùna implementación correcta de $Perm(R)$.

---

## 3.3. Paralelizando la programación de riego óptima


### Argumentación de Corrección
Se debe demostrar que la función `buscarMinimoPar(S)` retorna la programación $\pi \in S$ que minimiza la función de costo total.

**Definiciones:**
* Sea $C(\pi)$ el costo calculado por `costoRiegoFinca` + `costoMovilidad`.
* Sea $S$ el conjunto de todas las permutaciones posibles.

**Demostración por Inducción Estructural:**

1.  **Caso Base (`S.length <= umbral`):**
    El algoritmo aplica `S.map(pi => (pi, costo(pi))).minBy(_._2)`.
    * La función `minBy` recorre la colección completa y garantiza retornar el elemento con el valor mínimo según el criterio dado.
    * Dado que `costoRiegoFinca` y `costoMovilidad` son deterministas y correctos, el caso base es correcto.

2.  **Paso Inductivo (`S.length > umbral`):**
    El conjunto se divide en $S_{izq}$ y $S_{der}$.
    * **Hipótesis:** Asumimos que las llamadas recursivas `buscarMinimoPar(izq)` y `buscarMinimoPar(der)` retornan correctamente el mínimo local de sus respectivos subconjuntos.
    * Sea $min_{izq} = (\pi_i, c_i)$ el resultado de la rama izquierda.
    * Sea $min_{der} = (\pi_d, c_d)$ el resultado de la rama derecha.
    * El algoritmo realiza la comparación final: `if (c_i <= c_d) min_{izq} else min_{der}`.

    Matemáticamente:
    $$\min(S) \equiv \min(S_{izq} \cup S_{der}) \equiv \min(\min(S_{izq}), \min(S_{der}))$$

    Por lo tanto, al combinar los resultados parciales correctos, el resultado global es necesariamente el mínimo de todo el conjunto $S$.
### Casos de Prueba (Diseño)
Para validar la corrección, se proponen los siguientes 5 casos de prueba que deben incluirse en el conjunto de tests (`src/test/scala`):

1.  **Finca Vacía:**
    * *Entrada:* `Finca` vacía, `Distancia` vacía.
    * *Esperado:* `Vector()` o manejo de excepción controlado (según implementación, en este código devuelve `(Vector(), 0)`).

2.  **Finca Unitaria (1 Tablón):**
    * *Entrada:* `Finca` con 1 elemento.
    * *Esperado:* La única programación posible `Vector(0)` y su costo calculado manualmente.

3.  **Finca Pequeña (Bajo el umbral):**
    * *Entrada:* `Finca` de 3 tablones ($3! = 6$ permutaciones).
    * *Acción:* Ejecutar `ProgramacionRiegoOptimo` (secuencial) y `ProgramacionRiegoOptimoPar`.
    * *Esperado:* `ResultadoParalelo == ResultadoSecuencial`.

4.  **Finca Mediana (Sobre el umbral):**
    * *Entrada:* `Finca` de 10 tablones (lo suficientemente grande para generar > 200 permutaciones si se usa fuerza bruta, o ajustando el umbral a 1 para el test).
    * *Esperado:* El costo devuelto por la versión paralela debe ser idéntico al de la versión secuencial.

5.  **Costos Idénticos:**
    * *Entrada:* Una finca donde todos los tablones son idénticos y las distancias son 0.
    * *Esperado:* Cualquier permutación es válida, pero el costo numérico debe ser el mínimo posible (en este caso, igual para todas). La función no debe fallar ni ciclarse.