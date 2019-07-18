package milu.kiriu2010.milugles32.es32x02.a15

import android.graphics.Bitmap
import android.util.Log
import milu.kiriu2010.gui.model.MgModelAbs
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.abs
import kotlin.math.sqrt

// ----------------------------------------------
// 画像
// ----------------------------------------------
// 2019.07.18
// ----------------------------------------------
class A15Model: MgModelAbs() {
    // 速度バッファ
    lateinit var bufVec: FloatBuffer
    // 速度データ
    var datVec = arrayListOf<Float>()

    fun createPath(bmp: Bitmap) {
        datPos.clear()
        datNor.clear()
        datCol.clear()
        datTxc.clear()
        datIdx.clear()
        datVec.clear()

        val byteBuf = ByteBuffer.allocate(bmp.byteCount)
        bmp.copyPixelsToBuffer(byteBuf)
        byteBuf.position(0)
        (0 until bmp.height).forEach { i ->
            val y = i.toFloat()/bmp.height.toFloat() * 2f - 1f
            val k = i * bmp.width
            (0 until bmp.width).forEach { j ->
                val x = j.toFloat()/bmp.width.toFloat() * 2f - 1f
                val l = (k+j) * 4
                datPos.addAll(arrayListOf(x,-y,0f))
                val m = sqrt(x*x+y*y)
                datVec.addAll(arrayListOf(x/m,-y/m,0f))
                // 妖怪人間ベムみたい
                datCol.add(abs(byteBuf[l].toFloat())/255f)
                datCol.add(abs(byteBuf[l+1].toFloat())/255f)
                datCol.add(abs(byteBuf[l+2].toFloat())/255f)
                //datCol.add(abs(byteBuf[l+3].toFloat())/255f)
                datCol.add(1f)
                /* 白黒だし、画像がみえない
                val bb = byteBuf[k+j]
                val r = (bb.toInt() and 0x00ff0000) shr 16
                val g = (bb.toInt() and 0x0000ff00) shr 8
                val b = (bb.toInt() and 0x000000ff)
                //val a = (bb.toInt() and 0xff000000)
                datCol.add(abs(r.toFloat())/255f)
                datCol.add(abs(g.toFloat())/255f)
                datCol.add(abs(b.toFloat())/255f)
                datCol.add(1f)
                */
                /* 赤
                datCol.add(1f)
                datCol.add(0f)
                datCol.add(0f)
                datCol.add(1f)
                */
            }
        }
        Log.d(javaClass.simpleName,"datPos[${datPos[0]}][${datPos[256]}]")
        Log.d(javaClass.simpleName,"datCol[${datCol[0]}][${datCol[256]}]")

        // バッファ割り当て
        allocateBuffer()

        // 速度バッファ
        bufVec = ByteBuffer.allocateDirect(datVec.toArray().size * 4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(datVec.toFloatArray())
                position(0)
            }
        }
    }

    override fun createPath( opt: Map<String,Float> ) {
    }
}
