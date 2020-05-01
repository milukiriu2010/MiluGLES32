package milu.kiriu2010.gui.model.d2

import milu.kiriu2010.gui.model.MgModelAbs

// --------------------------------------
// レイモデル(というよりXYZ軸)
// --------------------------------------
class Ray01Model: MgModelAbs() {

    override fun createPath( opt: Map<String,Float> ) {
        datPos.clear()
        datNor.clear()
        datCol.clear()
        datTxc.clear()
        datIdx.clear()

        val pattern = opt["pattern"]?.toInt() ?: 1

        when ( pattern ) {
            1 -> createPathPattern1()
            else -> createPathPattern1()
        }

        // バッファ割り当て
        allocateBuffer()
    }

    private fun createPathPattern1() {
        // 頂点データ
        datPos.addAll(arrayListOf(   0f, 0f, -2f))
        datPos.addAll(arrayListOf(   0f, 0f,  2f))
        datPos.addAll(arrayListOf( 0.1f, 0f,1.9f))
        datPos.addAll(arrayListOf(   0f, 0f,  2f))
        datPos.addAll(arrayListOf(-0.1f, 0f,1.9f))
        datPos.addAll(arrayListOf(   0f, 0f,  2f))
        datPos.addAll(arrayListOf(   0f, 0f,  0f))
        datPos.addAll(arrayListOf(   1f, 0f,  0f))
        datPos.addAll(arrayListOf(   0f, 0f,  0f))
        datPos.addAll(arrayListOf(   0f, 1f,  0f))

        // 色データ
        datCol.addAll(arrayListOf<Float>(0f,0f,1f,1f))
        datCol.addAll(arrayListOf<Float>(0f,0f,1f,1f))
        datCol.addAll(arrayListOf<Float>(0f,0f,1f,1f))
        datCol.addAll(arrayListOf<Float>(0f,0f,1f,1f))
        datCol.addAll(arrayListOf<Float>(0f,0f,1f,1f))
        datCol.addAll(arrayListOf<Float>(0f,0f,1f,1f))
        datCol.addAll(arrayListOf<Float>(1f,0f,0f,1f))
        datCol.addAll(arrayListOf<Float>(1f,0f,0f,1f))
        datCol.addAll(arrayListOf<Float>(0f,1f,0f,1f))
        datCol.addAll(arrayListOf<Float>(0f,1f,0f,1f))
    }

}