package milu.kiriu2010.milugles32.es32x02.a14

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VBOAbs

// ------------------------------------
// シェーダB
// Transform feedback
// ------------------------------------
// https://wgld.org/d/webgl2/w014.html
// ------------------------------------
class ES32a14ShaderB(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es

            layout (location = 0) in vec4  a_Position;
            layout (location = 1) in vec4  a_Color;

            uniform mat4  u_matVP;

            out vec4  v_Color;

            void main() {
                v_Color = a_Color;
                gl_Position  = u_matVP * a_Position;
                gl_PointSize = 10.0;
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es

            precision highp   float;

            in  vec4  v_Color;

            out vec4  o_FragColor;

            void main() {
                o_FragColor = v_Color;
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
        hUNI = IntArray(1)

        // uniform(u_matVP)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matVP")
        MyGLES32Func.checkGlError("u_matVP:glGetUniformLocation")

        return this
    }

    fun draw(vbo: ES32VBOAbs,
             u_matVP: FloatArray,
             bmpSize: Int) {
        //Log.d(javaClass.simpleName,"draw:${model.javaClass.simpleName}")
        val model = vbo.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)
        //Log.d(javaClass.simpleName,"draw:glUseProgram")

        // attribute(位置)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,vbo.hVBO[0])
        GLES32.glVertexAttribPointer(hATTR[0],4,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("BindVertexArray",this,model)
        //Log.d(javaClass.simpleName,"draw:glBindVertexArray")

        // attribute(色)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,vbo.hVBO[1])
        GLES32.glVertexAttribPointer(hATTR[1],4,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("BindVertexArray",this,model)
        //Log.d(javaClass.simpleName,"draw:glBindVertexArray")

        // uniform(u_matVP)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matVP,0)
        MyGLES32Func.checkGlError("u_matVP",this,model)

        // モデルを描画
        GLES32.glDrawArrays(GLES32.GL_POINTS, 0,bmpSize)
        MyGLES32Func.checkGlError("glDrawArrays",this,model)
        //Log.d(javaClass.simpleName,"draw:glDrawArrays")

        // VBO解放
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,0)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER,0)
    }
}
