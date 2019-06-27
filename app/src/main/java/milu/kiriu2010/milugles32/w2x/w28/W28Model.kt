package milu.kiriu2010.milugles32.w2x.w28

import milu.kiriu2010.gui.model.MgModelAbs

// ----------------------------------------
// テクスチャパラメータ用モデル
// ----------------------------------------
// https://wgld.org/d/webgl/w028.html
// ----------------------------------------
class W28Model: MgModelAbs() {
    override fun createPath(opt: Map<String, Float>) {
        // 頂点データ
        datPos.addAll(arrayListOf(-1f, 1f,0f))
        datPos.addAll(arrayListOf( 1f, 1f,0f))
        datPos.addAll(arrayListOf(-1f,-1f,0f))
        datPos.addAll(arrayListOf( 1f,-1f,0f))

        // 色データ
        (0..3).forEach {
            datCol.addAll(arrayListOf(1f,1f,1f,1f))
        }

        // テクスチャコードデータ
        datTxc.addAll(arrayListOf(-0.75f,-0.75f))
        datTxc.addAll(arrayListOf( 1.75f,-0.75f))
        datTxc.addAll(arrayListOf(-0.75f, 1.75f))
        datTxc.addAll(arrayListOf( 1.75f, 1.75f))

        // インデックスデータ
        datIdx.addAll(arrayListOf(0,1,2))
        datIdx.addAll(arrayListOf(3,2,1))

        allocateBuffer()
    }
}
