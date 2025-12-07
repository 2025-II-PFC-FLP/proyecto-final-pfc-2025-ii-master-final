# INFORME DE CORRECCIÓN – Punto 2.6 (Programación de Riego Óptima)

## 1. Argumentación de corrección

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



## Correctitud parcial (función devuelve un candidato válido)

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

## Correctitud total (selección del mínimo)

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
