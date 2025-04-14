package core

import spinal.core._
import spinal.lib._


case class Adder(size: Int) extends Component {
  val io = new Bundle {
    val a, b   = in  UInt(size bits)
    val result = out UInt(size bits)
  }

  var carry = False

  for (i <- 0 until size) {
    val a = io.a(i)
    val b = io.b(i)

    io.result(i) := a ^ b ^ carry
    carry \= (a & b) | (a & carry) | (b & carry);
  }
}
