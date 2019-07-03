package milu.kiriu2010.milugles32.w7x.w75

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
//   0:頂点位置
//   1:法線
//   2:インスタンス位置
//   3:インスタンス色
// IBO
// --------------------------------
// ES3.2用
// --------------------------------
class W75VAO: ES32VAOAbs() {

    // インスタンスの数
    private val instanceCnt = 100

    // インスタンス位置データ
    private var datInstancePosition = arrayListOf<Float>()
    // インスタンス色データ
    private var datInstanceColor = arrayListOf<Float>()
    // インスタンス位置バッファ
    private lateinit var bufInstancePosition: FloatBuffer
    // インスタンス色バッファ
    private lateinit var bufInstanceColor: FloatBuffer

    init {
        (0 until instanceCnt).forEach { i ->
            // 頂点座標
            var j = i%10
            var k = floor(i.toFloat()*0.1f)*0.5f + 0.5f
            var t = 3600f*j.toFloat()/instanceCnt.toFloat()
            datInstancePosition.add(MyMathUtil.cosf(t)*k)
            datInstancePosition.add(0f)
            datInstancePosition.add(MyMathUtil.sinf(t)*k)
            // 頂点色
            var hsv = MgColor.hsva(3600*i/instanceCnt,1f,1f,1f)
            datInstanceColor.add(hsv[0])
            datInstanceColor.add(hsv[1])
            datInstanceColor.add(hsv[2])
            datInstanceColor.add(hsv[3])
        }

        bufInstancePosition = ByteBuffer.allocateDirect(datInstancePosition.toArray().size*4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(datInstancePosition.toFloatArray())
                position(0)
            }
        }

        bufInstanceColor = ByteBuffer.allocateDirect(datInstanceColor.toArray().size*4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(datInstanceColor.toFloatArray())
                position(0)
            }
        }
    }

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
        hVBO = IntArray(4)
        GLES32.glGenBuffers(4, hVBO,0)
        MyGLES32Func.checkGlError("hVBO:glGenBuffers")

        // 位置
        modelAbs.bufPos.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,hVBO[0])
        MyGLES32Func.checkGlError("a_Position:glBindBuffer")
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,modelAbs.bufPos.capacity()*4, modelAbs.bufPos,usagePos)
        MyGLES32Func.checkGlError("a_Position:glBufferData")
        GLES32.glEnableVertexAttribArray(0)
        MyGLES32Func.checkGlError("a_Position:glEnableVertexAttribArray")
        GLES32.glVertexAttribPointer(0,3,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("a_Position:glVertexAttribPointer")
        //GLES32.glVertexAttribPointer(0,3,GLES32.GL_FLOAT,false,3*4,0)

        // 法線
        modelAbs.bufNor.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,hVBO[1])
        MyGLES32Func.checkGlError("a_Normal:glBindBuffer")
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,modelAbs.bufNor.capacity()*4, modelAbs.bufNor,GLES32.GL_STATIC_DRAW)
        MyGLES32Func.checkGlError("a_Normal:glBufferData")
        GLES32.glEnableVertexAttribArray(1)
        MyGLES32Func.checkGlError("a_Normal:glEnableVertexAttribArray")
        GLES32.glVertexAttribPointer(1,3,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("a_Normal:glVertexAttribPointer")
        //GLES32.glVertexAttribPointer(1,3,GLES32.GL_FLOAT,false,3*4,0)

        // インスタンス位置
        bufInstancePosition.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,hVBO[2])
        MyGLES32Func.checkGlError("a_InstancePosition:glBindBuffer")
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,bufInstancePosition.capacity()*4, bufInstancePosition,GLES32.GL_STATIC_DRAW)
        MyGLES32Func.checkGlError("a_InstancePosition:glBufferData")
        GLES32.glEnableVertexAttribArray(2)
        MyGLES32Func.checkGlError("a_InstancePosition:glEnableVertexAttribArray")
        GLES32.glVertexAttribPointer(2,3,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("a_InstancePosition:glVertexAttribPointer")
        GLES32.glVertexAttribDivisor(2,1)
        MyGLES32Func.checkGlError("a_InstancePosition:glVertexAttribDivisor")

        // オフセット
        bufInstanceColor.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,hVBO[3])
        MyGLES32Func.checkGlError("a_InstanceColor:glBindBuffer")
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,bufInstanceColor.capacity()*4, bufInstanceColor,GLES32.GL_STATIC_DRAW)
        MyGLES32Func.checkGlError("a_InstanceColor:glBufferData")
        GLES32.glEnableVertexAttribArray(3)
        MyGLES32Func.checkGlError("a_InstanceColor:glEnableVertexAttribArray")
        GLES32.glVertexAttribPointer(3,4,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("a_InstanceColor:glVertexAttribPointer")
        GLES32.glVertexAttribDivisor(3,1)
        MyGLES32Func.checkGlError("a_InstanceColor:glVertexAttribDivisor")

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
