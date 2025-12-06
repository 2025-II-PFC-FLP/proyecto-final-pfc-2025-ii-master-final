package taller

import org.scalameter.measure
import taller.RiegoOptimo._

object Benchmarking {

  def main(args: Array[String]): Unit = {

    benchmarkingGenerarProgramacionRiego()

  }


  val fincaPeque : Finca = fincaAlAzar(4)
  val fincaMed : Finca = fincaAlAzar(20)
  val fincaGigante : Finca = fincaAlAzar(100)


  def benchmarkingGenerarProgramacionRiego(): Unit = {

    println("\n Benchmarking para programación de riegos")

/*    val t1 = measure {generarProgramacionesRiego(fincaPeque)}
    val t2 = measure {generarProgramacionesRiegoPar(fincaPeque)}
    println(s"  Secuencial expr1: $t1")
    println(s"  Paralelo expr1: $t2")
    println(s"  Aceleracion: ${t1.value/t2.value}")

    val t3 = measure {generarProgramacionesRiego(fincaMed)}
    val t4 = measure {generarProgramacionesRiegoPar(fincaMed)}
    println(s"  Secuencial expr2: $t3")
    println(s"  Paralelo expr2: $t4")
    println(s"  Aceleracion: ${t3.value/t4.value}")

    val t5 = measure {generarProgramacionesRiego(fincaGigante)}
    val t6 = measure {generarProgramacionesRiegoPar(fincaGigante)}
    println(s"  Secuencial expr3: $t5")
    println(s"  Paralelo expr3: $t6")
    println(s"  Aceleracion: ${t5.value/t6.value}")*/
  }



}
