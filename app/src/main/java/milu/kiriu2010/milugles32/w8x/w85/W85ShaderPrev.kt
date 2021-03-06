package milu.kiriu2010.milugles32.w8x.w85

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------
// プレビュー
// MRT(Multiple Render Targets)
// ------------------------------------
// https://wgld.org/d/webgl/w085.html
// ------------------------------------
class W85ShaderPrev(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es

            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec2  a_TexCoord;
            
            uniform vec3 u_offset;

            out vec2  v_TexCoord;

            void main() {
                v_TexCoord  = a_TexCoord;
                gl_Position = vec4(a_Position*0.25 + u_offset, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es

            precision highp   float;
            
            uniform  sampler2D  u_texture;

            in  vec2  v_TexCoord;

            out vec4  o_FragColor;

            void main() {
                o_FragColor = texture(u_texture, v_TexCoord);
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

        // uniform()
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_offset")
        MyGLES32Func.checkGlError("u_offset:glGetUniformLocation")

        // uniform()
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_texture")
        MyGLES32Func.checkGlError("u_texture:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_offset: FloatArray,
             u_texture: Int) {
        //Log.d(javaClass.simpleName,"draw:${model.javaClass.simpleName}")
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)
        //Log.d(javaClass.simpleName,"draw:glUseProgram")

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)
        //Log.d(javaClass.simpleName,"draw:glBindVertexArray")

        // uniform()
        GLES32.glUniform3fv(hUNI[0],1,u_offset,0)
        MyGLES32Func.checkGlError("u_offset",this,model)
        //Log.d(javaClass.simpleName,"draw:u_offset")

        // uniform()
        GLES32.glUniform1i(hUNI[1],u_texture)
        MyGLES32Func.checkGlError("u_texture",this,model)
        //Log.d(javaClass.simpleName,"draw:u_texture")

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)
        MyGLES32Func.checkGlError("glDrawElements",this,model)
        //Log.d(javaClass.simpleName,"draw:glDrawElements")

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
