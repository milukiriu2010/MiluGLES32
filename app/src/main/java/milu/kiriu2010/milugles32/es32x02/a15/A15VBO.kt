package milu.kiriu2010.milugles32.es32x02.a15

import android.opengl.GLES32
import milu.kiriu2010.gui.model.MgModelAbs
import milu.kiriu2010.gui.vbo.es32.ES32VBOAbs

// ----------------------------------------------
// VBO(頂点位置＋色+速度)
// IBO
// ----------------------------------------------
// 2019.07.18
// ----------------------------------------------
class A15VBO: ES32VBOAbs() {

    override fun makeVIBO(modelAbs: MgModelAbs) {
        model = modelAbs
        val model15 = model as A15Model

        // ------------------------------------------------
        // VBOの生成
        // ------------------------------------------------
        hVBO = IntArray(3)
        GLES32.glGenBuffers(3, hVBO,0)

        // 位置
        model.bufPos.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,hVBO[0])
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,model.bufPos.capacity()*4, model.bufPos,usagePos)

        // 色
        model.bufCol.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,hVBO[1])
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,model.bufCol.capacity()*4, model.bufCol,usageCol)

        // 速度
        model15.bufVec.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,hVBO[2])
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,model15.bufVec.capacity()*4, model15.bufVec, GLES32.GL_DYNAMIC_COPY)

        // ------------------------------------------------
        // IBOの生成
        // ------------------------------------------------
        hIBO = IntArray(1)
        GLES32.glGenBuffers(1, hIBO,0)

        model.bufIdx.position(0)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER,hIBO[0])
        GLES32.glBufferData(GLES32.GL_ELEMENT_ARRAY_BUFFER,model.bufIdx.capacity()*2, model.bufIdx,GLES32.GL_STATIC_DRAW)

        // リソース解放
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,0)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER,0)
    }
}
