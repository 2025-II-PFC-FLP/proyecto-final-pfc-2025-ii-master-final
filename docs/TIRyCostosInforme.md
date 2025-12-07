# 1. Contextualización

Para indagar en los puntos realizados en esta parte del codigo,
debemos tener en cuenta las condiciones que se buscan cumplir. Iniciando
con el punto **2.3** tenemos el siguiente enunciado:

**"Implemente una funcion tIR que reciba de entrada una finca f y una programacion de riego
especıfica π, y devuelva el tiempo de inicio de riego de cada tablon de la finca f segun π:"**

Y se codifico la siguiente propuesta respectivamente:

## 1.1) Punto 2.3:

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

Inicialmente se contribuyo creando un valor orden que recorra los tablones
al iniciar el tiempo de riego de estos, pero, indagando más en cuanto a su programación se
decidio compactar el codigo en dos valores, uno para pi y otro que se encargue
de hacer el recorrido esperado:

```scala
    val (tInit, _) =
      pi.foldLeft((Vector.fill(n)(0), 0)) { case ((t, tiempoActual), tablon) =>
        val tNuevo = t.updated(tablon, tiempoActual)
        val nuevoTiempoActual = tiempoActual + treg(f, tablon)
        (tNuevo, nuevoTiempoActual)
      }
    tInit
  }
```

En este caso lo que se esta realizando en esta sección del codigo es
un recorrido de los tablones especificados mediante la utilización de un `foldLeft`
de inicio, tomando como referente el tiempo actual e ir actualizandolo
con el tiempo de riego que ha transcurrido, iniciando en 0 y visitando
cada tabla donde la siguiente empieza cuando la anterior finaliza, usando como
referencia la expresión otorgada en el documento base:

$$
t\Pi_{\pi_0} = 0,
$$

$$
t\Pi_{\pi_j} = t\Pi_{\pi_{j-1}} + trF_{\pi_{j-1}}, \quad j = 1, \ldots, n-1
$$

## 1.2) Punto 2.4:

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

Prosiguiendo con el punto 2.4, se nos propone codificar las siguientes operaciones:

### costoRiegoTablon:

$$
CR\Pi_F[i] =
\begin{cases}
tsF_i - (t\Pi_i + trF_i), & \text{si } tsF_i - trF_i \ge t\Pi_i, \\[6pt]
pF_i \cdot \big( (t\Pi_i + trF_i) - tsF_i \big), & \text{de lo contrario.}
\end{cases}
$$

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
```

En resumidas cuentas lo que esta primera función de `costoRiegoTablon` nos
quiere decir es proveer los tiempos de riego establecidos ademas
de una condición: `if (ts - tr >= t)`

Si esto se cumple, se ejecuta: `ts - (t + tr)`, lo que quiere decir
que el tiempo de riego no llego tarde, al contrario, este se rego a tiempo.

Si el caso anterior **NO** se cumple, se ejecuta: `p * ((t + tr) - ts)`, lo que
quiere decir de que el tiempo de riego ha sido tarde.

### costoRiegoFinca:

$$
CR\Pi_F = \sum_{i=0}^{n-1} CR\Pi_F[i]
$$

```scala
  def costoRiegoFinca(f: Finca, pi: ProgRiego): Int =
    (0 until f.length).foldLeft(0)((acum, i) =>
      acum + costoRiegoTablon(i, f, pi)
    )
```
Siguiendo con `costoRiegoFinca` tenemos un recorrido de los tablones
propuestos con la ayuda de un `foldLeft`, que mientras se van creando
los indices, el foldLeft acumulara el valor total de cada tablon que explicado
en terminos mas faciles de entender: "El costo total de riego de la finca es la suma 
de los costos de riego de cada tablon individual."

### costoMovilidad:

$$
CM\Pi_F = \sum_{j=0}^{\,n-2} DF[\pi_j, \pi_{j+1}]
$$

```scala
  def costoMovilidad(f: Finca, pi: ProgRiego, d: Distancia): Int = {
    pi.sliding(2).foldLeft(0) { (acum, par) =>
      acum + d(par.head)(par.last)
    }
  }
```

Por ultimo, `costoMovilidad` se encarga de calcular el costo exacto
a la hora de moverse a traves de los tablones segun el orden de riego implementado,
con la ayuda de un **sliding** que permitira hacer un recorrido secuencial del vector
(en este caso, nuestros tablones) para representar el orden de riego, nuevamente,
con la ayuda de un foldLeft que forma parejas cuya visualización para mayor entendimiento
seria tal que asi:

```scala
pi.sliding(2)  ⇒  Vector(Vector(3,0), Vector(0,2), Vector(2,1))
```

Estas parejas se toman como una distancia que luego se calculan teniendo
en cuenta el acumulador, representado tal que de la siguiente manera:

```scala
(acum, par) => acum + d(par.head)(par.last)
```

Y que en terminos sencillos para entender, se podria simplificar como:
"La función calcula la suma de todas las distancias entre 
tablones consecutivos en el orden de riego."
