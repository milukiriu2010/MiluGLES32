package milu.kiriu2010.gui.model.d2

import milu.kiriu2010.gui.model.MgModelAbs

// --------------------------------------------------
// 板ポリゴン
// --------------------------------------------------
// 2019.04.30  51:XZ平面(右回り)
// 2019.05.01  53:XY平面(左回り)
// 2019.05.07   1:XY平面(右回り)にテクスチャ座標付与
// 2019.05.08  62:XY平面(左+右回り)
// --------------------------------------------------
class Board00Model: MgModelAbs() {

    override fun createPath( opt: Map<String,Float> ) {
        datPos.clear()
        datNor.clear()
        datCol.clear()
        datTxc.clear()
        datIdx.clear()

        val pattern = opt["pattern"]?.toInt() ?: 1

        when ( pattern ) {
            // XY平面(右回り)
            1 -> createPathPattern1(opt)
            // XY平面(右回り)(アルファブレンディング)
            29 -> createPathPattern29(opt)
            // XY平面(左回り)
            53 -> createPathPattern53(opt)
            // XY平面(左+右回り)
            62 -> createPathPattern62(opt)
            // XZ平面(左回り)
            49 -> createPathPattern49(opt)
            // XZ平面(右回り)
            51 -> createPathPattern51(opt)
            // XY平面(左回り)画像貼り付け
            100 -> createPathPattern100(opt)
            // XY平面
            101 -> createPathPattern101(opt)
            else -> createPathPattern1(opt)
        }

        // バッファ割り当て
        allocateBuffer()
    }

    // XY平面(右回り)
    //  w26,w27,w35,w40
    private fun createPathPattern1(opt: Map<String, Float>) {
        val scale = opt["scale"] ?: 1f
        val color = FloatArray(4)
        color[0] = opt["colorR"] ?: 1f
        color[1] = opt["colorG"] ?: 1f
        color[2] = opt["colorB"] ?: 1f
        color[3] = opt["colorA"] ?: 1f

        // 頂点データ(Zを描くような順序)
        datPos.addAll(arrayListOf(-scale, scale,0f))
        datPos.addAll(arrayListOf( scale, scale,0f))
        datPos.addAll(arrayListOf(-scale,-scale,0f))
        datPos.addAll(arrayListOf( scale,-scale,0f))

        // 法線データ
        (0..3).forEach {
            datNor.addAll(arrayListOf(0f,0f,1f))
        }

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
        datIdx.addAll(arrayListOf<Short>(0,1,2))
        datIdx.addAll(arrayListOf<Short>(3,2,1))
    }

    // XY平面(右回り)
    //   アルファブレンディング
    //   ステンシルバッファ
    // w29,w30,w38,w86
    private fun createPathPattern29(opt: Map<String, Float>) {
        // 頂点データ
        datPos.addAll(arrayListOf(-1f, 1f,0f))
        datPos.addAll(arrayListOf( 1f, 1f,0f))
        datPos.addAll(arrayListOf(-1f,-1f,0f))
        datPos.addAll(arrayListOf( 1f,-1f,0f))

        // 法線データ
        (0..3).forEach {
            datNor.addAll(arrayListOf(0f,0f,1f))
        }

        // 色データ
        datCol.addAll(arrayListOf(1f,0f,0f,1f))
        datCol.addAll(arrayListOf(0f,1f,0f,1f))
        datCol.addAll(arrayListOf(0f,0f,1f,1f))
        datCol.addAll(arrayListOf(1f,1f,1f,1f))

        // テクスチャ座標
        datTxc.addAll(arrayListOf(0f,0f))
        datTxc.addAll(arrayListOf(1f,0f))
        datTxc.addAll(arrayListOf(0f,1f))
        datTxc.addAll(arrayListOf(1f,1f))

        // 頂点インデックス
        datIdx.addAll(arrayListOf(0,1,2))
        datIdx.addAll(arrayListOf(3,2,1))
    }

    // XY平面(左回り)
    //  w53,w54,w55,w65,w66,w67,w68
    private fun createPathPattern53(opt: Map<String, Float>) {
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

        // 法線データ
        (0..3).forEach {
            datNor.addAll(arrayListOf(0f,0f,1f))
        }

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
        datIdx.addAll(arrayListOf<Short>(2,3,1))
    }

