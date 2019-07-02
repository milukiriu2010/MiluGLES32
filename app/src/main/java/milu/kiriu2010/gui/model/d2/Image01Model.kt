package milu.kiriu2010.gui.model.d2

import android.graphics.Bitmap
import milu.kiriu2010.gui.basic.MyPointF
import milu.kiriu2010.gui.model.MgModelAbs
import java.nio.ByteBuffer
import kotlin.math.pow

// ----------------------------------------------
// 画像
// ----------------------------------------------
// 2019.06.20
// ----------------------------------------------
class Image01Model: MgModelAbs() {

    fun createPath(bmp: Bitmap, isEmpty: Boolean ) {
        datPos.clear()
        datNor.clear()
        datCol.clear()
        datTxc.clear()
        datIdx.clear()

        val byteBuf = ByteBuffer.allocate(bmp.byteCount)
        bmp.copyPixelsToBuffer(byteBuf)
        (0 until bmp.height).forEach { i ->
            val y = i.toFloat()/bmp.height.toFloat() * 2f - 1f
            val k = i * bmp.width
            (0 until bmp.width).forEach { j ->
                val x = j.toFloat()/bmp.width.toFloat() * 2f - 1f
                val l = (k+j) * 4
                when ( isEmpty ) {
                    true -> {
                        datPos.addAll(arrayListOf(0f,0f,0f,0f))
                        datCol.addAll(arrayListOf(0f,0f,0f,0f))
                    }
                    false -> {
                        datPos.addAll(arrayListOf(x,-y,0f,1f))
                        datCol.add(byteBuf[l].toFloat()/255f)
                        datCol.add(byteBuf[l+1].toFloat()/255f)
                        datCol.add(byteBuf[l+2].toFloat()/255f)
                        datCol.add(byteBuf[l+3].toFloat()/255f)
                    }
                }
                /*
                datIdx.add((k+j).toShort())
                datIdx.add((k+j+1).toShort())
                datIdx.add((k+j+2).toShort())
                datIdx.add((k+j+3).toShort())
                */
            }
        }

        // バッファ割り当て
        allocateBuffer()
    }

    override fun createPath( opt: Map<String,Float> ) {
    }
}
