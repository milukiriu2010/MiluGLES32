package milu.kiriu2010.milugles32.w7x.w72

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ----------------------------------------
// シェーダ(テクスチャへの描きこみを行う)
// ----------------------------------------
// 頂点の座標位置をテクスチャへ描きこむ
// 16ピクセルのテクスチャに
// 頂点の座標位置を色として描く
// ----------------------------------------
// 浮動小数点数VTF
// ----------------------------------------
// https://wgld.org/d/webgl/w072.html
// ----------------------------------------
class W72ShaderMapping(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in float a_Index;

            out  vec3  v_Color;

            // 描きこみの対象となる
            // フレームバッファ(テクスチャ)一辺あたりの長さを１で割る
            const float frag     = 1.0/16.0;
            // さらに半分の値
            const float texShift = 0.5 * frag;
            
            const float rCoef = 1.0;
            const float gCoef = 1.0/255.0;
            const float bCoef = 1.0/(255.0*255.0);

            void main() {
                float r = a_Position.x * rCoef;
                float g = a_Position.y * gCoef;
                float b = a_Position.z * bCoef;
            
                // -1～1の範囲にある頂点の位置を0～1の範囲に収める
                v_Color   = (vec3(r,g,b)+1.0) * 0.5;
                // fract => x-floor(x)を返す
                // 頂点の識別番号に定数fragをかけ,
                // その結果の小数点以下の部分だけを抜き出す
                float pu = fract(a_Index*frag)*2.0 - 1.0;
                float pv = floor(a_Index*frag)*frag*2.0 - 1.0;
                // 各テクセルに対して中心部へオフセットをかけるためtexShiftを足す
                gl_Position  = vec4(pu+texShift, pv+texShift, 0.0, 1.0);
                gl_PointSize = 1.0;
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp   float;

            in  vec3  v_Color;
            out vec4  o_FragColor;

            void main() {
                o_FragColor = vec4(v_Color, 1.0);
            }
            """.trimIndent()


    override fun loadShader(): ES32MgShader {
        // 頂点シェーダを生成
        svhandle = MyGLES32Func.loadShader(GLES32.GL_VERTEX_SHADER, scv)
        // フラグメントシェーダを生成
        sfhandle = MyGLES32Func.loadShader(GLES32.GL_FRAGMENT_SHADER, scf)

        // プログラムオブジェクトの生成とリンク
        programHandle = MyGLES32Func.createProgram(svhandle,sfhandle)

        return this
    }

    fun draw(vao: ES32VAOAbs,
             count: Int) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // モデルを描画
        GLES32.glDrawArrays(GLES32.GL_POINTS,0,count)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
