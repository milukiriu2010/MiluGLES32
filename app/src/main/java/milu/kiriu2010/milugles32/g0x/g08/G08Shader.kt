package milu.kiriu2010.milugles32.g0x.g08

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ---------------------------------------
// レイマーチング(RGB)
// ---------------------------------------
// https://wgld.org/d/glsl/g008.html
// ---------------------------------------
class G08Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;

            void main() {
                gl_Position = vec4(a_Position,1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp   float;

            // 経過時間(ミリ秒を1/1000)
            uniform   float     u_time;
            // 0.0-1.0に正規化
            uniform   vec2      u_mouse;
            // 描画領域の幅・高さ
            uniform   vec2      u_resolution;
            
            out vec4  o_FragColor;

            void main() {
                // ----------------------------------------------------
                // マウス座標の正規化
                // ----------------------------------------------------
                // 0～1の範囲で入ってくるマウスの位置を
                // -1～1の範囲に正規化している
                // Y座標は上下逆のため、正負を逆転している
                // ----------------------------------------------------
                vec2 m = vec2(u_mouse.x*2.0-1.0,-u_mouse.y*2.0+1.0);
                // ----------------------------------------------------
                // フラグメント座標の正規化
                // ----------------------------------------------------
                // 今から処理しようとしているスクリーン上のピクセル位置を
                // -1～1の範囲に正規化している
                // ----------------------------------------------------
                vec2 p = (gl_FragCoord.xy * 2.0 - u_resolution)/min(u_resolution.x, u_resolution.y);

                // camera
                vec3 cPos = vec3(0.0,  0.0,  3.0); // カメラの位置
                vec3 cDir = vec3(0.0,  0.0, -1.0); // カメラの向き(視線)
                vec3 cUp  = vec3(0.0,  1.0,  0.0); // カメラの上方向
                vec3 cSide = cross(cDir, cUp);     // 外積を使って横方向を算出
                float targetDepth = 0.1;           // フォーカスする深度

                // ray
                // X:赤,Y:緑,Z:青
                vec3 ray = normalize(cSide * p.x + cUp * p.y + cDir * targetDepth);

                // color
                o_FragColor = vec4(ray.xy, -ray.z, 1.0);
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

        // uniform(時間)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_time")
        MyGLES32Func.checkGlError("u_time:glGetUniformLocation")

        // uniform(タッチ位置)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_mouse")
        MyGLES32Func.checkGlError("u_mouse:glGetUniformLocation")

        // uniform(解像度)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_resolution")
        MyGLES32Func.checkGlError("u_resolution:glGetUniformLocation")

        // uniform(ノイズの種類)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_noiseType")
        MyGLES32Func.checkGlError("u_noiseType:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_time: Float,
             u_mouse: FloatArray,
             u_resolution: FloatArray) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(時間)
        GLES32.glUniform1f(hUNI[0],u_time)
        MyGLES32Func.checkGlError("u_time",this,model)

        // uniform(タッチ位置)
        GLES32.glUniform2fv(hUNI[1],1,u_mouse,0)
        MyGLES32Func.checkGlError("u_mouse",this,model)

        // uniform(解像度)
        GLES32.glUniform2fv(hUNI[2],1,u_resolution,0)
        MyGLES32Func.checkGlError("u_resolution",this,model)

        // モデル描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
