package taller

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

import RiegoOptimo._

@RunWith(classOf[JUnitRunner])
class RiegoOptimoParTest extends AnyFunSuite with Matchers {

  test("1. costoRiegoFincaPar: debe dar el mismo resultado que la versión secuencial") {
    val f = Vector((10, 3, 2), (5, 2, 3), (8, 4, 1))
    val pi = Vector(2, 0, 1)

    val costoSeq = costoRiegoFinca(f, pi)
    val costoPar = costoRiegoFincaPar(f, pi)

    costoPar shouldBe costoSeq
  }

  test("2. costoRiegoFincaPar: finca vacía debe retornar 0") {
    val fVacia = Vector.empty[Tablon]
    val piVacio = Vector.empty[Int]

    val costoPar = costoRiegoFincaPar(fVacia, piVacio)

    costoPar shouldBe 0
  }

  test("3. costoRiegoFincaPar: un solo tablón") {
    val f = Vector((10, 2, 1))
    val pi = Vector(0)

    val costoSeq = costoRiegoFinca(f, pi)
    val costoPar = costoRiegoFincaPar(f, pi)

    costoPar shouldBe costoSeq
  }

  test("4. costoRiegoFincaPar: finca grande (6 tablones)") {
    val f = Vector(
      (10, 3, 4), (5, 3, 3), (2, 2, 1),
      (8, 1, 1), (6, 4, 2), (12, 2, 3)
    )
    val pi = Vector(2, 1, 4, 3, 0, 5)

    val costoSeq = costoRiegoFinca(f, pi)
    val costoPar = costoRiegoFincaPar(f, pi)

    costoPar shouldBe costoSeq
  }

  test("5. costoMovilidadPar: debe dar el mismo resultado que la versión secuencial") {
    val pi = Vector(0, 1, 4, 2, 3)
    val d = Vector(
      Vector(0, 2, 2, 4, 4),
      Vector(2, 0, 4, 2, 6),
      Vector(2, 4, 0, 2, 2),
      Vector(4, 2, 2, 0, 4),
      Vector(4, 6, 2, 4, 0)
    )

    val costoSeq = costoMovilidad(fincaAlAzar(5), pi, d)
    val costoPar = costoMovilidadPar(pi, d)

    costoPar shouldBe costoSeq
  }
}