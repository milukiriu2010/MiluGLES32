package milu.kiriu2010.milugles32.g0x.g07

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ---------------------------------------
// フラグメントシェーダ ノイズ
// ---------------------------------------
// https://wgld.org/d/glsl/g007.html
// ---------------------------------------
class G07Shader(ctx: Context): ES32MgShader(ctx) {
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
            // ノイズタイプ
            uniform   int       u_noiseType;
            
            out vec4  o_FragColor;

            const int   oct  = 8;
            const float per  = 0.5;
            const float PI   = 3.1415926;
            const float cCorners = 1.0 / 16.0;
            const float cSides   = 1.0 / 8.0;
            const float cCenter  = 1.0 / 4.0;

            // 補間関数
            float interpolate(float a, float b, float x){
	            float f = (1.0 - cos(x * PI)) * 0.5;
	            return a * (1.0 - f) + b * f;
            }

            // 乱数生成
            float rnd(vec2 p){
                return fract(sin(dot(p ,vec2(12.9898,78.233))) * 43758.5453);
            }

            // 補間乱数
            float irnd(vec2 p){
                vec2 i = floor(p);
                vec2 f = fract(p);
                vec4 v = vec4(rnd(vec2(i.x,       i.y      )),
                              rnd(vec2(i.x + 1.0, i.y      )),
                              rnd(vec2(i.x,       i.y + 1.0)),
                              rnd(vec2(i.x + 1.0, i.y + 1.0)));
                // interpolateで乱数を補間している
                return interpolate(interpolate(v.x, v.y, f.x), interpolate(v.z, v.w, f.x), f.y);
            }

            // ノイズ生成
            float noise(vec2 p){
                float t = 0.0;
                for(int i = 0; i < oct; i++){
                    float freq = pow(2.0, float(i));
                    float amp  = pow(per, float(oct - i));
                    // 乱数を合成している
                    t += irnd(vec2(p.x / freq, p.y / freq)) * amp;
                }
                return t;
            }

            // シームレスノイズ生成
            // 一辺の長さをどのくらいに設定してシームレスなタイル状にするか引数で指定できる
            float snoise(vec2 p, vec2 q, vec2 r){
                return noise(vec2(p.x,       p.y      )) *        q.x  *        q.y  +
                       noise(vec2(p.x,       p.y + r.y)) *        q.x  * (1.0 - q.y) +
                       noise(vec2(p.x + r.x, p.y      )) * (1.0 - q.x) *        q.y  +
                       noise(vec2(p.x + r.x, p.y + r.y)) * (1.0 - q.x) * (1.0 - q.y);
            }

            void main() {
                float n = 0.0;
                // ノイズ
                if ( u_noiseType == 0 ) {
                    vec2 t = gl_FragCoord.xy + vec2(u_time * 10.0);
                    n = noise(t);
                }
                // シームレスなノイズ
                else {
                    const float map = 256.0;
                    vec2 t = mod(gl_FragCoord.xy + vec2(u_time * 10.0), map);
                    n = snoise(t, t / map, vec2(map));
                }

                o_FragColor = vec4(vec3(n), 1.0);
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
             u_resolution: FloatArray,
             u_noiseType: Int) {
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

        // uniform(ノイズの種類)
        GLES32.glUniform1i(hUNI[3],u_noiseType)
        MyGLES32Func.checkGlError("u_noiseType",this,model)

        // モデル描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
