package taller

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

import RiegoOptimo._

@RunWith(classOf[JUnitRunner])
class RiegoOptimoTest extends AnyFunSuite with Matchers {

  test("1. ProgramacionRiegoOptimoPar: Cálculo exacto con 2 tablones") {
    // T0: ts=10, tr=5, p=1
    // T1: ts=10, tr=5, p=1
    val f = Vector((10, 5, 1), (10, 5, 1))

    // Distancia entre 0 y 1 es 10
    val d = Vector(
      Vector(0, 10),
      Vector(10, 0)
    )

    val (prog, costo) = ProgramacionRiegoOptimoPar(f, d)

    // Análisis matemático para el informe:
    // 1. Riego T_primero: inicia 0, fin 5. Costo = 10 - 5 = 5.
    // 2. Riego T_segundo: inicia 5, fin 10. Costo = 10 - 10 = 0.
    // 3. Movilidad: d(0,1) = 10[cite: 65].
    // Costo Total Esperado = 5 (riego) + 10 (movilidad) = 15.

    costo shouldBe 15
    prog.length shouldBe 2
  }

  test("2. Un solo tablón: validación de fórmula ts - (t + tr)") {
    val fUnica = Vector((10, 2, 1)) // ts=10, tr=2, p=1
    val dUnica = Vector(Vector(0))

    val (prog, costo) = ProgramacionRiegoOptimoPar(fUnica, dUnica)

    // t=0. Cálculo: 10 - (0 + 2) = 8
    costo shouldBe 8
    prog shouldBe Vector(0)
  }


  test("3. Prioridad: debe elegir regar primero el tablón con prioridad 4") {
    // T0: ts=5, tr=5, p=1 (Poca multa)
    // T1: ts=5, tr=5, p=4 (Mucha multa)
    val f = Vector((5, 5, 1), (5, 5, 4))
    val d = Vector(Vector(0, 0), Vector(0, 0)) // Distancia cero para aislar costo riego

    val (prog, costo) = ProgramacionRiegoOptimoPar(f, d)

    // Orden (1, 0): T1 inicia en 0 (Costo 0). T0 inicia en 5. Multa T0: 1 * ((5+5)-5) = 5. Total = 5.
    // Orden (0, 1): T0 inicia en 0 (Costo 0). T1 inicia en 5. Multa T1: 4 * ((5+5)-5) = 20. Total = 20.
    prog.head shouldBe 1
    costo shouldBe 5
  }

  test("4. Movilidad: debe elegir el camino más corto entre tablones") {
    val f = Vector.fill(3)((100, 1, 1)) // Riego casi gratuito
    // 0 y 1 están cerca (distancia 1). 1 y 2 están cerca (distancia 1).
    // 0 y 2 están muy lejos (distancia 50).
    val d = Vector(
      Vector(0,  1, 50),
      Vector(1,  0,  1),
      Vector(50, 1,  0)
    )

    val (prog, _) = ProgramacionRiegoOptimoPar(f, d)

    // El camino óptimo debe ser 0-1-2 o 2-1-0. Nunca 0-2 (salto de 50).
    prog.sliding(2).exists(p => (p(0)==0 && p(1)==2) || (p(0)==2 && p(1)==0)) shouldBe false
  }

  test("5. Consistencia: el costo mínimo paralelo debe igualar al secuencial") {
    // Finca controlada para evitar variabilidad aleatoria
    val f = Vector((10,2,4), (5,3,3), (2,2,1))
    val d = Vector(Vector(0,2,4), Vector(2,0,4), Vector(4,4,0))

    val (_, costoSeq) = ProgramacionRiegoOptimo(f, d)
    val (_, costoPar) = ProgramacionRiegoOptimoPar(f, d)

    costoPar shouldBe costoSeq
  }
}