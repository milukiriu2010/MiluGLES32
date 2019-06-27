package milu.kiriu2010.milugles32.w5x.w50

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -------------------------------------------------------
// シェーダ(光学迷彩)
// -------------------------------------------------------
// 透けて見えるため、反射光によるハイライトは入らない。
// ハイライトが入ると、ゼリーのような見た目になるらしい。
// -------------------------------------------------------
// https://wgld.org/d/webgl/w050.html
// -------------------------------------------------------
class W50ShaderStealth(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec4  a_Color;
            
            uniform   mat4  u_matM;
            // ビュー×プロジェクション×テクスチャ座標変換行列
            uniform   mat4  u_matVPT;
            uniform   mat4  u_matMVP;
            // テクスチャ座標をずらすために使われる係数
            uniform   float u_coefficient;
            
            out  vec4  v_Color;
            out  vec4  v_TexCoord;

            void main() {
                vec3   pos  = (u_matM * vec4(a_Position, 1.0)).xyz;
                vec3   nor  = normalize((u_matM * vec4(a_Normal, 1.0)).xyz);
                v_Color     = a_Color;
                // ----------------------------------------------------------------------
                // モデル座標変換行列を掛け合わせた頂点位置と
                // テクスチャ座標変換行列とをかけあわせることで
                // テクスチャ座標を取得
                // ----------------------------------------------------------------------
                // 係数と法線を掛け合わせた数値を加算することでテクスチャ座標をずらしている
                // ----------------------------------------------------------------------
                v_TexCoord  = u_matVPT * vec4(pos + nor * u_coefficient, 1.0);
                gl_Position = u_matMVP * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp   float;

            uniform   sampler2D u_Texture0;
            
            in  vec4  v_Color;
            in  vec4  v_TexCoord;
            
            out vec4  o_FragColor;

            void main() {
                vec4 smpColor = textureProj(u_Texture0, v_TexCoord);
                o_FragColor   = v_Color * smpColor;
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

        // uniform(モデル)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matM")
        MyGLES32Func.checkGlError("u_matM:glGetUniformLocation")

        // uniform(ビュー×プロジェクション×テクスチャ座標変換行列)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matVPT")
        MyGLES32Func.checkGlError("u_matVPT:glGetUniformLocation")

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(光学迷彩にかける補正係数)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_coefficient")
        MyGLES32Func.checkGlError("u_coefficient:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_Texture0")
        MyGLES32Func.checkGlError("u_Texture0:glGetUniformLocation")

        return this
    }

    // 光学迷彩
    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matVPT: FloatArray,
             u_matMVP: FloatArray,
             u_coefficient: Float,
             u_Texture0: Int) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UserProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matM,0)
        MyGLES32Func.checkGlError("u_matM",this,model)

        // uniform(ビュー×プロジェクション×テクスチャ座標変換行列)
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matVPT,0)
        MyGLES32Func.checkGlError("u_matVPT",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[2],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(光学迷彩にかける補正係数)
        GLES32.glUniform1f(hUNI[3],u_coefficient)
        MyGLES32Func.checkGlError("u_coefficient",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[4],u_Texture0)
        MyGLES32Func.checkGlError("u_Texture0",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
