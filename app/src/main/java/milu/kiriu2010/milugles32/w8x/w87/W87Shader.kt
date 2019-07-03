package milu.kiriu2010.milugles32.w8x.w87

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// --------------------------------------
// シェーダ(フラットシェーダ)
// --------------------------------------
// https://wgld.org/d/webgl/w087.html
// --------------------------------------
class W87Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es

            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec4  a_Color;
            
            uniform   mat4  u_matM;
            uniform   mat4  u_matMVP;
            
            out  vec4  v_Position;
            out  vec4  v_Color;

            void main() {
                v_Color     = a_Color;
                v_Position  = u_matM   * vec4(a_Position, 1.0);
                gl_Position = u_matMVP * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es

            precision highp   float;

            uniform   vec3   u_vecLight;
            
            in  vec4   v_Position;
            in  vec4   v_Color;
            
            out vec4  o_FragColor;

            void main() {
                vec3 dx = dFdx(v_Position.xyz);
                vec3 dy = dFdy(v_Position.xyz);
                vec3 n  = normalize(cross(normalize(dx),normalize(dy)));

                vec3  light  = normalize(u_vecLight);
                float diff   = clamp(dot(n,light),0.1,1.0);
                o_FragColor  = vec4(v_Color.rgb * diff, 1.0);
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
        hUNI = IntArray(3)

        // uniform(モデル)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matM")
        MyGLES32Func.checkGlError("u_matM:glGetUniformLocation")

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(光源位置)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_vecLight")
        MyGLES32Func.checkGlError("u_vecLight:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matMVP: FloatArray,
             u_vecLight: FloatArray) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル座標変換行列)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matM,0)
        MyGLES32Func.checkGlError("u_matM",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(光源位置)
        GLES32.glUniform3fv(hUNI[2],1,u_vecLight,0)
        MyGLES32Func.checkGlError("u_vecLight",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
