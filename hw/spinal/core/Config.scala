package core

import spinal.core._
import spinal.core.sim._

object Config {
  def spinal = SpinalConfig(
    targetDirectory = "hw/gen",
    defaultConfigForClockDomains = ClockDomainConfig(
      resetActiveLevel = HIGH
    ),
    onlyStdLogicVectorAtTopLevelIo = false
  )

  // disable the cache cause sbt cannot find the pluginsCacheDir
  def sim = SimConfig.withConfig(spinal).withIVerilog.disableCache
}
