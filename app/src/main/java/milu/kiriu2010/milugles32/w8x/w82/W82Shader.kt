package milu.kiriu2010.milugles32.w8x.w82

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs
import milu.kiriu2010.math.MyMathUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

// ------------------------------------
// シェーダ(VBO逐次更新:パーティクル)
// ------------------------------------
// https://wgld.org/d/webgl/w081.html
// ------------------------------------
class W82Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec2  a_Position;
            
            uniform   float u_pointSize;

            void main() {
                gl_Position  = vec4(a_Position, 0.0, 1.0);
                gl_PointSize = u_pointSize;
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp   float;
            
            uniform vec4 u_pointColor;
            
            out vec4 o_FragColor;

            void main() {
                o_FragColor = u_pointColor;
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

        // uniform(描画点の大きさ)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_pointSize")
        MyGLES32Func.checkGlError("u_pointSize:glGetUniformLocation")

        // uniform(描画点の色)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_pointColor")
        MyGLES32Func.checkGlError("u_pointColor:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_pointSize: Float,
             u_pointColor: FloatArray,
             renderer: W82Renderer) {
        val model = vao.model as W82Model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // 押下位置を-1～1に正規化
        val mx = (renderer.touchP.x-renderer.renderW.toFloat()*0.5f)/renderer.renderW.toFloat()*2f
        val my = -(renderer.touchP.y-renderer.renderH.toFloat()*0.5f)/renderer.renderH.toFloat()*2f

        // 点を更新
        model.updatePoint(renderer.isRunning,renderer.velocity,renderer.SPEED,mx,my)

        // uniform(描画点の大きさ)
        GLES32.glUniform1f(hUNI[0],u_pointSize)
        MyGLES32Func.checkGlError("u_pointSize",this,model)

        // uniform(描画点の色)
        GLES32.glUniform4fv(hUNI[1], 1, u_pointColor,0)
        MyGLES32Func.checkGlError("u_pointColor",this,model)

        // モデルを描画
        GLES32.glDrawArrays(GLES32.GL_POINTS,0,model.verticesCount)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
