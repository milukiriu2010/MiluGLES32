package milu.kiriu2010.milugles32.w8x.w82

import android.opengl.GLES32
import milu.kiriu2010.gui.model.MgModelAbs
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

// --------------------------------------
// パーティクル
// --------------------------------------
class W82Model: MgModelAbs() {

    // 頂点の配置解像度X
    private val resolutionX = 100
    // 頂点の配置解像度Y
    private val resolutionY = 100
    // 頂点間の間隔X
    private val intervalX = 1f/resolutionX.toFloat()
    // 頂点間の間隔Y
    private val intervalY = 1f/resolutionY.toFloat()
    // 頂点の個数
    val verticesCount = resolutionX*resolutionY
    // 頂点の進行方向ベクトル
    val datVec = arrayListOf<Float>()

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

        (0 until resolutionX).forEach { i ->
            (0 until resolutionY).forEach { j ->
                // 頂点データ
                val x = i.toFloat() * intervalX * 2f - 1f
                val y = j.toFloat() * intervalY * 2f - 1f
                datPos.addAll(arrayListOf(x,y))
                // 頂点のベクトル
                datVec.addAll(arrayListOf(0f,0f))
            }
        }
    }

    // 点を更新する
    fun updatePoint(isRunning: Boolean,velocity: Float,speed: Float,mx: Float,my: Float) {
        bufPos.position(0)
        val size = bufPos.limit()
        val buf = ByteBuffer.allocateDirect(datPos.toArray().size*4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(datPos.toFloatArray())
                position(0)
            }
        }

        (0 until resolutionX).forEach { i ->
            val k = i * resolutionX
            (0 until resolutionY).forEach { j ->
                val l = (k+j)*2

                val p0 = buf.get(l)
                val p1 = buf.get(l+1)
                val v0 = datVec[l]
                val v1 = datVec[l+1]

                // マウスが押下されている場合ベクトルを更新する
                if (isRunning) {
                    val p = vectorUpdate(p0,p1,mx,my,v0,v1)
                    datVec[l] = p[0]
                    datVec[l+1] = p[1]
                }

                buf.put(l,p0+velocity*speed*v0)
                buf.put(l+1,p1+velocity*speed*v1)
            }
        }

        buf.position(0)
        GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER,0,buf.capacity()*4,buf)
    }

    // ベクトル演算
    private fun vectorUpdate(x: Float, y: Float, tx: Float, ty: Float, vx: Float, vy: Float): FloatArray {
        var px = tx - x
        var py = ty - y

        var r = sqrt(px*px+py*py)*5f
        if ( r != 0f ) {
            px /= r
            py /= r
        }

        px += vx
        py += vy
        r = sqrt(px*px+py+py)
        if ( r != 0f ) {
            px /= r
            py /= r
        }

        return floatArrayOf(px,py)
    }
}