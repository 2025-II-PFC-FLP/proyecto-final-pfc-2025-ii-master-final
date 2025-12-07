package taller

import org.scalameter._
import taller.RiegoOptimo._

class Benchmarking {

  val fincaPeque : Finca = fincaAlAzar(2)
  val fincaMed : Finca = fincaAlAzar(6)
  val fincaGigante : Finca = fincaAlAzar(8)


  def benchmarkingCostosRiego(): Unit = {

    println("\n Benchmarking para costos de riego\n")

    val t1 = withWarmer(new Warmer.Default) measure {costoRiegoFinca(fincaPeque, Vector(0,1))}
    val t2 = withWarmer(new Warmer.Default) measure {costoRiegoFincaPar(fincaPeque, Vector(0,1))}
    println(s"  Secuencial pi1: $t1")
    println(s"  Paralelo pi1: $t2")
    println(s"  Aceleracion: ${t1.value/t2.value}\n")

    val t3 = withWarmer(new Warmer.Default) measure {costoRiegoFinca(fincaMed, Vector(2,1,4,3,0,5))}
    val t4 = withWarmer(new Warmer.Default) measure {costoRiegoFincaPar(fincaMed, Vector(2,1,4,3,0,5))}
    println(s"  Secuencial pi2: $t3")
    println(s"  Paralelo pi2: $t4")
    println(s"  Aceleracion: ${t3.value/t4.value}\n")

    val t5 = withWarmer(new Warmer.Default) measure {costoRiegoFinca(fincaGigante, Vector(2,1,5,4,3,6,7,0))}
    val t6 = withWarmer(new Warmer.Default) measure {costoRiegoFincaPar(fincaGigante, Vector(2,1,5,4,3,6,7,0))}
    println(s"  Secuencial pi3: $t5")
    println(s"  Paralelo pi3: $t6")
    println(s"  Aceleracion: ${t5.value/t6.value}\n")
  }


