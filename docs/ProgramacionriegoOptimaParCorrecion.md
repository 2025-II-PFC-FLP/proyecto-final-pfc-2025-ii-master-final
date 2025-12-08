## 4.3. Informe de Corrección: `ProgramacionRiegoOptimoPar`

### Argumentación de Corrección
Se debe demostrar que la función `buscarMinimoPar(S)` encuentra el mínimo global del conjunto de programaciones $S$, tal que retorna $\Pi_{opt} \in S$ donde Costo($\Pi_{opt}$) $\le$ Costo($\Pi_k$) $\forall \Pi_k \in S$.

**Definiciones:**
* Sea $C(\pi)$ el costo total de una programación.
* Sea $f(S)$ la función que retorna la tupla $(\pi, C(\pi))$ con el menor $C(\pi)$ en el vector $S$.

**Demostración por Inducción Estructural:**

1.  **Caso Base:** $|S| \le \text{umbral}$.
    El algoritmo ejecuta:
    $$S.map(\pi \to (\pi, C(\pi))).minBy(\_.\_2)$$
    Por definición de la función de orden superior `minBy` en colecciones de Scala, esto garantiza encontrar el elemento mínimo recorriendo linealmente el subconjunto. La corrección es trivial dada la corrección de `costoRiegoFinca` y `costoMovilidad`.

2.  **Paso Inductivo:** $|S| > \text{umbral}$.
    El conjunto $S$ se divide en dos subconjuntos disyuntos $S_{izq}$ y $S_{der}$ tales que $S = S_{izq} \cup S_{der}$.

    * **Hipótesis de Inducción (H.I.):** Asumimos que las llamadas recursivas son correctas.
        * $res_{izq} = \text{buscarMinimoPar}(S_{izq}) = \min_{\pi \in S_{izq}} C(\pi)$
        * $res_{der} = \text{buscarMinimoPar}(S_{der}) = \min_{\pi \in S_{der}} C(\pi)$

    * **Paso de Combinación:** El algoritmo ejecuta:
      $$\text{if } (res_{izq}.costo \le res_{der}.costo) \text{ then } res_{izq} \text{ else } res_{der}$$

    Matemáticamente, el mínimo de un conjunto unido es el mínimo de los mínimos de sus partes:
    $$\min(S) = \min(\min(S_{izq}), \min(S_{der}))$$

    Por lo tanto, al comparar los resultados de las llamadas recursivas (que son correctas por H.I.), la función retorna el mínimo global de $S$.

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