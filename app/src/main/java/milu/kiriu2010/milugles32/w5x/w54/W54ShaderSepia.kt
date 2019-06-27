package milu.kiriu2010.milugles32.w5x.w54

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -------------------------------------
// セピア調変換用シェーダ
// -------------------------------------
// https://wgld.org/d/webgl/w053.html
// -------------------------------------
class W54ShaderSepia(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec2  a_TextureCoord;
            
            uniform   mat4  u_matMVP;
            
            out  vec2  v_TexCoord;

            void main() {
                v_TexCoord      = a_TextureCoord;
                gl_Position     = u_matMVP   * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform   sampler2D   u_Texture0;
            uniform   int         u_grayScale;
            uniform   int         u_sepiaScale;
            
            in  vec2  v_TexCoord;
            out vec4  o_FragColor;

            // NTSC系加重平均法
            const float redScale   = 0.298912;
            const float greenScale = 0.586611;
            const float blueScale  = 0.114478;
            const vec3  monochromeScale = vec3(redScale, greenScale, blueScale);

            // セピアは、RGBで表すと(107,74,43)
            const float sRedScale   = 1.07;
            const float sGreenScale = 0.74;
            const float sBlueScale  = 0.43;
            const vec3  sepiaScale = vec3(sRedScale, sGreenScale, sBlueScale);

            // いったんグレースケール化した後で、RGBの比率でセピア変換する
            void main() {
                vec4 smpColor = texture(u_Texture0, v_TexCoord);
                float grayColor = dot(smpColor.rgb, monochromeScale);
                if (bool(u_grayScale)) {
                    smpColor = vec4(vec3(grayColor), 1.0);
                } else if (bool(u_sepiaScale)) {
                    vec3 monoColor = vec3(grayColor) * sepiaScale;
                    smpColor = vec4(monoColor, 1.0);
                }
                o_FragColor = smpColor;
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

        // テクスチャユニット
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_Texture0")
        MyGLES32Func.checkGlError("u_Texture:glGetUniformLocation")

        // uniform(グレースケールを使うかどうか)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle, "u_grayScale")
        MyGLES32Func.checkGlError("u_grayScale:glGetUniformLocation")

        // uniform(セピア調にするかどうか)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle, "u_sepiaScale")
        MyGLES32Func.checkGlError("u_sepiaScale:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_Texture0: Int,
             u_grayScale: Int,
             u_sepiaScale: Int) {
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
        GLES32.glUniform1i(hUNI[1],u_Texture0)
        MyGLES32Func.checkGlError("u_Texture0",this,model)

        // uniform(グレースケールを使うかどうか)
        GLES32.glUniform1i(hUNI[2], u_grayScale)
        MyGLES32Func.checkGlError("u_grayScale",this,model)

        // uniform(セピア調にするかどうか)
        GLES32.glUniform1i(hUNI[3], u_sepiaScale)
        MyGLES32Func.checkGlError("u_sepiaScale",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
