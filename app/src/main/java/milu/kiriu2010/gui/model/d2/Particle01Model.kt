package milu.kiriu2010.gui.model.d2

import milu.kiriu2010.gui.model.MgModelAbs

// --------------------------------------
// パーティクル
// --------------------------------------
class Particle01Model: MgModelAbs() {

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
        val color = FloatArray(4)
        color[0] = opt["colorR"] ?: 1f
        color[1] = opt["colorG"] ?: 1f
        color[2] = opt["colorB"] ?: 1f
        color[3] = opt["colorA"] ?: 1f

        // 頂点データ
        datPos.addAll(arrayListOf(-1f, 1f,0f))
        datPos.addAll(arrayListOf( 1f, 1f,0f))
        datPos.addAll(arrayListOf(-1f,-1f,0f))
        datPos.addAll(arrayListOf( 1f,-1f,0f))

        // 色データ
        (0..3).forEach {
            datCol.addAll(arrayListOf<Float>(color[0],color[1],color[2],color[3]))
        }

        // テクスチャ座標
        datTxc.addAll(arrayListOf(0f,0f))
        datTxc.addAll(arrayListOf(1f,0f))
        datTxc.addAll(arrayListOf(0f,1f))
        datTxc.addAll(arrayListOf(1f,1f))

        // インデックスデータ
        datIdx.addAll(arrayListOf<Short>(0,2,1))
        datIdx.addAll(arrayListOf<Short>(1,2,3))
    }
}