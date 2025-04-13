package core

import spinal.core._
import spinal.lib._

// Hardware definition
case class MyTopLevel() extends Component {
  val io = new Bundle {
    val cond0 = in  Bool()
    val cond1 = in  Bool()
    val flag  = out Bool()
    val state = out UInt(8 bits)
  }

  val counter = Reg(UInt(8 bits)) init 0

  when(io.cond0) {
    counter := counter + 1
  }

  io.state := counter
  io.flag := (counter === 0) | io.cond1
}

class AdderCell() extends  Component {
  val io = new Bundle {
    val a, b, cin = in  port Bool()
    val sum, cout = out port Bool()
  }

  io.sum  := io.a ^ io.b ^ io.cin
  io.cout := (io.a & io.b) | (io.a & io.cin) | (io.b * io.cin)
}

class Adder(size: Int) extends Component {
  val io = new Bundle {
    val a, b   = in  UInt(size bits)
    val result = out UInt(size bits)
  }

  var carry = False

  for (i <- 0 until size) {
    val a = io.a(i)
    val b = io.b(i)

    io.result(i) := a ^ b ^ c
    c \= (a & b) | (a & c) | (b & c);
  }
}

object MyTopLevelVerilog extends App {
  Config.spinal.generateVerilog(MyTopLevel())
}

object MyTopLevelVhdl extends App {
  Config.spinal.generateVhdl(MyTopLevel())
}
