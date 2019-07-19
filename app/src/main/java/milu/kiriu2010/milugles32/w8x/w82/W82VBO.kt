package milu.kiriu2010.milugles32.w8x.w82

import android.opengl.GLES32
import milu.kiriu2010.gui.model.MgModelAbs
import milu.kiriu2010.gui.vbo.es32.ES32VBOAbs

// ----------------------------------------------
// VBO(頂点位置＋色)
// IBO
// ----------------------------------------------
class W82VBO: ES32VBOAbs() {

    override fun makeVIBO(modelAbs: MgModelAbs) {
        model = modelAbs

        // ------------------------------------------------
        // VBOの生成
        // ------------------------------------------------
        hVBO = IntArray(1)
        GLES32.glGenBuffers(1, hVBO,0)

        // 位置
        model.bufPos.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,hVBO[0])
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,model.bufPos.capacity()*4, model.bufPos,usagePos)

        // リソース解放
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,0)
        //GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER,0)
    }
}
