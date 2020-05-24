package milu.kiriu2010.milugles32.w2x.w28

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ----------------------------------
// シェーダ(テクスチャパラメータ)
// ----------------------------------
// W27と同じ
// ----------------------------------
class W28Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
        """#version 320 es
                
            layout (location = 0) in vec3 a_Position;
            layout (location = 1) in vec4 a_Color;
            layout (location = 2) in vec2 a_TextureCoord;
            
            uniform   mat4 u_matMVP;
            
            out  vec4 v_Color;
            out  vec2 v_TextureCoord;

            void main() {
                v_Color        = a_Color;
                v_TextureCoord = a_TextureCoord;
                gl_Position    = u_matMVP * vec4(a_Position,1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
        """#version 320 es
            precision mediump float;

            uniform   sampler2D  u_Texture0;
            uniform   sampler2D  u_Texture1;
            
            in  vec4  v_Color;
            in  vec2  v_TextureCoord;
            
            out vec4  o_FragColor;

            void main() {
                // テクスチャデータからフラグメントの情報(色情報)を抜き出す
                vec4 smpColor0 = texture(u_Texture0, v_TextureCoord);
                vec4 smpColor1 = texture(u_Texture1, v_TextureCoord);
                // 頂点色と２つのテクスチャの色を掛け合わせて出力
                o_FragColor    = v_Color * smpColor0 * smpColor1;
            }
            """.trimIndent()

    override fun loadShader(): ES32MgShader {
        // 頂点シェーダを生成
        val svhandle = MyGLES32Func.loadShader(GLES32.GL_VERTEX_SHADER, scv)
        // フラグメントシェーダを生成
        val sfhandle = MyGLES32Func.loadShader(GLES32.GL_FRAGMENT_SHADER, scf)

        // プログラムオブジェクトの生成とリンク
        programHandle = MyGLES32Func.createProgram(svhandle,sfhandle)

        // ----------------------------------------------
        // uniformハンドルに値をセット
        // ----------------------------------------------
        hUNI = IntArray(3)

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // テクスチャユニット0
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_Texture0")
        MyGLES32Func.checkGlError("u_Texture0:glGetUniformLocation")

        // テクスチャユニット1
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_Texture1")
        MyGLES32Func.checkGlError("u_Texture1:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_Texture0: Int,
             u_Texture1: Int) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(テクスチャユニット0)
        GLES32.glUniform1i(hUNI[1],u_Texture0)
        MyGLES32Func.checkGlError("u_Texture0",this,model)

        // uniform(テクスチャユニット1)
        GLES32.glUniform1i(hUNI[2],u_Texture1)
        MyGLES32Func.checkGlError("u_Texture1",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
