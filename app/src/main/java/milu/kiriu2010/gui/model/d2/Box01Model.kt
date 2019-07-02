package milu.kiriu2010.gui.model.d2

import milu.kiriu2010.gui.model.MgModelAbs

// --------------------------------------
// ボックスモデル
// --------------------------------------
class Box01Model: MgModelAbs() {

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

    // XY平面(右回り)
    private fun createPathPattern1(opt: Map<String, Float>) {
        val color = FloatArray(4)
        color[0] = opt["colorR"] ?: 1f
        color[1] = opt["colorG"] ?: 1f
        color[2] = opt["colorB"] ?: 1f
        color[3] = opt["colorA"] ?: 1f

        // 頂点データ
        datPos.addAll(arrayListOf( 1f, 1f, 0f))
        datPos.addAll(arrayListOf( 1f, 0f, 0f))
        datPos.addAll(arrayListOf( 0f, 1f,-1f))
        datPos.addAll(arrayListOf( 0f, 0f,-1f))
        datPos.addAll(arrayListOf(-1f, 1f, 0f))
        datPos.addAll(arrayListOf(-1f, 0f, 0f))
        datPos.addAll(arrayListOf( 0f, 0f, 1f))

        // 色データ
        (0..6).forEach {
            datCol.addAll(arrayListOf<Float>(color[0],color[1],color[2],color[3]))
        }

        // インデックスデータ
        datIdx.addAll(arrayListOf<Short>(0,2,3))
        datIdx.addAll(arrayListOf<Short>(0,3,1))
        datIdx.addAll(arrayListOf<Short>(2,4,5))
        datIdx.addAll(arrayListOf<Short>(2,5,3))
        datIdx.addAll(arrayListOf<Short>(1,3,5))
        datIdx.addAll(arrayListOf<Short>(1,5,6))
    }

}