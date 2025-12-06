# Informe de corrección proyecto final - El problema del riego óptimo

- [`2.3. Calculando el tiempo de inicio de riego`](#23-calculando-el-tiempo-de-inicio-de-riego)
- [`2.4. Calculando costos`](#24-calculando-costos)
- [`2.5. Generando programaciones de riego`](#25-generando-programaciones-de-riego)
- [`2.6. Calculando una programación de riego óptima`](#26-calculando-una-programación-de-riego-óptima)
- [`3.1. Paralelizando el cálculo de los costos de riego y de movilidad`](#31-paralelizando-el-cálculo-de-los-costos-de-riego-y-de-movilidad)
- [`3.2. Paralelizando la generación de programaciones de riego`](#32-paralelizando-la-generación-de-programaciones-de-riego)
- [`3.3. Paralelizando la programación de riego óptima`](#33-paralelizando-la-programación-de-riego-óptima)



## 2.3. Calculando el tiempo de inicio de riego





## 2.4. Calculando costos







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
  
    $productoCartesiano(Matriz_{[nn]}) = (n \otimes n_1) \otimes n_2) \otimes n_3) \otimes ... \otimes n_n)  = Matriz_{n^n,n}$

- finalmente, el producto cartesiano obtenido se tratará, conservando solo aquellos elementos que se consideren como permutaciones
    del conjunto dado teniendo en cuenta que la cantidad de filas será el de las permutaciones acorde a $n!$
    $filtro(Matriz_{n^n,n}) = Matriz_{n!,n}$
    
## 2.6. Calculando una programación de riego óptima





## 3.1. Paralelizando el cálculo de los costos de riego y de movilidad


### 3.1.1. paralelización cálculo costos de riego


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


## 3.2. Paralelizando la generación de programaciones de riego








## 3.3. Paralelizando la programación de riego óptima
