# Learning Hardware

## Running

```sh
// To generate the Verilog from the example
sbt "runMain core.MyTopLevelVerilog"

// To generate the VHDL from the example
sbt "runMain core.MyTopLevelVhdl"

// To run the testbench
sbt "runMain core.MyTopLevelSim"
```

* The example hardware description is into `hw/spinal/core/MyTopLevel.scala`
* The testbench is into `hw/spinal/core/MyTopLevelSim.scala`
