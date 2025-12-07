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

  test("Ejemplo de costoRiegoTablon hipotetico") {

    val finca: RiegoOptimo.Finca = Vector(
      (10, 3, 1),   // (tsup, treg, prio)
      (12, 2, 2),
      (15, 4, 3)
    )

    val prog: RiegoOptimo.ProgRiego = Vector(0, 1, 2)
    // tIR = Vector(0, 3, 5)

    /* Para tablon 0:
     ts = 10, tr = 3, t = 0, p = 1
     ts - tr = 7 >= 0  => rama 1
     costo = 10 - (0+3) = 7*/

    val res = RiegoOptimo.costoRiegoTablon(0, finca, prog)
    assert(res == 7)
  }

  test("Ejemplo de costoRiegoFinca (aqui se hace una suma de tablones)") {

    val finca: RiegoOptimo.Finca = Vector(
      (10, 3, 1),
      (12, 2, 2),
      (15, 4, 3)
    )

    val prog: RiegoOptimo.ProgRiego = Vector(0, 1, 2)
    // tIR = Vector(0, 3, 5)

    /* Tab.0 = 7  (del test anterior)

     Para tab.1:
     ts = 12, tr = 2, t = 3
     ts - tr = 10 >= 3 → rama 1
     costo = 12 - (3+2) = 7

     Para tab.2:
     ts = 15, tr = 4, t = 5
     ts - tr = 11 >= 5 → rama 1
     costo = 15 - (5+4) = 6

     Total = 20 */

    val res = RiegoOptimo.costoRiegoFinca(finca, prog)
    assert(res == 20)
  }

  test("Ejemplo de costoMovilidad con una matriz simple") {

    val finca: RiegoOptimo.Finca = Vector(
      (10, 3, 1),
      (12, 2, 2),
      (15, 4, 3)
    )

    val prog: RiegoOptimo.ProgRiego = Vector(0, 1, 2)

    val distancias: RiegoOptimo.Distancia = Vector(
      Vector(0, 5, 9),
      Vector(5, 0, 4),
      Vector(9, 4, 0)
    )

    // pi = Vector(0,1,2)
    /* se suman:
       d(0)(1) = 5
       d(1)(2) = 4
     total = 9*/

    val res = RiegoOptimo.costoMovilidad(finca, prog, distancias)
    assert(res == 9)
  }

}




