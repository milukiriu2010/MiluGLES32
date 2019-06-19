package milu.kiriu2010.milugles32.es32x02.a14

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------
// シェーダA
// Transform feedback
// ------------------------------------
// https://wgld.org/d/webgl2/w014.html
// ------------------------------------
class ES32a14ShaderA(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es

            layout (location = 0) in vec4  a_Position;
            layout (location = 1) in vec4  a_Color;

            uniform float u_time;
            uniform vec2  u_mouse;

            out vec4  v_Color;

            void main() {
                vec2 p = u_mouse - a_Position.xy;
                float z = cos(length(p*20.0)-u_time) * 0.1;
                gl_Position = a_Position + vec4(0.0, 0.0, z, 0.0);
                v_Color = a_Color;
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es

            precision highp   float;

            out vec4  o_FragColor;

            void main() {
                o_FragColor = vec4(1.0);
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

        // uniform(u_time)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_time")
        MyGLES32Func.checkGlError("u_time:glGetUniformLocation")

        // uniform(u_mouse)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle, "u_mouse")
        MyGLES32Func.checkGlError("u_mouse:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_time: Float,
             u_mouse: FloatArray) {
        //Log.d(javaClass.simpleName,"draw:${model.javaClass.simpleName}")
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)
        //Log.d(javaClass.simpleName,"draw:glUseProgram")

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)
        //Log.d(javaClass.simpleName,"draw:glBindVertexArray")

        // uniform(u_time)
        GLES32.glUniform1f(hUNI[0],u_time)
        MyGLES32Func.checkGlError("u_time",this,model)

        // uniform(u_mouse)
        GLES32.glUniform2fv(hUNI[1],1,u_mouse,0)
        MyGLES32Func.checkGlError("u_mouse",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)
        MyGLES32Func.checkGlError("glDrawElements",this,model)
        //Log.d(javaClass.simpleName,"draw:glDrawElements")

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
