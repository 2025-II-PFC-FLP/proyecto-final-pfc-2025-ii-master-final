package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TestTIRyCostoRiegos extends AnyFunSuite {

  test("Ejemplo de tIR Numero 1 presente en el taler ") {

    val finca: RiegoOptimo.Finca = Vector(
      (10, 3, 4),
      (5, 3, 3),
      (2, 2, 1),
      (8, 1, 1),
      (6, 4, 2)
    )

    val prog: RiegoOptimo.ProgRiego = Vector(0, 1, 4, 2, 3)

    val tIResperado: RiegoOptimo.TiempoInicioRiego =
      Vector(0, 3, 10, 12, 6)

    val resultado = RiegoOptimo.tIR(finca, prog)

    println("El resultado obtenido fue: " + resultado)
    println("El resultado esperado fue: " + tIResperado)

    assert(resultado == tIResperado)
  }

  test("Ejemplo de tIR Numero 2 presente en el taler ") {

    val finca: RiegoOptimo.Finca = Vector(
      (10, 3, 4),
      (5, 3, 3),
      (2, 2, 1),
      (8, 1, 1),
      (6, 4, 2)
    )

    val prog: RiegoOptimo.ProgRiego = Vector(2, 1, 4, 3, 0)

    val tIResperado: RiegoOptimo.TiempoInicioRiego =
      Vector(10, 2, 0, 9, 5)

    val resultado = RiegoOptimo.tIR(finca, prog)

    println("El resultado obtenido fue: " + resultado)
    println("El resultado esperado fue: " + tIResperado)

    assert(resultado == tIResperado)
  }

  test("Ejemplo de tIR Numero 3 presente en el taler ") {

    val finca: RiegoOptimo.Finca = Vector(
      (9, 3, 4),
      (5, 3, 3),
      (2, 2, 1),
      (8, 1, 1),
      (6, 4, 2)
    )

    val prog: RiegoOptimo.ProgRiego = Vector(2, 1, 4, 0, 3)

    val tIResperado: RiegoOptimo.TiempoInicioRiego =
      Vector(9, 2, 0, 12, 5)

    val resultado = RiegoOptimo.tIR(finca, prog)

    println("El resultado obtenido fue: " + resultado)
    println("El resultado esperado fue: " + tIResperado)

    assert(resultado == tIResperado)
  }
}




