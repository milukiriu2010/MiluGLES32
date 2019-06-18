package milu.kiriu2010.milugles32.es32x01.a09

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------
// UBO
// シェーダA
// ------------------------------------
// https://wgld.org/d/webgl2/w009.html
// ------------------------------------
class ES32a09ShaderA(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es

            layout (location = 0) in vec3  a_Position;

            layout (std140) uniform matrix {
                mat4 mvp;
            } u_mat;
            uniform  float u_scale;

            void main() {
                gl_Position = u_mat.mvp * vec4(a_Position * u_scale, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es

            precision highp   float;

            layout (std140) uniform material {
                vec4 base;
            } u_color;

            out vec4  o_FragColor;

            void main() {
                o_FragColor = u_color.base;
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
        // UBIハンドルに値をセット
        // ----------------------------------------------
        hUBI = IntArray(2)

        // UBO(matrix)
        hUBI[0] = GLES32.glGetUniformBlockIndex(programHandle,"matrix")
        MyGLES32Func.checkGlError("matrix:glGetUniformBlockIndex")
        GLES32.glUniformBlockBinding(programHandle,hUBI[0],0)
        MyGLES32Func.checkGlError("matrix:glUniformBlockBinding")

        // UBO(material)
        hUBI[1] = GLES32.glGetUniformBlockIndex(programHandle,"material")
        MyGLES32Func.checkGlError("material:glGetUniformBlockIndex")
        GLES32.glUniformBlockBinding(programHandle,hUBI[1],1)
        MyGLES32Func.checkGlError("material:glUniformBlockBinding")

        // ----------------------------------------------
        // uniformハンドルに値をセット
        // ----------------------------------------------
        hUNI = IntArray(1)

        // uniform(スケール)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_scale")
        MyGLES32Func.checkGlError("u_scale:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_scale: Float) {
        //Log.d(javaClass.simpleName,"draw:${model.javaClass.simpleName}")
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)
        //Log.d(javaClass.simpleName,"draw:glUseProgram")

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)
        //Log.d(javaClass.simpleName,"draw:glBindVertexArray")

        // uniform(スケール)
        GLES32.glUniform1f(hUNI[0],u_scale)
        MyGLES32Func.checkGlError("u_scale",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)
        MyGLES32Func.checkGlError("glDrawElements",this,model)

        // リソース解放
        //GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,0)
        //GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER,0)

        // VAO解放
        GLES32.glBindVertexArray(0)
        //MyGLES32Func.checkGlError2("draw:glBindVertexArray0",this,model)
    }
}
