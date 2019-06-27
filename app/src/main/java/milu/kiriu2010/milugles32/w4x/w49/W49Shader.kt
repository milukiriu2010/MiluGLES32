package milu.kiriu2010.milugles32.w4x.w49

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// --------------------------------------
// シェーダ for 射影テクスチャマッピング
// --------------------------------------
// https://wgld.org/d/webgl/w049.html
// --------------------------------------
class W49Shader(ctx: Context): ES32MgShader(ctx) {
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
            
            out  vec3  v_Position;
            out  vec3  v_Normal;
            out  vec4  v_Color;
            // 射影変換を適用した頂点座標
            out  vec4  v_TexCoord;

            void main() {
                v_Position    = (u_matM * vec4(a_Position,1.0)).xyz;
                v_Normal      = a_Normal;
                v_Color       = a_Color;
                // モデル座標変換行列を掛け合わせた頂点位置と
                // テクスチャ座標変換行列とをかけあわせることで
                // テクスチャ座標を取得
                v_TexCoord    = u_matVPT * vec4(v_Position,1.0);
                gl_Position   = u_matMVP * vec4(a_Position,1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform   mat4        u_matINV;
            uniform   vec3        u_vecLight;
            uniform   sampler2D   u_Texture0;
            
            in  vec3  v_Position;
            in  vec3  v_Normal;
            in  vec4  v_Color;
            in  vec4  v_TexCoord;
            
            out vec4  o_FragColor;

            void main() {
                // 点光源でのライティング
                vec3  light    = u_vecLight - v_Position;
                vec3  invLight = normalize(u_matINV * vec4(light,0.0)).xyz;
                float diffuse  = clamp(dot(v_Normal,invLight), 0.1, 1.0);
                // 頂点を射影変換した結果からテクスチャをサンプリングするため
                // texture2DProjを使う
                vec4  smpColor = textureProj(u_Texture0, v_TexCoord);
                o_FragColor    = v_Color * vec4(vec3(diffuse), 1.0) * smpColor;
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

        // uniform(逆行列)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_matINV")
        MyGLES32Func.checkGlError("u_matINV:glGetUniformLocation")

        // uniform(光源位置)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_vecLight")
        MyGLES32Func.checkGlError("u_vecLight:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle, "u_Texture0")
        MyGLES32Func.checkGlError("u_Texture0:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matVPT: FloatArray,
             u_matMVP: FloatArray,
             u_matI: FloatArray,
             u_vecLight: FloatArray,
             u_Texture0: Int) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

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

        // uniform(逆行列)
        GLES32.glUniformMatrix4fv(hUNI[3],1,false,u_matI,0)
        MyGLES32Func.checkGlError("u_matINV",this,model)

        // uniform(光源位置)
        GLES32.glUniform3fv(hUNI[4],1,u_vecLight,0)
        MyGLES32Func.checkGlError("u_vecLight",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[5], u_Texture0)
        MyGLES32Func.checkGlError("u_Texture0",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
