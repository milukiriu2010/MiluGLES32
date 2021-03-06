package milu.kiriu2010.milugles32.w8x.w81

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs
import milu.kiriu2010.math.MyMathUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

// ------------------------------------
// シェーダ(VBOを逐次更新)
// ------------------------------------
// https://wgld.org/d/webgl/w081.html
// ------------------------------------
class W81Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            
            uniform   mat4  u_matMVP;
            uniform   float u_pointSize;

            void main() {
                gl_Position  = u_matMVP   * vec4(a_Position, 1.0);
                gl_PointSize = u_pointSize;
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp   float;
            
            out vec4 o_FragColor;

            void main() {
                o_FragColor = vec4(0.0, 0.7, 1.0, 1.0);
            }
            """.trimIndent()

    override fun loadShader(): ES32MgShader {
        // 頂点シェーダを生成
        svhandle = MyGLES32Func.loadShader(GLES32.GL_VERTEX_SHADER, scv)
        // フラグメントシェーダを生成
        sfhandle = MyGLES32Func.loadShader(GLES32.GL_FRAGMENT_SHADER, scf)

        // プログラムオブジェクトの生成とリンク
        programHandle = MyGLES32Func.createProgram(svhandle,sfhandle)

        // ----------------------------------------------
        // uniformハンドルに値をセット
        // ----------------------------------------------
        hUNI = IntArray(2)

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(描画点の大きさ)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_pointSize")
        MyGLES32Func.checkGlError("u_pointSize:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_pointSize: Float,
             t0: Float) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // 点を更新
        model.bufPos.position(0)
        val size = model.bufPos.limit()
        // size=3267
        //Log.d(javaClass.simpleName,"size[$size]")
        val buf = ByteBuffer.allocateDirect(model.datPos.toArray().size*4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(model.datPos.toFloatArray())
                position(0)
            }
        }
        (0 until size step 3).forEach { i ->
            val t = MyMathUtil.cosf(t0)
            val x = model.datPos[i]
            val y = model.datPos[i+1]
            val z = model.datPos[i+2]
            buf.put(i,x+x*t)
            buf.put(i+1,y+y*t)
            buf.put(i+2,z+z*t)
        }
        buf.position(0)
        GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER,0,buf.capacity()*4,buf)
        MyGLES32Func.checkGlError("a_Position:glBufferSubData",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(描画点の大きさ)
        GLES32.glUniform1f(hUNI[1], u_pointSize)
        MyGLES32Func.checkGlError("u_pointSize",this,model)

        // モデルを描画
        GLES32.glDrawArrays(GLES32.GL_POINTS,0,model.datPos.size/3)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
