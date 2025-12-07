# INFORME DE PROCESO

La función ProgramacionRiegoOptimo es recursiva indirectamente porque:

- Recorre la lista de permutaciones generada en 2.5.

- Para cada permutación invoca funciones recursivas (costoRiego, tIR, etc.)


A continuación, se muestra el proceso interno, usando un ejemplo reducido con 3 tablones.

Sea la finca F con 3 tablones.
Permutaciones generadas:
$$
Π_1 = \langle 0,1,2 \rangle \\
Π_2 = \langle 0,2,1 \rangle \\
Π_3 = \langle 1,0,2 \rangle \\
Π_4 = \langle 1,2,0 \rangle \\
Π_5 = \langle 2,0,1 \rangle \\
Π_6 = \langle 2,1,0 \rangle
$$

La función evalúa cada una recursivamente.


---

## 1. Pila de llamadas para evaluar los costos de una permutación

Ejemplo con Π = ⟨0,1,2⟩:

```mermaid
sequenceDiagram
participant P as ProgramacionRiegoOptimo
participant C1 as costoRiegoFinca
participant C2 as costoMovilidad
participant T as tIR

    P->>T: tIR(F, Π)
    T-->>P: tiempos calculados

    P->>C1: costoRiegoFinca(F, Π)
    C1->>C1: costoRiegoTablon(0)
    C1->>C1: costoRiegoTablon(1)
    C1->>C1: costoRiegoTablon(2)
    C1-->>P: CR total

    P->>C2: costoMovilidad(F, Π, D)
    C2-->>P: CM total

    P-->>P: comparar costo con mínimo previo
```

---

## 2. Proceso recursivo de selección del mínimo

Para simplificar, asumamos que la función usa un foldLeft.

Estado inicial:
```scala
mejorProg = Π1
mejorCosto = C(Π1)
índice = 2
``` 
Iteración 2 (Π₂)
```mermaid
graph TD
    A[Comparar costo de PI2] --> B{C_PI2_menor_que_mejorCosto?}
    B -- SI --> C[Actualizar mejorProg con PI2]
    B -- SI --> D[Actualizar mejorCosto con costo_PI2]
    B -- NO --> E[Mantener valores previos]
```
Iteración 3 (Π₃)

Se repite el mismo proceso, formando una recursión por acumulación.
```mermaid
graph TD
A[Comparar costo de PI3] --> B{C_PI3_menor_que_mejorCosto?}
B -- SI --> C[Actualizar mejorProg con PI3]
B -- SI --> D[Actualizar mejorCosto]
B -- NO --> E[Mantener valores previos]

```
---

## 3. Diagrama final del proceso completo
```mermaid
graph TD
    A[generar_programaciones_riego] --> B[lista_de_permutaciones]
    B --> C[evaluar_costo_PI1]
    B --> D[evaluar_costo_PI2]
    B --> E[evaluar_costo_PI3]
    B --> F[evaluar_costo_PI4]
    B --> G[evaluar_costo_PI5]
    B --> H[evaluar_costo_PI6]

    C --> Z[comparar_costos_y_mantener_minimo]
    D --> Z
    E --> Z
    F --> Z
    G --> Z
    H --> Z

    Z --> R[retornar_programacion_optima]

```
