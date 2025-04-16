package core

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import spinal.lib.experimental.math._

case class FP16Adder() extends Component {
  val io = new Bundle {
    val input = slave Flow (new Bundle {
      val a = Floating16()
      val b = Floating16()
    })
    val output = master Flow Floating16()
  }

  val FLOAT_A = Payload(Floating16())
  val FLOAT_B = Payload(Floating16())

  val REC_A                  = Payload(Floating16().toRecFloating)
  val REC_B                  = Payload(Floating16().toRecFloating)
  val SHIFT_AMOUNT           = Payload(UInt(Floating16().exponentSize + 1 bits))
  val RESULT_EXP_PRE_NORM    = Payload(SInt(Floating16().exponentSize + 2 bits))
  val USE_SUBTRACTION        = Payload(Bool())
  val RESULT_IS_NAN          = Payload(Bool())
  val RESULT_IS_INF          = Payload(Bool())
  val RESULT_IS_ZERO_PRE_ADD = Payload(Bool())
  val RESULT_SIGN_PRE_ADD    = Payload(Bool())

  val unpack = new Node {
    arbitrateFrom(io.input)
    
    FLOAT_A := insert(io.input.payload.a)
    FLOAT_B := insert(io.input.payload.b)

    REC_A := insert(FLOAT_A.toRecFloating)
    REC_B := insert(FLOAT_B.toRecFloating)

    val resIsNaN  = REC_A.isNaN || REC_B.isNaN || (REC_A.isInfinite && REC_B.isInfinite && (REC_A.sign =/= REC_B.sign))
    val resIsInf  = (REC_A.isInfinite || REC_B.isInfinite) && !resIsNaN
    val resIsZero = REC_A.isZero && REC_B.isZero // Only if both are zero initially

    val useSubtraction_int = REC_A.sign =/= REC_A.sign
    val prelimSign         = REC_A.sign

    val expA_uint        = REC_A.exponent.asUInt
    val expB_uint        = REC_B.exponent.asUInt
    val compAExpIsLarger = expA_uint >= expB_uint

    val expA_sint    = REC_A.exponent.asSInt.resize(Floating16().exponentSize + 2 bits)
    val expB_sint    = REC_B.exponent.asSInt.resize(Floating16().exponentSize + 2 bits)
    val expDiff_sint = expA_sint - expB_sint

    val largerExp_sint = Mux(compAExpIsLarger, expA_sint, expB_sint)

    val shiftAmount_uint = expDiff_sint.abs

    SHIFT_AMOUNT           := insert(shiftAmount_uint.resize(Floating16().exponentSize + 1 bits)) 
    RESULT_EXP_PRE_NORM    := insert(largerExp_sint) 
    USE_SUBTRACTION        := insert(useSubtraction_int) 
    RESULT_IS_NAN          := insert(resIsNaN) 
    RESULT_IS_INF          := insert(resIsInf) 
    RESULT_IS_ZERO_PRE_ADD := insert(resIsZero) 
    RESULT_SIGN_PRE_ADD    := insert(prelimSign) 
  }

  val align = new Node { }

  val add = new Node { }

  val normalize = new Node { }

  val output = new Node { }

  val nodes = Seq(unpack, align, add, normalize, output)

  val stages = nodes.sliding(2).map {
    case Seq(node_0, node_1) => StageLink(node_0, node_1)
  }.toSeq

  Builder(stages)
}
