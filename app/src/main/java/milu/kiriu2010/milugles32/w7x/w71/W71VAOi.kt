package milu.kiriu2010.milugles32.w7x.w71

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
class W71VAOi: ES32VAOAbs() {

    // 頂点インデックス:データ
    var datIndex = arrayListOf<Float>()
    // 頂点インデックス:バッファ
    lateinit var bufIndex: FloatBuffer

    override fun makeVIBO(modelAbs: MgModelAbs) {
        //Log.d(javaClass.simpleName,"makeVIBO:${modelAbs.javaClass.simpleName}")
        model = modelAbs

        // 頂点の個数分の連番を配列に格納する
        val datIdxSize = model.datPos.size/3
        (0 until datIdxSize).forEach {
            datIndex.add(it.toFloat())
        }

        bufIndex = ByteBuffer.allocateDirect(datIndex.toArray().size*4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(datIndex.toFloatArray())
                position(0)
            }
        }

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
        bufIndex.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,hVBO[0])
        MyGLES32Func.checkGlError("a_Index:glBindBuffer")
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,bufIndex.capacity()*4, bufIndex,GLES32.GL_STATIC_DRAW)
        MyGLES32Func.checkGlError("a_Index:glBufferData")
        GLES32.glEnableVertexAttribArray(0)
        MyGLES32Func.checkGlError("a_Index:glEnableVertexAttribArray")
        GLES32.glVertexAttribPointer(0,1,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("a_Index:glVertexAttribPointer")
        //GLES32.glVertexAttribPointer(0,3,GLES32.GL_FLOAT,false,3*4,0)

        // ------------------------------------------------
        // IBOの生成
        // ------------------------------------------------
        hIBO = IntArray(1)
        GLES32.glGenBuffers(1, hIBO,0)
        MyGLES32Func.checkGlError("hIBO:glGenBuffers")

        modelAbs.bufIdx.position(0)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER,hIBO[0])
        MyGLES32Func.checkGlError("idx:glBindBuffer")
        GLES32.glBufferData(GLES32.GL_ELEMENT_ARRAY_BUFFER,modelAbs.bufIdx.capacity()*2, modelAbs.bufIdx,GLES32.GL_STATIC_DRAW)
        MyGLES32Func.checkGlError("idx:glBufferData")

        // リソース解放
        GLES32.glBindVertexArray(0)
        MyGLES32Func.checkGlError("glBindVertexArray")
        //GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,0)
        //GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER,0)
    }
}
