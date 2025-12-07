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

Demostrando mas a fondo el correcto funcionamiento y paso a paso de este codigo,
tomaremos un ejemplo concreto como guia: (Alternativamente, se puede considerar
como una simulación de la pila)

```scala
finca =
tablon:  ts  tr  p
0 → (10, 3, 4)
1 → (5, 3, 3)
2 → (2, 2, 1)
3 → (8, 1, 1)
4 → (6, 4, 2)

ProgRiego pi = Vector(0, 1, 4, 2, 3)
```

Este bloque  de codigo con los tablones se puede resumir tal que asi, 
`tr(0)=3, tr(1)=3, tr(2)=2, tr(3)=1, tr(4)=4` donde el estado inicial del `foldLeft` es
simplemente valores iniciales de 0, `(t = Vector(0,0,0,0,0), tiempoActual = 0)`

Iniciando con el tablon 0 podemos ver que `tiempoActual` ira actualizandose continuamente,
por ejemplo:

```scala
tNuevo = t.updated(0, 0) → (0,0,0,0,0)
nuevoTiempoActual = 0 + tr(0)=3 → 3
//La salida del primer llamado siendo:
(tNuevo = (0,0,0,0,0), tiempoActual = 3)
```

Despues de seguir con el segundo tablon, la llamada se actualiza como ha de esperarse:

```scala
tNuevo = updated(1, 3) → (0,3,0,0,0)
nuevoTiempoActual = 3 + tr(1)=3 → 6
//La salida del segundo llamado siendo:
(t = (0,3,0,0,0), tiempoActual = 6)
```

Este proceso (como se puede intuir) se ira repitiendo hasta que cada uno de los tablones
han sido recorridos en su totalidad, por lo que, para formalizar y reducir la cantidad de llamados
individuales de estos recorridos podemos resumir el proceso de una manera más compacto
como si fueran llamados de pila:

```scala
foldLeft call → step 1 tablon=0
    produce (t=(0,0,0,0,0), tiempo=3)
foldLeft call → step 2 tablon=1
    produce (t=(0,3,0,0,0), tiempo=6)
foldLeft call → step 3 tablon=4
    produce (t=(0,3,0,0,6), tiempo=10)
foldLeft call → step 4 tablon=2
    produce (t=(0,3,10,0,6), tiempo=12)
foldLeft call → step 5 tablon=3
    produce (t=(0,3,10,12,6), tiempo=13)
return tInit = (0,3,10,12,6)
```

En conclusión, `foldLeft` se encarga de recorrer el valor de pi atribuido al orden de riego
para llevar un conteo de los tiempos obtenidos por cada uno de estos (acumulador), e ir
actualizandolos individualmente mientras se mantiene `tiempoActual` activa por cada llamado
para al finar pasar el valor final a la variable `t`.

# Informe de corrección:

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
quiere decir de que el tiempo de riego no ha llegado a tiempo como se esperaba.

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
los indices, el foldLeft acumulara el valor total de cada tablon, explicado
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

# Informe de corrección:

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
