package milu.kiriu2010.milugles32.g0x.g04

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ---------------------------------------
// 様々な図形を描く
// ---------------------------------------
// https://wgld.org/d/glsl/g004.html
// ---------------------------------------
class G04Shader(ctx: Context): ES32MgShader(ctx) {
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
            precision mediump   float;

            // 経過時間(ミリ秒を1/1000)
            uniform   float     u_time;
            // 0.0-1.0に正規化
            uniform   vec2      u_mouse;
            // 描画領域の幅・高さ
            uniform   vec2      u_resolution;
            // 描画する図形の種類
            uniform   int       u_type;
            
            out vec4 o_FragColor;

            void main() {
                // 0～1の範囲で入ってくるマウスの位置を
                // -1～1の範囲に正規化している
                // Y座標は上下逆のため、正負を逆転している
                vec2 m = vec2(u_mouse.x*2.0-1.0,-u_mouse.y*2.0+1.0);
                // 今から処理しようとしているスクリーン上のピクセル位置を
                // -1～1の範囲に正規化している
                vec2 p = (gl_FragCoord.xy * 2.0 - u_resolution)/min(u_resolution.x, u_resolution.y);
                float t = 0.0;

                // 輪
                if ( u_type == 1 ) {
                    t = 0.02/abs(0.5-length(p));
                }
                // 輪(時間経過によって大きさが変化する)
                else if ( u_type == 2 ) {
                    t = 0.02/abs(abs(sin(u_time))-length(p));
                }
                // グラデーション
                else if ( u_type == 3 ) {
                    vec2 v = vec2(0.0,1.0);
                    // ピクセル位置とY方向にプラスとなるベクトルとの内積
                    // X軸より上が白っぽく、上に上がるにつれ真っ白になる。
                    // X軸より下は負になるので黒になる
                    t = dot(p,v);
                }
                // 上から円錐を見たような描画
                else if ( u_type == 4 ) {
                    vec2 v = vec2(0.0,1.0);
                    t = dot(p,v)/(length(p)*length(v));
                }
                // 集中線のような放射状のライン
                else if ( u_type == 5 ) {
                    t = atan(p.y,p.x) + u_time;
                    t = sin(t*10.0);
                }
                // 花
                else if ( u_type == 6 ) {
                    float u = sin((atan(p.y,p.x)+u_time*0.5)*6.0);
                    t = 0.01/abs(u-length(p));
                }
                // 波打つリング
                else if ( u_type == 7 ) {
                    float u = sin((atan(p.y,p.x)+u_time*0.5)*20.0)*0.01;
                    t = 0.01/abs(0.5+u-length(p));
                }
                // 花その２
                else if ( u_type == 8 ) {
                    float u = sin((atan(p.y,p.x)+u_time*0.5)*20.0)*0.5;
                    t = 0.01/abs(0.25+u-length(p));
                }
                // 花模様をファンのように変形
                else if ( u_type == 9 ) {
                    float u = abs( sin((atan(p.y,p.x)-length(p)+u_time)*10.0)*0.5 )+0.2;
                    t = 0.01/abs(u-length(p));
                }
                else {
                    t = 0.0;
                }

                o_FragColor = vec4(vec3(t), 1.0);
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

        // uniform(描画する図形の種類)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_type")
        MyGLES32Func.checkGlError("u_type:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_time: Float,
             u_mouse: FloatArray,
             u_resolution: FloatArray,
             u_type: Int) {
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

        // uniform(描画する図形の種類)
        GLES32.glUniform1i(hUNI[3],u_type)
        MyGLES32Func.checkGlError("u_type",this,model)

        // モデル描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
