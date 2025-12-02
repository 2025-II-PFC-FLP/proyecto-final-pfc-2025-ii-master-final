package taller

import org.scalatest.funsuite.AnyFunSuite

class ProgramacionRiegoOptimoTest extends AnyFunSuite {

  test("Caso 1: Finca de 1 tablón debe devolver el único orden posible") {
    val f = Vector((5, 3, 2))
    val d = Vector(Vector(0))

    val (pi, costo) = RiegoOptimo.ProgramacionRiegoOptimo(f, d)

    assert(pi == Vector(0))
    assert(costo == RiegoOptimo.costoRiegoFinca(f, pi))
  }

  test("Caso 2: Finca de 2 tablones siempre debe devolver una permutación válida") {
    val f = Vector((5, 2, 1), (4, 1, 2))
    val d = Vector(
      Vector(0, 3),
      Vector(3, 0)
    )

    val (pi, _) = RiegoOptimo.ProgramacionRiegoOptimo(f, d)

    assert(pi.sorted == Vector(0, 1)) // es una permutación válida
  }

  test("Caso 3: Debe calcular un costo total coherente (no negativo)") {
    val f = Vector(
      (6, 2, 1),
      (7, 2, 1),
      (5, 1, 1)
    )

    val d = Vector(
      Vector(0, 1, 2),
      Vector(1, 0, 1),
      Vector(2, 1, 0)
    )

    val (_, costo) = RiegoOptimo.ProgramacionRiegoOptimo(f, d)

    assert(costo >= 0)
  }

  test("Caso 4: Todas las programaciones generadas deben ser evaluadas sin error") {
    val f = Vector((5, 2, 1), (3, 1, 2), (4, 2, 1))
    val d = Vector(
      Vector(0, 2, 3),
      Vector(2, 0, 1),
      Vector(3, 1, 0)
    )

    val todas = RiegoOptimo.generarProgramacionesRiego(f)

    // No debe causar excepción al evaluar todas
    val evaluadas = todas.map(pi =>
      RiegoOptimo.costoRiegoFinca(f, pi) + RiegoOptimo.costoMovilidad(f, pi, d)
    )

    assert(evaluadas.length == 6)
  }

  test("Caso 5: La función debe devolver un par (programación, costo)") {
    val f = Vector(
      (8, 3, 2),
      (4, 1, 3),
      (6, 2, 1)
    )

    val d = Vector(
      Vector(0, 4, 2),
      Vector(4, 0, 3),
      Vector(2, 3, 0)
    )

    val resultado = RiegoOptimo.ProgramacionRiegoOptimo(f, d)

    assert(resultado._1.length == 3) // programación válida
    assert(resultado._2 >= 0) // costo válido
  }
}
