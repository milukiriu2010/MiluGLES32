package milu.kiriu2010.milugles32.w6x.w69

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.MgModelAbs
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -------------------------------------
// シェーダ(深度格納用)
// -------------------------------------
// w51とは違うらしい
// -------------------------------------
// https://wgld.org/d/webgl/w069.html
// -------------------------------------
class W69ShaderDepth(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            
            uniform   mat4  u_matMVP;
            out vec4  v_Position;

            void main() {
                v_Position  = u_matMVP * vec4(a_Position, 1.0);
                gl_Position = v_Position;
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision mediump   float;

            in  vec4 v_Position;
            
            out vec4 o_FragColor;

            // ----------------------------------------------------
            // 深度値を色情報に格納する
            // ----------------------------------------------------
            // 正規化デバイス座標系に座標系を変換するにはwを使う
            // ３次元である頂点位置に対してwで割る処理を挟むことで
            // 必ず頂点座標は-1～1の範囲内に収まる
            // テクスチャに描きこめる値は0～1の範囲なので、
            // 1足して2で割る
            // ----------------------------------------------------
            // クリップ空間は最終的に正規化されるため、
            // どれほど広大な空間も最後には-1～1の範囲に収束する
            // ----------------------------------------------------
            void main() {
                float depth = (v_Position.z/v_Position.w+1.0)*0.5;
                o_FragColor = vec4(vec3(depth),1.0);
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
        hUNI = IntArray(1)

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        return this
    }


    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
