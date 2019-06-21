package milu.kiriu2010.gui.model.d2

import milu.kiriu2010.gui.model.MgModelAbs

// https://developer.android.com/training/graphics/opengl/shapes
class Square01Model: MgModelAbs() {

    override fun createPath( opt: Map<String,Float> ) {
        datPos.clear()
        datNor.clear()
        datCol.clear()
        datTxc.clear()
        datIdx.clear()

        val pattern = opt["pattern"]?.toInt() ?: 18

        when ( pattern ) {
            1 -> createPathPattern1(opt)
            18 -> createPathPattern18(opt)
            else -> createPathPattern18(opt)
        }

        // バッファ割り当て
        allocateBuffer()
    }

    // g001
    private fun createPathPattern1(opt: Map<String, Float>) {
        // 頂点データ
        datPos.addAll(arrayListOf(-1f, 1f,0f))
        datPos.addAll(arrayListOf( 1f, 1f,0f))
        datPos.addAll(arrayListOf(-1f,-1f,0f))
        datPos.addAll(arrayListOf( 1f,-1f,0f))

        // インデックスデータ
        datIdx.addAll(arrayListOf<Short>(0,2,1))
        datIdx.addAll(arrayListOf<Short>(1,2,3))
    }

    // w18,19
    private fun createPathPattern18(opt: Map<String, Float>) {
        // 頂点データ
        datPos.addAll(arrayListOf( 0f, 1f,0f))
        datPos.addAll(arrayListOf( 1f, 0f,0f))
        datPos.addAll(arrayListOf(-1f, 0f,0f))
        datPos.addAll(arrayListOf( 0f,-1f,0f))

        // 色データ
        datCol.addAll(arrayListOf(1f,0f,0f,1f))
        datCol.addAll(arrayListOf(0f,1f,0f,1f))
        datCol.addAll(arrayListOf(0f,0f,1f,1f))
        datCol.addAll(arrayListOf(1f,1f,1f,1f))

        // インデックスデータ
        datIdx.addAll(arrayListOf<Short>(0,1,2))
        datIdx.addAll(arrayListOf<Short>(1,2,3))
    }

}
