package milu.kiriu2010.gui.model.d2

import milu.kiriu2010.gui.model.MgModelAbs


// 線
class Line01Model: MgModelAbs() {

    override fun createPath( opt: Map<String,Float> ){
        // 頂点データ
        datPos.addAll(arrayListOf(-1f,-1f,0f))
        datPos.addAll(arrayListOf( 1f,-1f,0f))
        datPos.addAll(arrayListOf(-1f, 1f,0f))
        datPos.addAll(arrayListOf( 1f, 1f,0f))

        // 色データ
        datCol.addAll(arrayListOf(1f,1f,1f,1f))
        datCol.addAll(arrayListOf(1f,0f,0f,1f))
        datCol.addAll(arrayListOf(0f,1f,0f,1f))
        datCol.addAll(arrayListOf(0f,0f,1f,1f))

        allocateBuffer()
    }
}
