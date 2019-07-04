package milu.kiriu2010.milugles32.w7x.w74

import milu.kiriu2010.gui.model.MgModelAbs

// --------------------------------------------------
// 板ポリゴン
// --------------------------------------------------
// 2019.04.30  51:XZ平面(右回り)
// 2019.05.01  53:XY平面(左回り)
// 2019.05.07   1:XY平面(右回り)にテクスチャ座標付与
// 2019.05.08  62:XY平面(左+右回り)
// --------------------------------------------------
class W74Model: MgModelAbs() {

    override fun createPath( opt: Map<String,Float> ) {
        datPos.clear()
        datNor.clear()
        datCol.clear()
        datTxc.clear()
        datIdx.clear()

        createPathPattern1(opt)

        // バッファ割り当て
        allocateBuffer()
    }

    private fun createPathPattern1(opt: Map<String, Float>) {
        // 頂点データ(Zを描くような順序)
        datPos.addAll(arrayListOf(-10f, 0f,-10f))
        datPos.addAll(arrayListOf( 10f, 0f,-10f))
        datPos.addAll(arrayListOf(-10f, 0f, 10f))
        datPos.addAll(arrayListOf( 10f, 0f, 10f))

        // 色データ
        (0..3).forEach {
            datCol.addAll(arrayListOf<Float>(1f,1f,1f,1f))
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