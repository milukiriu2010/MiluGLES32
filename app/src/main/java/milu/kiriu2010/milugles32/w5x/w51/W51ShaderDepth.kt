package milu.kiriu2010.milugles32.w5x.w51

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.MgModelAbs
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -------------------------------------
// シェーダ(深度格納用)
// -------------------------------------
// https://wgld.org/d/webgl/w051.html
// -------------------------------------
class W51ShaderDepth(ctx: Context): ES32MgShader(ctx) {
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
            precision highp   float;

            uniform   int       u_depthBuffer;
            
            in  vec4  v_Position;
            
            out vec4  o_FragColor;

            // 深度値を32ビット精度に変換している
            vec4 convRGBA(float depth) {
                // 深度値を255倍し、その小数点以下の数値を抜き出し、次に渡す。
                // の繰り返し
                float r = depth;
                float g = fract(r*255.0);
                float b = fract(g*255.0);
                float a = fract(b*255.0);
                // 誤差を相殺するためのバイアスを各要素にかける
                float coef = 1.0/255.0;
                r -= g*coef;
                g -= b*coef;
                b -= a*coef;
                return vec4(r,g,b,a);
            }

            // 深度値を色情報に格納する
            void main() {
                vec4 convColor;
                // デプスバッファの深度値を使う
                if (bool(u_depthBuffer)) {
                    convColor = convRGBA(gl_FragCoord.z);
                }
                // 頂点座標からダイレクトに深度に相当する値を生成してフラグメントに描く
                else {
                    float near = 0.1;
                    float far  = 150.0;
                    float linearDepth = 1.0/(far-near);
                    linearDepth *= length(v_Position);
                    convColor = convRGBA(linearDepth);
                }
                o_FragColor   = convColor;
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
        hUNI = IntArray(2)

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(深度値を使うかどうか)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle, "u_depthBuffer")
        MyGLES32Func.checkGlError("u_depthBuffer:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_depthBuffer: Int) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(深度値を使うかどうか)
        GLES32.glUniform1i(hUNI[1], u_depthBuffer)
        MyGLES32Func.checkGlError("u_depthBuffer",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
