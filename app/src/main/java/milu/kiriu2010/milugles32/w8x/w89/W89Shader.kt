package milu.kiriu2010.milugles32.w8x.w89

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// --------------------------------------
// シェーダ(スフィア環境マッピング)
// --------------------------------------
// https://wgld.org/d/webgl/w089.html
// --------------------------------------
class W89Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            
            uniform   mat4  u_matMVP;
            uniform   mat4  u_matN;
            
            out  vec3  v_Normal;

            void main() {
                v_Normal    = normalize((u_matN*vec4(a_Normal,0.0)).xyz);
                gl_Position = u_matMVP * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform sampler2D u_Texture;
            
            in  vec3  v_Normal;
            
            out vec4  o_FragColor;

            void main() {
                vec2 texCoord = (v_Normal.xy + 1.0)/2.0;
                vec4 smpColor = texture(u_Texture,vec2(texCoord.s,1.0-texCoord.t));
                o_FragColor   = smpColor;
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
        hUNI = IntArray(3)

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform()
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matN")
        MyGLES32Func.checkGlError("u_matN:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_matN: FloatArray) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform()
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matN,0)
        MyGLES32Func.checkGlError("u_matN",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
