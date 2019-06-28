package milu.kiriu2010.milugles32.g0x.g05

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ---------------------------------------
// マンデルブロ集合
// ---------------------------------------
// https://wgld.org/d/glsl/g005.html
// ---------------------------------------
class G05Shader(ctx: Context): ES32MgShader(ctx) {
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

            vec3 hsv(float h, float s, float v){
                vec4 t = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
                vec3 p = abs(fract(vec3(h) + t.xyz) * 6.0 - vec3(t.w));
                return v * mix(vec3(t.x), clamp(p - vec3(t.x), 0.0, 1.0), s);
            }

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

                int j = 0;
                // -------------------------------------
                // 原点を少しずらす
                // -------------------------------------
                // x.x=-1.5～0.5
                // x.y=-1.0～1.0
                // -------------------------------------
                vec2  x = p + vec2(-0.5, 0.0);
                // -------------------------------------
                // マウス座標を使って拡大度を変更
                // -------------------------------------
                // y = 1.0～1.5
                // -------------------------------------
                float y = 1.5 - u_mouse.x * 0.5;
                // 漸化式Zの初期値(Z0)
                vec2  z = vec2(0.0, 0.0);
                // 漸化式Zの繰り返し処理
                for(int i = 0; i < 360; i++){
                    j++;
                    // 発散判定
                    if(length(z) > 2.0){break;}
                    // z.x ⇒ Znの実数部
                    // z.y ⇒ Znの虚数部
                    // x*y ⇒ C
                    z = vec2(z.x * z.x - z.y * z.y, 2.0 * z.x * z.y) + x * y;
                }

                // 時間の経過で色HSV出力する
                float h   = mod(u_time * 20.0, 360.0) / 360.0;
                vec3  rgb = hsv(h, 1.0, 1.0);
                // 漸化式で繰り返した回数をもとに輝度を決める
                float t   = float(j) / 360.0;
                o_FragColor = vec4(rgb * t, 1.0);
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
        hUNI = IntArray(3)

        // uniform(時間)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_time")
        MyGLES32Func.checkGlError("u_time:glGetUniformLocation")

        // uniform(タッチ位置)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_mouse")
        MyGLES32Func.checkGlError("u_mouse:glGetUniformLocation")

        // uniform(解像度)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_resolution")
        MyGLES32Func.checkGlError("u_resolution:glGetUniformLocation")

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
