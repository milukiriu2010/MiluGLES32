package milu.kiriu2010.milugles32.w7x.w71

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -------------------------------------
// シェーダ(点のレンダリングを行う)
// -------------------------------------
// テクスチャから座標位置を読み出す
// -------------------------------------
// 頂点テクスチャフェッチ
// -------------------------------------
// https://wgld.org/d/webgl/w071.html
// -------------------------------------
class W71ShaderPoint(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in float a_Index;

            uniform   mat4       u_matMVP;
            uniform   sampler2D  u_Texture0;

            // 描きこみの対象となる
            // フレームバッファ(テクスチャ)一辺あたりの長さを１で割る
            const float frag     = 1.0/16.0;
            // さらに半分の値
            const float texShift = 0.5 * frag;

            void main() {
                // fract => x-floor(x)を返す
                // 頂点の識別番号に定数fragをかけ,
                // その結果の小数点以下の部分だけを抜き出す
                float pu = fract(a_Index*frag + texShift);
                float pv = floor(a_Index*frag)*frag + texShift;
                // オフセットさせながらテクスチャを参照
                vec3  tPosition = texture(u_Texture0,vec2(pu,pv)).rgb*2.0 - 1.0;
                gl_Position     = u_matMVP * vec4(tPosition, 1.0);
                gl_PointSize    = 16.0;
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp   float;

            uniform sampler2D   u_Texture0;
            
            out vec4 o_FragColor;

            void main() {
                o_FragColor = texture(u_Texture0, gl_PointCoord);
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

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(テクスチャユニット0)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle, "u_Texture0")
        MyGLES32Func.checkGlError("u_Texture0:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_Texture0: Int) {
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
        GLES32.glUniform1i(hUNI[1], u_Texture0)
        MyGLES32Func.checkGlError("u_Texture0",this,model)

        // モデルを描画
        GLES32.glDrawArrays(GLES32.GL_POINTS,0,model.datIdx.size)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
