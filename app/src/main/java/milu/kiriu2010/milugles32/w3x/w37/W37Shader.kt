package milu.kiriu2010.milugles32.w3x.w37

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------
// ポイントスプライト
// ------------------------------------
// https://wgld.org/d/webgl/w037.html
// ------------------------------------
class W37Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec4  a_Color;
            
            uniform   mat4  u_matMVP;
            uniform   float u_pointSize;
            
            out  vec4  v_Color;

            void main() {
                v_Color        = a_Color;
                gl_Position    = u_matMVP * vec4(a_Position,1.0);
                gl_PointSize   = u_pointSize;
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp   float;

            uniform   sampler2D u_Texture0;
            uniform   int       u_useTexture;
            
            in  vec4  v_Color;
            
            out vec4  o_FragColor;

            void main() {
                vec4 smpColor = vec4(1.0);
                if (bool(u_useTexture)) {
                    // -----------------------------------------
                    // gl_PointCoord
                    //   描画される点上のテクスチャ座標が入る
                    //   レンダリングされる点そのものに対して
                    //   テクスチャを適用する場合に指定する
                    // -----------------------------------------
                    smpColor = texture(u_Texture0, gl_PointCoord);
                }

                // -------------------------------------------------------------------------
                // 完全に透過している場合、
                // フラグメントシェーダが何もしないようにする
                // -------------------------------------------------------------------------
                // アルファブレンドでは奥にあるものからレンダリングしていくのが基本だが
                // ポイントスプライトを用いた処理では、
                // 点と点との距離が近い場合や、カメラからみて重なってしまうような座標にある場合、
                // 適切にZソートしないと希望通りの結果が得られない。
                // -------------------------------------------------------------------------
                if (smpColor.a == 0.0) {
                    discard;
                }
                else {
                    o_FragColor  = v_Color * smpColor;
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
        hUNI = IntArray(4)
        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(描画点のサイズ)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_pointSize")
        MyGLES32Func.checkGlError("u_pointSize:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle, "u_Texture0")
        MyGLES32Func.checkGlError("u_Texture0:glGetUniformLocation")

        // uniform(テクスチャを使うかどうか)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle, "u_useTexture")
        MyGLES32Func.checkGlError("u_useTexture:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_pointSize: Float,
             u_Texture0: Int,
             u_useTexture: Int,
             lineType: Int) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(描画点のサイズ)
        GLES32.glUniform1f(hUNI[1],u_pointSize)
        MyGLES32Func.checkGlError("u_pointSize",this,model)

        // uniform(テクスチャユニット)
        if ( u_Texture0 >= 0 ) {
            GLES32.glUniform1i(hUNI[2], u_Texture0)
            MyGLES32Func.checkGlError("u_Texture0",this,model)
        }

        // uniform(テクスチャを使うかどうか)
        GLES32.glUniform1i(hUNI[3], u_useTexture)
        MyGLES32Func.checkGlError("u_useTexture",this,model)

        // モデルを描画
        GLES32.glDrawArrays(lineType, 0, model.datPos.size/3)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
