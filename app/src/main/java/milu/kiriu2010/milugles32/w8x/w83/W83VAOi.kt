package milu.kiriu2010.milugles32.w8x.w83

import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.color.MgColor
import milu.kiriu2010.gui.model.MgModelAbs
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs
import milu.kiriu2010.math.MyMathUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.floor

// --------------------------------
// VAO
// VBO
//   0:インデックス
// IBO
// --------------------------------
// ES3.2用
// --------------------------------
class W83VAOi: ES32VAOAbs() {

    override fun makeVIBO(modelAbs: MgModelAbs) {
        //Log.d(javaClass.simpleName,"makeVIBO:${modelAbs.javaClass.simpleName}")
        model = modelAbs

        // ------------------------------------------------
        // VAOの生成
        // ------------------------------------------------
        hVAO = IntArray(1)
        GLES32.glGenVertexArrays(1,hVAO,0)
        MyGLES32Func.checkGlError("glGenVertexArrays")
        GLES32.glBindVertexArray(hVAO[0])
        MyGLES32Func.checkGlError("glBindVertexArray")

        // ------------------------------------------------
        // VBOの生成
        // ------------------------------------------------
        hVBO = IntArray(1)
        GLES32.glGenBuffers(1, hVBO,0)
        MyGLES32Func.checkGlError("hVBO:glGenBuffers")

        // 頂点インデックス
        model.bufPos.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,hVBO[0])
        MyGLES32Func.checkGlError("a_Index:glBindBuffer")
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,model.bufPos.capacity()*4, model.bufPos,GLES32.GL_STATIC_DRAW)
        MyGLES32Func.checkGlError("a_Index:glBufferData")
        GLES32.glEnableVertexAttribArray(0)
        MyGLES32Func.checkGlError("a_Index:glEnableVertexAttribArray")
        GLES32.glVertexAttribPointer(0,1,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("a_Index:glVertexAttribPointer")
        //GLES32.glVertexAttribPointer(0,3,GLES32.GL_FLOAT,false,3*4,0)

        // リソース解放
        GLES32.glBindVertexArray(0)
        MyGLES32Func.checkGlError("glBindVertexArray")
        //GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,0)
        //GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER,0)
    }
}
