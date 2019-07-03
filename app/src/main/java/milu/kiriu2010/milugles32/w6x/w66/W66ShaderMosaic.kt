package milu.kiriu2010.milugles32.w6x.w66

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ---------------------------------------
// シェーダ(モザイク)
// ---------------------------------------
// https://wgld.org/d/webgl/w066.html
// ---------------------------------------
class W66ShaderMosaic(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec2  a_TextureCoord;
            
            uniform   mat4  u_matMVP;
            
            out  vec2  v_TexCoord;

            void main() {
                v_TexCoord  = a_TextureCoord;
                gl_Position = u_matMVP   * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform  sampler2D u_Texture;
            uniform  float     u_renderWH;
            
            in  vec2  v_TexCoord;
            
            out vec4  o_FragColor;

            // スクリーン上を区切るボックスのサイズを8x8に設定している
            void main() {
                // 512x512の描画領域を扱う場合
                //float tFrag = 1.0/512.0;
                //float nFrag = 1.0/64.0;

                float tFrag = 1.0/u_renderWH;
                // 色を出力する際、正規化するための係数
                //float nFrag = 1.0/(u_renderWH/8.0);
                float nFrag = 1.0/64.0;

                vec4  destColor = vec4(0.0);
                vec2  fc = vec2(gl_FragCoord.s, u_renderWH - gl_FragCoord.t);
                float offsetX = mod(fc.s, 8.0);
                float offsetY = mod(fc.t, 8.0);

                for (float x = 0.0; x <= 7.0; x += 1.0) {
                    for (float y = 0.0; y <= 7.0; y += 1.0) {
                        destColor += texture( u_Texture, (fc + vec2(x-offsetX,y-offsetY)) * tFrag );
                    }
                }

                o_FragColor = destColor * nFrag;
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

        // uniform(テクスチャユニット)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_Texture")
        MyGLES32Func.checkGlError("u_Texture:glGetUniformLocation")

        // uniform(レンダリング領域の大きさ)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle, "u_renderWH")
        MyGLES32Func.checkGlError("u_renderWH:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_Texture: Int,
             u_renderWH: Float) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[1],u_Texture)
        MyGLES32Func.checkGlError("u_Texture",this,model)

        // uniform(レンダリング領域の大きさ)
        GLES32.glUniform1f(hUNI[2], u_renderWH)
        MyGLES32Func.checkGlError("u_renderWH",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
