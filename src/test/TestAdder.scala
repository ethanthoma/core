package core

import spinal.core._
import spinal.core.sim._

object TestAdder extends App {
  val width = 8

  Config.sim.compile(Adder(width)).doSim { dut =>
    dut.clockDomain.forkStimulus(period = 10)

    def checkAddition(a: Int, b: Int): Unit = {
      // Apply the stimulus
      dut.io.a #= a
      dut.io.b #= b
      
      // Wait for simulation to propagate values
      sleep(1)
      
      // Get the expected result (with truncation to 8 bits)
      val expected = (a + b) & 0xFF
      val actual = dut.io.result.toInt
      
      // Check the result
      assert(actual == expected)
      if (actual == expected) {
        println(f"PASS: $a%3d + $b%3d = $actual%3d")
      } else {
        println(f"FAIL: $a%3d + $b%3d = $actual%3d (expected $expected%3d)")
      }
    }

    dut.io.a #= 0
    dut.io.b #= 0

    dut.clockDomain.waitRisingEdge()

    println("Testing basic additions:")
    checkAddition(0, 0)
    checkAddition(1, 0)
    checkAddition(0, 1)
    checkAddition(1, 1)
    checkAddition(5, 10)
    checkAddition(42, 100)
    checkAddition(255, 0)
    checkAddition(200, 55)

    println("\nTesting random cases:")
    for(_ <- 0 until 5) {
      val a = scala.util.Random.nextInt(256)
      val b = scala.util.Random.nextInt(256)
      checkAddition(a, b)
    }
  }
}
