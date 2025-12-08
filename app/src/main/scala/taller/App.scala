package taller

object App {
  def greeting(): String = {
    "¡Hello World!."
  }
  def main(args: Array[String]): Unit = {
    val benchmarking : Benchmarking = new Benchmarking()

    benchmarking.benchmarkingCostosRiego()
    benchmarking.benchmarkingCostosMovilidad()
    benchmarking.benchmarkingGenerarProgramacionRiego()
    benchmarking.benchmarkingProgramacionOptima()

  }
}