    // XY平面(左+右回り)
    //  w62
    private fun createPathPattern62(opt: Map<String, Float>) {
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

        // 法線データ
        (0..3).forEach {
            datNor.addAll(arrayListOf(0f,0f,1f))
        }

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

    // XZ平面(左回り)
    //  w49
    private fun createPathPattern49(opt: Map<String, Float>) {
        val color = FloatArray(4)
        color[0] = opt["colorR"] ?: 1f
        color[1] = opt["colorG"] ?: 1f
        color[2] = opt["colorB"] ?: 1f
        color[3] = opt["colorA"] ?: 1f

        // 頂点データ
        datPos.addAll(arrayListOf(-1f, 0f,-1f))
        datPos.addAll(arrayListOf( 1f, 0f,-1f))
        datPos.addAll(arrayListOf(-1f, 0f, 1f))
        datPos.addAll(arrayListOf( 1f, 0f, 1f))

        // 法線データ
        (0..3).forEach {
            datNor.addAll(arrayListOf(0f,1f,0f))
        }

        // 色データ
        (0..3).forEach {
            datCol.addAll(arrayListOf<Float>(color[0],color[1],color[2],color[3]))
        }

        // インデックスデータ
        datIdx.addAll(arrayListOf<Short>(0,1,2))
        datIdx.addAll(arrayListOf<Short>(3,2,1))
    }

    // XZ平面(右回り)
    //  w51
    private fun createPathPattern51(opt: Map<String, Float>) {
        val color = FloatArray(4)
        color[0] = opt["colorR"] ?: 1f
        color[1] = opt["colorG"] ?: 1f
        color[2] = opt["colorB"] ?: 1f
        color[3] = opt["colorA"] ?: 1f

        // 頂点データ
        datPos.addAll(arrayListOf(-1f, 0f,-1f))
        datPos.addAll(arrayListOf( 1f, 0f,-1f))
        datPos.addAll(arrayListOf(-1f, 0f, 1f))
        datPos.addAll(arrayListOf( 1f, 0f, 1f))

        // 法線データ
        (0..3).forEach {
            datNor.addAll(arrayListOf(0f,1f,0f))
        }

        // 色データ
        (0..3).forEach {
            datCol.addAll(arrayListOf<Float>(color[0],color[1],color[2],color[3]))
        }

        // インデックスデータ
        datIdx.addAll(arrayListOf<Short>(0,2,1))
        datIdx.addAll(arrayListOf<Short>(3,1,2))
    }

    // XY平面(左回り)画像貼り付け
    //  a03,a06,a07
    private fun createPathPattern100(opt: Map<String, Float>) {
        // 頂点データ
        datPos.addAll(arrayListOf( 1f, 1f,0f))
        datPos.addAll(arrayListOf(-1f, 1f,0f))
        datPos.addAll(arrayListOf( 1f,-1f,0f))
        datPos.addAll(arrayListOf(-1f,-1f,0f))

        // 法線データ
        (0..3).forEach {
            datNor.addAll(arrayListOf(0f,0f,1f))
        }

        // テクスチャ座標
        datTxc.addAll(arrayListOf(1f,0f))
        datTxc.addAll(arrayListOf(0f,0f))
        datTxc.addAll(arrayListOf(1f,1f))
        datTxc.addAll(arrayListOf(0f,1f))

        // インデックスデータ
        datIdx.addAll(arrayListOf<Short>(0,1,2))
        datIdx.addAll(arrayListOf<Short>(2,1,3))
    }

    // XY平面
    //  a13
    private fun createPathPattern101(opt: Map<String, Float>) {
        // 頂点データ
        datPos.addAll(arrayListOf( 1f, 1f,0f))
        datPos.addAll(arrayListOf(-1f, 1f,0f))
        datPos.addAll(arrayListOf( 1f,-1f,0f))
        datPos.addAll(arrayListOf(-1f,-1f,0f))

        // 法線データ
        (0..3).forEach {
            datNor.addAll(arrayListOf(0f,0f,1f))
        }

        // テクスチャ座標
        datTxc.addAll(arrayListOf(0f,0f))
        datTxc.addAll(arrayListOf(1f,0f))
        datTxc.addAll(arrayListOf(0f,1f))
        datTxc.addAll(arrayListOf(1f,1f))

        // インデックスデータ
        datIdx.addAll(arrayListOf<Short>(0,1,2))
        datIdx.addAll(arrayListOf<Short>(2,1,3))
    }
}