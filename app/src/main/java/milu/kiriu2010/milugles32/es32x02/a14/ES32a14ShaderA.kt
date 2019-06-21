package milu.kiriu2010.milugles32.es32x02.a14

import android.content.Context
import android.opengl.GLES32
import android.util.Log
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VBOAbs

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
        programHandle = MyGLES32Func.createProgramTransformFeedback(svhandle,sfhandle, arrayOf("gl_Position","v_Color"))

        // ----------------------------------------------
        // attributeハンドルに値をセット
        // ----------------------------------------------
        hATTR = intArrayOf(0,1)

        // 属性(位置)
        // attribute属性を有効にする
        // ここで呼ばないと描画されない
        GLES32.glEnableVertexAttribArray(hATTR[0])
        MyGLES32Func.checkGlError("a_Position:glEnableVertexAttribArray")
        // attribute属性を登録
        GLES32.glVertexAttribPointer(hATTR[0],4,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("a_Position:glVertexAttribPointer")

        // 属性(色)
        // attribute属性を有効にする
        // ここで呼ばないと描画されない
        GLES32.glEnableVertexAttribArray(hATTR[1])
        MyGLES32Func.checkGlError("a_Color:glEnableVertexAttribArray")
        // attribute属性を登録
        GLES32.glVertexAttribPointer(hATTR[1],4,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("a_Color:glVertexAttribPointer")

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

    fun draw(vboFrom: ES32VBOAbs,
             vboTo: ES32VBOAbs,
             u_time: Float,
             u_mouse: FloatArray,
             bmpSize: Int) {
        //Log.d(javaClass.simpleName,"draw:${model.javaClass.simpleName}")
        val modelFrom = vboFrom.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,modelFrom)
        //Log.d(javaClass.simpleName,"draw:glUseProgram")

        // attribute(位置)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,vboFrom.hVBO[0])
        GLES32.glEnableVertexAttribArray(hATTR[0])
        GLES32.glVertexAttribPointer(hATTR[0],4,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("BindVertexArray",this,modelFrom)
        //Log.d(javaClass.simpleName,"draw:glBindVertexArray")

        // attribute(色)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,vboFrom.hVBO[1])
        GLES32.glEnableVertexAttribArray(hATTR[1])
        GLES32.glVertexAttribPointer(hATTR[1],4,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("BindVertexArray",this,modelFrom)
        //Log.d(javaClass.simpleName,"draw:glBindVertexArray")

        // 書き込み先のVBOをバインド
        // Feedback:位置
        GLES32.glBindBufferBase(GLES32.GL_TRANSFORM_FEEDBACK_BUFFER,0,vboTo.hVBO[0])
        // Feedback:色
        GLES32.glBindBufferBase(GLES32.GL_TRANSFORM_FEEDBACK_BUFFER,1,vboTo.hVBO[1])

        // begin transform feedback
        GLES32.glEnable(GLES32.GL_RASTERIZER_DISCARD)
        GLES32.glBeginTransformFeedback(GLES32.GL_POINTS)

        // uniform(u_time)
        GLES32.glUniform1f(hUNI[0],u_time)
        MyGLES32Func.checkGlError("u_time",this,modelFrom)

        // uniform(u_mouse)
        GLES32.glUniform2fv(hUNI[1],1,u_mouse,0)
        MyGLES32Func.checkGlError("u_mouse",this,modelFrom)
        Log.d(javaClass.simpleName,"draw:u_time[${u_time}]x[${u_mouse[0]}]y[${u_mouse[1]}]")


        // モデルを描画
        GLES32.glDrawArrays(GLES32.GL_POINTS, 0,bmpSize)
        MyGLES32Func.checkGlError("glDrawArrays",this,modelFrom)
        //Log.d(javaClass.simpleName,"draw:glDrawArrays:${modelFrom.datIdx.size/4}")

        // end transform feedback
        GLES32.glDisable(GLES32.GL_RASTERIZER_DISCARD)
        GLES32.glEndTransformFeedback()
        GLES32.glBindBufferBase(GLES32.GL_TRANSFORM_FEEDBACK_BUFFER,0,0)
        GLES32.glBindBufferBase(GLES32.GL_TRANSFORM_FEEDBACK_BUFFER,1,0)

        // VBO解放
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,0)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER,0)
    }
}
