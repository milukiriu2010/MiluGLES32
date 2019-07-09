package milu.kiriu2010.milugles32.w8x.w83

import android.opengl.GLES32
import milu.kiriu2010.gui.model.MgModelAbs
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

// --------------------------------------
// 頂点
// --------------------------------------
class W83ModelVertices: MgModelAbs() {

    var w = 512
    var h = 512

    override fun createPath( opt: Map<String,Float> ) {
        datPos.clear()
        datNor.clear()
        datCol.clear()
        datTxc.clear()
        datIdx.clear()

        val pattern = opt["pattern"]?.toInt() ?: 1

        when ( pattern ) {
            1 -> createPathPattern1(opt)
            else -> createPathPattern1(opt)
        }

        // バッファ割り当て
        allocateBuffer()
    }

    private fun createPathPattern1(opt: Map<String, Float>) {
        w = opt["w"]?.toInt() ?: 512
        h = opt["h"]?.toInt() ?: 512

        (0 until w).forEach { i ->
            (0 until h).forEach { j ->
                datPos.add((i*w+j).toFloat())
            }
        }
    }
}