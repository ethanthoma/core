package core

import spinal.core._
import spinal.core.sim._
import spinal.lib._
import spinal.lib.sim.{FlowDriver, FlowMonitor, ScoreboardInOrder}
import spinal.lib.experimental.math._

object TestFP16Adder extends App {
  Config.sim.compile(FP16Adder()).doSim { dut =>
    SimTimeout(100000)

    val scoreboard = ScoreboardInOrder[Int]()

    FlowDriver(dut.io.input, dut.clockDomain) { payload =>
      payload.randomize()
      true
    }
    FlowMonitor(dut.io.input, dut.clockDomain) { payload =>
      scoreboard.pushRef(payload.toInt)
    }

    FlowDriver(dut.io.output, dut.clockDomain) { payload =>
      scoreboard.pushDut(payload.toInt)
    }

    dut.clockDomain.forkStimulus(period = 10)
    dut.clockDomain.waitActiveEdgeWhere(scoreboard.matches == 100)
  }
}
