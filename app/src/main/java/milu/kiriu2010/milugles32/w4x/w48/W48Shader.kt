package milu.kiriu2010.milugles32.w4x.w48

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -----------------------------------------
// シェーダ for トゥーンレンダリング
// -----------------------------------------
// https://wgld.org/d/webgl/w048.html
// -----------------------------------------
class W48Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec4  a_Color;
            
            uniform   mat4  u_matMVP;
            uniform   int   u_edge;
            
            out  vec3  v_Normal;
            out  vec4  v_Color;

            void main() {
                vec3 pos      = a_Position;
                // エッジ用モデルを描画する場合、
                // モデルを法線方向に少しだけ膨らませる。
                if (bool(u_edge)) {
                    pos += a_Normal * 0.05;
                }
                v_Normal      = a_Normal;
                v_Color       = a_Color;
                gl_Position   = u_matMVP * vec4(pos,1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform   mat4        u_matINV;
            uniform   vec3        u_vecLight;
            uniform   sampler2D   u_Texture0;
            uniform   vec4        u_EdgeColor;
            
            in  vec3  v_Normal;
            in  vec4  v_Color;
            
            out vec4  o_FragColor;

            void main() {
                // 色のアルファ値
                // 0より大きい⇒エッジ用に使うためuniformの値をそのまま使う
                // 0         ⇒ライティングの計算を行う
                if (u_EdgeColor.a > 0.0) {
                    o_FragColor    = u_EdgeColor;
                }
                else {
                    vec3  invLight = normalize(u_matINV * vec4(u_vecLight,0.0)).xyz;
                    float diffuse  = clamp(dot(v_Normal,u_vecLight), 0.0, 1.0);
                    // -------------------------------------------------------------
                    // 色として出力する変数diffuseの値を、テクスチャの参照に使っている
                    // -------------------------------------------------------------
                    // 左が黒で右へ、だんだん白くなっていくテクスチャなので、
                    // 光が強く当たっているほど右側のテクセルを参照することになり、
                    // 結果的にモデルの色がそのまま出力される。
                    // 逆に光が当たっていない部分ほど
                    // 左側のテクセルを参照することになるので、
                    // モデルの色が若干暗くなる。
                    // -------------------------------------------------------------
                    vec4  smpColor = texture(u_Texture0, vec2(diffuse,0.0));
                    o_FragColor    = v_Color * smpColor;
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

        // uniform(逆行列)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matINV")
        MyGLES32Func.checkGlError("u_matINV:glGetUniformLocation")

        // uniform(平行光源)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_vecLight")
        MyGLES32Func.checkGlError("u_vecLight:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle, "u_Texture0")
        MyGLES32Func.checkGlError("u_Texture0:glGetUniformLocation")

        // uniform(エッジをつけるかどうか)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_edge")
        MyGLES32Func.checkGlError("u_edge:glGetUniformLocation")

        // uniform(エッジの色)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle,"u_EdgeColor")
        MyGLES32Func.checkGlError("u_EdgeColor:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_matI: FloatArray,
             u_vecLight: FloatArray,
             u_Texture0: Int,
             u_edge: Int,
             u_EdgeColor: FloatArray) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(逆行列)
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matI,0)
        MyGLES32Func.checkGlError("u_matINV",this,model)

        // uniform(平行光源)
        GLES32.glUniform3fv(hUNI[2],1,u_vecLight,0)
        MyGLES32Func.checkGlError("u_vecLight",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[3], u_Texture0)
        MyGLES32Func.checkGlError("u_Texture0",this,model)

        // uniform(エッジをつけるかどうか)
        GLES32.glUniform1i(hUNI[4],u_edge)
        MyGLES32Func.checkGlError("u_edge",this,model)

        // uniform(エッジの色)
        GLES32.glUniform4fv(hUNI[5],1,u_EdgeColor,0)
        MyGLES32Func.checkGlError("u_EdgeColor",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
