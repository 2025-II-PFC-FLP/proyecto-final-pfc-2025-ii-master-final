package taller

object Main extends App {
  // Instanciamos la clase de pruebas
  val benchmark = new Benchmarking()

  println("=== Iniciando Benchmarking del Punto 3.3 (Optimización) ===")

  // Llamamos a la función que mide ProgramacionRiegoOptimoPar vs Secuencial
  benchmark.benchmarkingProgramacionOptima()

  println("=== Fin del Benchmarking ===")
}