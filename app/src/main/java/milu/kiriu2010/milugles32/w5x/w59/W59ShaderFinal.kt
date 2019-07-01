package milu.kiriu2010.milugles32.w5x.w59

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -----------------------------------------------------------------------
// シェーダ(最終結果)
//   レンダリングされた全てのシーンを合成する
// -----------------------------------------------------------------------
// https://wgld.org/d/webgl/w059.html
// -----------------------------------------------------------------------
class W59ShaderFinal(ctx: Context): ES32MgShader(ctx) {
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

            // 深度マップテクスチャ
            uniform   sampler2D   u_TextureDepth;
            // ぼけていないシーンのテクスチャ
            uniform   sampler2D   u_TextureScene;
            // 小さくぼけたシーンのテクスチャ
            uniform   sampler2D   u_TextureBlur1;
            // 大きくぼけたシーンのテクスチャ
            uniform   sampler2D   u_TextureBlur2;
            uniform   int         u_result;
            
            in  vec2  v_TextureCoord;
            
            out vec4  o_FragColor;

            // フレームバッファに描きこまれた深度値を本来の値に変換する
            float restDepth(vec4 RGBA) {
                const float rMask = 1.0;
                const float gMask = 1.0/255.0;
                const float bMask = 1.0/(255.0*255.0);
                const float aMask = 1.0/(255.0*255.0*255.0);
                float depth = dot(RGBA, vec4(rMask, gMask, bMask, aMask));
                return depth;
            }

            // フレームバッファに描かれた深度値を読み出し、
            // ライト視点で座標変換した頂点の深度値と比較する
            void main() {
                // 深度値を逆変換して、元に戻している
                float d = restDepth(texture(u_TextureDepth, vec2(v_TextureCoord.s,1.0-v_TextureCoord.t)));
                // ぼやけたシーンをどの程度合成するか決めている
                float coef = 1.0 - d;
                float coefBlur1 = coef * d;
                float coefBlur2 = coef * coef;
                vec4 colorScene = texture(u_TextureScene, vec2(v_TextureCoord.s,1.0-v_TextureCoord.t));
                vec4 colorBlur1 = texture(u_TextureBlur1, v_TextureCoord);
                vec4 colorBlur2 = texture(u_TextureBlur2, v_TextureCoord);
                vec4 destColor  = colorScene*d + colorBlur1*coefBlur1 + colorBlur2*coefBlur2;

                // 合成
                if ( u_result == 0 ) {
                    o_FragColor = destColor;
                }
                // モノクロ
                else if ( u_result == 1 ) {
                    o_FragColor = vec4(vec3(d),1.0);
                }
                // ぼやけていないシーン
                else if ( u_result == 2 ) {
                    o_FragColor = colorScene;
                }
                // 小さくぼやけたシーン
                else if ( u_result == 3 ) {
                    o_FragColor = colorBlur1;
                }
                // 大きくぼやけたシーン
                else {
                    o_FragColor = colorBlur2;
                }
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
        hUNI = IntArray(6)

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle, "u_TextureDepth")
        MyGLES32Func.checkGlError("u_TextureDepth:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle, "u_TextureScene")
        MyGLES32Func.checkGlError("u_TextureScene:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle, "u_TextureBlur1")
        MyGLES32Func.checkGlError("u_TextureBlur1:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle, "u_TextureBlur2")
        MyGLES32Func.checkGlError("u_TextureBlur2:glGetUniformLocation")

        // uniform(結果)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle, "u_result")
        MyGLES32Func.checkGlError("u_result:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_TextureDepth: Int,
             u_TextureScene: Int,
             u_TextureBlur1: Int,
             u_TextureBlur2: Int,
             u_result: Int) {
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
        GLES32.glUniform1i(hUNI[1], u_TextureDepth)
        MyGLES32Func.checkGlError("u_TextureDepth",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[2], u_TextureScene)
        MyGLES32Func.checkGlError("u_TextureScene",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[3], u_TextureBlur1)
        MyGLES32Func.checkGlError("u_TextureBlur1",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[4], u_TextureBlur2)
        MyGLES32Func.checkGlError("u_TextureBlur2",this,model)

        // uniform(結果)
        GLES32.glUniform1i(hUNI[5], u_result)
        MyGLES32Func.checkGlError("u_result",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