  def benchmarkingCostosMovilidad(): Unit = {
    println("\n Benchmarking para costos de movilidad\n")

    val t1 = withWarmer(new Warmer.Default) measure {costoMovilidad(fincaPeque, Vector(0,1,4,2,3), Vector(Vector(0,2,2,4,4), Vector(2,0,4,2,6), Vector(2,4,0,2,2), Vector(4,2,2,0,4), Vector(4,6,2,4,0)))}
    val t2 = withWarmer(new Warmer.Default) measure {costoMovilidadPar(Vector(0,1,4,2,3), Vector(Vector(0,2,2,4,4), Vector(2,0,4,2,6), Vector(2,4,0,2,2), Vector(4,2,2,0,4), Vector(4,6,2,4,0)))}
    println(s"  Secuencial e1: $t1")
    println(s"  Paralelo e1: $t2")
    println(s"  Aceleracion: ${t1.value/t2.value}\n")

    val t3 = withWarmer(new Warmer.Default)  measure {costoMovilidad(fincaPeque, Vector(2,1,4,3,0,5), Vector(Vector(0, 4, 8, 18, 7, 17), Vector(4, 0, 5, 10, 18, 10), Vector(8, 5, 0, 3, 2, 2), Vector(18, 10, 3, 0, 8, 10), Vector(7, 18, 2, 8, 0, 5), Vector(17, 10, 2, 10, 5, 0)))}
    val t4 = withWarmer(new Warmer.Default) measure {costoMovilidadPar(Vector(2,1,4,3,0,5), Vector(Vector(0, 4, 8, 18, 7, 17), Vector(4, 0, 5, 10, 18, 10), Vector(8, 5, 0, 3, 2, 2), Vector(18, 10, 3, 0, 8, 10), Vector(7, 18, 2, 8, 0, 5), Vector(17, 10, 2, 10, 5, 0)))}
    println(s"  Secuencial e2: $t3")
    println(s"  Paralelo e2: $t4")
    println(s"  Aceleracion: ${t3.value/t4.value}\n")

    val t5 = withWarmer(new Warmer.Default) measure {costoMovilidad(fincaPeque, Vector(0,1,5,6,4,2,3), Vector(Vector(0, 1, 3, 7, 13, 15, 5), Vector(1, 0, 9, 5, 19, 8, 2), Vector(3, 9, 0, 20, 18, 13, 9), Vector(7, 5, 20, 0, 1, 16, 12), Vector(13, 19, 18, 1, 0, 14, 12), Vector(15, 8, 13, 16, 14, 0, 16), Vector(5, 2, 9, 12, 12, 16, 0)))}
    val t6 = withWarmer(new Warmer.Default) measure {costoMovilidadPar(Vector(0,1,5,6,4,2,3), Vector(Vector(0, 1, 3, 7, 13, 15, 5), Vector(1, 0, 9, 5, 19, 8, 2), Vector(3, 9, 0, 20, 18, 13, 9), Vector(7, 5, 20, 0, 1, 16, 12), Vector(13, 19, 18, 1, 0, 14, 12), Vector(15, 8, 13, 16, 14, 0, 16), Vector(5, 2, 9, 12, 12, 16, 0)))}
    println(s"  Secuencial e3: $t5")
    println(s"  Paralelo e3: $t6")
    println(s"  Aceleracion: ${t5.value/t6.value}\n")

    val t7 = withWarmer(new Warmer.Default) measure {costoMovilidad(fincaPeque, Vector(2,1,5,4,3,6,7,0), Vector(Vector(0, 8, 11, 1, 22, 21, 12, 9), Vector(8, 0, 8, 20, 9, 13, 13, 6), Vector(11, 8, 0, 22, 8, 18, 12, 3), Vector(1, 20, 22, 0, 11, 9, 8, 4), Vector(22, 9, 8, 11, 0, 21, 13, 16), Vector(21, 13, 18, 9, 21, 0, 7, 21), Vector(12, 13, 12, 8, 13, 7, 0, 6), Vector(9, 6, 3, 4, 16, 21, 6, 0)))}
    val t8 = withWarmer(new Warmer.Default) measure {costoMovilidadPar(Vector(2,1,5,4,3,6,7,0), Vector(Vector(0, 8, 11, 1, 22, 21, 12, 9), Vector(8, 0, 8, 20, 9, 13, 13, 6), Vector(11, 8, 0, 22, 8, 18, 12, 3), Vector(1, 20, 22, 0, 11, 9, 8, 4), Vector(22, 9, 8, 11, 0, 21, 13, 16), Vector(21, 13, 18, 9, 21, 0, 7, 21), Vector(12, 13, 12, 8, 13, 7, 0, 6), Vector(9, 6, 3, 4, 16, 21, 6, 0)))}
    println(s"  Secuencial e4: $t7")
    println(s"  Paralelo e4: $t8")
    println(s"  Aceleracion: ${t7.value/t8.value}\n")

  }

  def benchmarkingGenerarProgramacionRiego(): Unit = {
    println("\n Benchmarking para programación de riegos\n")

    val t1 = withWarmer(new Warmer.Default) measure {generarProgramacionesRiego(fincaPeque)}
    val t2 = withWarmer(new Warmer.Default) measure {generarProgramacionesRiegoPar(fincaPeque)}
    println(s"  Secuencial e1: $t1")
    println(s"  Paralelo e1: $t2")
    println(s"  Aceleracion: ${t1.value/t2.value}\n")

    val t3 = withWarmer(new Warmer.Default) measure {generarProgramacionesRiego(fincaMed)}
    val t4 = withWarmer(new Warmer.Default) measure {generarProgramacionesRiegoPar(fincaMed)}
    println(s"  Secuencial e2: $t3")
    println(s"  Paralelo e2: $t4")
    println(s"  Aceleracion: ${t3.value/t4.value}\n")

    val t5 = withWarmer(new Warmer.Default) measure {generarProgramacionesRiego(fincaGigante)}
    val t6 = withWarmer(new Warmer.Default) measure {generarProgramacionesRiegoPar(fincaGigante)}
    println(s"  Secuencial e3: $t5")
    println(s"  Paralelo e3: $t6")
    println(s"  Aceleracion: ${t5.value/t6.value}\n")
  }

  def benchmarkingProgramacionOptima(): Unit = {

  }



}
