package milu.kiriu2010.milugles32.es32x02.a15

import android.content.Context
import android.opengl.GLES32
import android.util.Log
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VBOAbs

// ------------------------------------
// シェーダB
// Transform feedback(GPGPU)
// ------------------------------------
// https://wgld.org/d/webgl2/w015.html
// ------------------------------------
class ES32a15ShaderB(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es

            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec4  a_Color;
            layout (location = 2) in vec3  a_Velocity;

            uniform mat4  u_matVP;
            uniform float u_move;

            out vec4  v_Color;

            void main() {
                v_Color = a_Color + vec4(a_Velocity, 0.0);
                gl_Position  = u_matVP * vec4(a_Position,1.0);
                gl_PointSize = 1.0 * (1.0 + u_move);
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
        hATTR = intArrayOf(0,1,2)

        // 属性(位置)
        // attribute属性を有効にする
        // ここで呼ばないと描画されない
        GLES32.glEnableVertexAttribArray(hATTR[0])
        MyGLES32Func.checkGlError("a_Position:glEnableVertexAttribArray")
        // attribute属性を登録
        GLES32.glVertexAttribPointer(hATTR[0],3,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("a_Position:glVertexAttribPointer")

        // 属性(色)
        // attribute属性を有効にする
        // ここで呼ばないと描画されない
        GLES32.glEnableVertexAttribArray(hATTR[1])
        MyGLES32Func.checkGlError("a_Color:glEnableVertexAttribArray")
        // attribute属性を登録
        GLES32.glVertexAttribPointer(hATTR[1],4,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("a_Color:glVertexAttribPointer")

        // 属性(速度)
        // attribute属性を有効にする
        // ここで呼ばないと描画されない
        GLES32.glEnableVertexAttribArray(hATTR[2])
        MyGLES32Func.checkGlError("a_Velocity:glEnableVertexAttribArray")
        // attribute属性を登録
        GLES32.glVertexAttribPointer(hATTR[2],3,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("a_Velocity:glVertexAttribPointer")

        // ----------------------------------------------
        // uniformハンドルに値をセット
        // ----------------------------------------------
        hUNI = IntArray(2)

        // uniform(u_matVP)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matVP")
        MyGLES32Func.checkGlError("u_matVP:glGetUniformLocation")

        // uniform(u_move)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle, "u_move")
        MyGLES32Func.checkGlError("u_move:glGetUniformLocation")

        return this
    }

    fun draw(vbo: ES32VBOAbs,
             u_matVP: FloatArray,
             u_move: Float,
             bmpSize: Int) {
        //Log.d(javaClass.simpleName,"draw:${model.javaClass.simpleName}")
        val model = vbo.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)
        //Log.d(javaClass.simpleName,"draw:glUseProgram")

        // attribute(位置)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,vbo.hVBO[0])
        GLES32.glVertexAttribPointer(hATTR[0],3,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("BindVertexArray",this,model)
        //Log.d(javaClass.simpleName,"draw:glBindVertexArray")
        //model.bufPos.position(0)
        //Log.d(javaClass.simpleName,"bufPos[0][${model.bufPos[0]}][${model.bufPos[256]}]")

        // attribute(色)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,vbo.hVBO[1])
        GLES32.glVertexAttribPointer(hATTR[1],4,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("BindVertexArray",this,model)
        //Log.d(javaClass.simpleName,"draw:glBindVertexArray")
        //model.bufCol.position(0)
        //Log.d(javaClass.simpleName,"bufCol[0][${model.bufCol[0]}][${model.bufCol[256]}]")

        // attribute(速度)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,vbo.hVBO[2])
        GLES32.glEnableVertexAttribArray(hATTR[2])
        GLES32.glVertexAttribPointer(hATTR[2],3,GLES32.GL_FLOAT,false,0,0)
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(u_matVP)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matVP,0)
        MyGLES32Func.checkGlError("u_matVP",this,model)

        // uniform(u_move)
        GLES32.glUniform1f(hUNI[1],u_move)
        MyGLES32Func.checkGlError("u_move",this,model)

        // モデルを描画
        GLES32.glDrawArrays(GLES32.GL_POINTS, 0,bmpSize)
        MyGLES32Func.checkGlError("glDrawArrays",this,model)
        //Log.d(javaClass.simpleName,"draw:glDrawArrays")

        // VBO解放
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,0)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER,0)
    }
}
