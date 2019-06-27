package milu.kiriu2010.milugles32.w3x.w39

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ---------------------------------------------
// ステンシルバッファを使ってアウトライン描画
// ---------------------------------------------
// https://wgld.org/d/webgl/w039.html
// ---------------------------------------------
class W39Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec4  a_Color;
            layout (location = 3) in vec2  a_TextureCoord;
            
            uniform   mat4  u_matMVP;
            uniform   mat4  u_matINV;
            uniform   vec3  u_vecLight;
            uniform   bool  u_useLight;
            uniform   bool  u_outLine;
            
            out  vec4  v_Color;
            out  vec2  v_TextureCoord;

            void main() {
                if (u_useLight) {
                    vec3  invLight = normalize(u_matINV * vec4(u_vecLight, 0.0)).xyz;
                    float diffuse  = clamp(dot(a_Normal, invLight), 0.1, 1.0);
                    v_Color        = a_Color * vec4(vec3(diffuse), 1.0);
                }
                else {
                    v_Color        = a_Color;
                }
                v_TextureCoord = a_TextureCoord;

                vec3 o_Position = a_Position;
                if (u_outLine) {
                    o_Position += a_Normal * 0.1;
                }
                gl_Position    = u_matMVP * vec4(o_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp   float;

            uniform   sampler2D u_Texture0;
            uniform   bool      u_useTexture;
            
            in   vec4  v_Color;
            in   vec2  v_TextureCoord;
            
            out  vec4  o_FragColor;

            void main() {
                vec4 smpColor = vec4(1.0);
                if (u_useTexture) {
                    smpColor = texture(u_Texture0, v_TextureCoord);
                }
                o_FragColor  = v_Color * smpColor;
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
        hUNI = IntArray(7)
        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(逆行列)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matINV")
        MyGLES32Func.checkGlError("u_matINV:glGetUniformLocation")

        // uniform(光源位置)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_vecLight")
        MyGLES32Func.checkGlError("u_vecLight:glGetUniformLocation")

        // uniform(ライティングを使うかどうか)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_useLight")
        MyGLES32Func.checkGlError("u_useLight:glGetUniformLocation")

        // uniform(法線方向に頂点を膨らませるかどうか)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_outLine")
        MyGLES32Func.checkGlError("u_outLine:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle, "u_Texture0")
        MyGLES32Func.checkGlError("u_Texture0:glGetUniformLocation")

        // uniform(テクスチャを使うかどうか)
        hUNI[6] = GLES32.glGetUniformLocation(programHandle,"u_useTexture")
        MyGLES32Func.checkGlError("u_useTexture:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_matI: FloatArray,
             u_vecLight: FloatArray,
             u_useLight: Int,
             u_outLine: Int,
             u_Texture0: Int,
             u_useTexture: Int) {
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

        // uniform(光源位置)
        GLES32.glUniform3fv(hUNI[2],1,u_vecLight,0)
        MyGLES32Func.checkGlError("u_vecLight",this,model)

        // uniform(ライティングを使うかどうか)
        GLES32.glUniform1i(hUNI[3],u_useLight)
        MyGLES32Func.checkGlError("u_useLight",this,model)

        // uniform(法線方向に頂点を膨らませるかどうか)
        GLES32.glUniform1i(hUNI[4],u_outLine)
        MyGLES32Func.checkGlError("u_outLine",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[5], u_Texture0)
        MyGLES32Func.checkGlError("u_Texture0",this,model)

        // uniform(テクスチャを使うかどうか)
        GLES32.glUniform1i(hUNI[6],u_useTexture)
        MyGLES32Func.checkGlError("u_useTexture",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
