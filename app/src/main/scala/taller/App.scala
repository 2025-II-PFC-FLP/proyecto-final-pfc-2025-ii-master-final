package taller

object App {
  def main(args: Array[String]): Unit = {
    val benchmarking : Benchmarking = new Benchmarking()

    benchmarking.benchmarkingCostosRiego()
    benchmarking.benchmarkingCostosMovilidad()
    benchmarking.benchmarkingGenerarProgramacionRiego()
    benchmarking.benchmarkingProgramacionOptima()

  }
}
