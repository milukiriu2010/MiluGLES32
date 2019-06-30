package milu.kiriu2010.milugles32.w5x.w58

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// --------------------------------------------
// シェーダ(正射影でレンダリング結果を合成)
//   レンダリングされた全てのシーンを合成する
// --------------------------------------------
// https://wgld.org/d/webgl/w058.html
// --------------------------------------------
class W58ShaderFinal(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec2  a_TextureCoord;
            
            uniform   mat4  u_matMVP;
            
            out  vec2  v_TextureCoord;

            void main() {
                v_TextureCoord = a_TextureCoord;
                gl_Position    = u_matMVP   * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            // フルカラーの本来のレンダリング結果
            uniform   sampler2D   u_Texture1;
            // ブラーをかけた反射光のレンダリング結果
            uniform   sampler2D   u_Texture2;
            uniform   int         u_glare;
            
            in  vec2  v_TextureCoord;
            
            out vec4  o_FragColor;

            void main() {
                vec4 destColor = texture(u_Texture1, v_TextureCoord);
                vec4 smpColor  = texture(u_Texture2, vec2(v_TextureCoord.s,1.0-v_TextureCoord.t));

                if ( bool(u_glare) ) {
                    // 反射光を２倍している
                    destColor += smpColor * 2.0;
                }
                o_FragColor = destColor;
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
        hUNI = IntArray(4)

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(テクスチャユニット１)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle, "u_Texture1")
        MyGLES32Func.checkGlError("u_Texture1:glGetUniformLocation")

        // uniform(テクスチャユニット２)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle, "u_Texture2")
        MyGLES32Func.checkGlError("u_Texture2:glGetUniformLocation")

        // uniform(グレアをかけるかどうか)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle, "u_glare")
        MyGLES32Func.checkGlError("u_glare:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_Texture1: Int,
             u_Texture2: Int,
             u_glare: Int) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(テクスチャユニット１)
        GLES32.glUniform1i(hUNI[1], u_Texture1)
        MyGLES32Func.checkGlError("u_Texture1",this,model)

        // uniform(テクスチャユニット２)
        GLES32.glUniform1i(hUNI[2], u_Texture2)
        MyGLES32Func.checkGlError("u_Texture2",this,model)

        // uniform(グレアをかけるかどうか)
        GLES32.glUniform1i(hUNI[3], u_glare)
        MyGLES32Func.checkGlError("u_glare",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
