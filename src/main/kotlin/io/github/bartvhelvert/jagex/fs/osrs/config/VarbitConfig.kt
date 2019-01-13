package io.github.bartvhelvert.jagex.fs.osrs.config

import io.github.bartvhelvert.jagex.fs.io.uByte
import io.github.bartvhelvert.jagex.fs.io.uShort
import io.github.bartvhelvert.jagex.fs.osrs.ConfigFile
import io.github.bartvhelvert.jagex.fs.osrs.ConfigFileCompanion
import java.nio.ByteBuffer


class VarbitConfig @ExperimentalUnsignedTypes constructor(
    id: Int,
    val varpId: UShort,
    val lsb: UByte,
    val msb: UByte
) : ConfigFile(id) {
    @ExperimentalUnsignedTypes
    override fun encode(): ByteBuffer  = if(varpId.toInt() != 0 && lsb.toInt() != 0 && msb.toInt() != 0) {
        ByteBuffer.allocate(6).apply {
            put(1)
            putShort(varpId.toShort())
            put(lsb.toByte())
            put(msb.toByte())
            put(0)
        }
    } else {
        ByteBuffer.allocate(1).apply { put(0) }
    }

    companion object : ConfigFileCompanion<VarbitConfig>() {
        override val id = 14

        @ExperimentalUnsignedTypes
        override fun decode(id: Int, buffer: ByteBuffer): VarbitConfig {
            var varpId: UShort = 0u
            var lsb: UByte = 0u
            var msb: UByte = 0u

            decoder@ while (true) {
                val opcode = buffer.get().toInt() and 0xFF
                when(opcode) {
                    0 -> break@decoder
                    1 -> {
                        varpId = buffer.uShort
                        lsb = buffer.uByte
                        msb = buffer.uByte
                    }
                }
            }
            return VarbitConfig(id, varpId, lsb, msb)
        }
    }
}