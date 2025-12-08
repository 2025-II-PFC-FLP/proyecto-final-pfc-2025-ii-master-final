## 4.3. Informe de Corrección: `ProgramacionRiegoOptimoPar`

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