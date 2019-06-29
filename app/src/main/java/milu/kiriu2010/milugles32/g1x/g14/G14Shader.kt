package milu.kiriu2010.milugles32.g1x.g14

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ---------------------------------------
// レイマーチング(トーラス＋床)
// ---------------------------------------
// https://wgld.org/d/glsl/g014.html
// ---------------------------------------
class G14Shader(ctx: Context): ES32MgShader(ctx) {
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
            // 法線の出力をするかどうか
            //   0: ライティング
            //   1: 法線
            uniform   int       u_showNormal;

            out vec4  o_FragColor;

            const float PI = 3.14159265;
            const float angle = 60.0;
            const float fov = angle * 0.5 * PI / 180.0;

            const vec3 cPos = vec3(0.0,  0.0,  3.0);
            const vec3 cDir = vec3(0.0,  0.0, -1.0);
            const vec3 cUp  = vec3(0.0,  1.0,  0.0);

            // 光源位置
            const vec3  lightDir = vec3(-0.57, 0.57, 0.57);

            // torus distance function
            float distFuncTorus(vec3 p){
                vec2 t = vec2(0.75, 0.25);
                vec2 r = vec2(length(p.xy) - t.x, p.z);
                return length(r) - t.y;
            }

            // floor distance function
            float distFuncFloor(vec3 p){
                return dot(p, vec3(0.0, 1.0, 0.0)) + 1.0;
            }

            // distance function
            float distFunc(vec3 p){
                float d1 = distFuncTorus(p);
                float d2 = distFuncFloor(p);
                return min(d1, d2);
            }

            // ---------------------------------------
            // 法線を算出する
            // ---------------------------------------
            //   p: レイとオブジェクトの交点の座標位置
            // ---------------------------------------
            vec3 genNormal(vec3 p){
                float d = 0.0001;
                // -----------------------------------------
                // 正負それぞれにほんの少しだけずらした座標を
                // distanceFuncに渡すことによって、
                // その戻り値から勾配を計算している
                // これにより、各軸に対して
                // どの程度の傾きになっているかわかる
                // -----------------------------------------
                return normalize(vec3(
                    distFunc(p + vec3(  d, 0.0, 0.0)) - distFunc(p + vec3( -d, 0.0, 0.0)),
                    distFunc(p + vec3(0.0,   d, 0.0)) - distFunc(p + vec3(0.0,  -d, 0.0)),
                    distFunc(p + vec3(0.0, 0.0,   d)) - distFunc(p + vec3(0.0, 0.0,  -d))
                ));
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

                // camera and ray
                vec3 cSide = cross(cDir, cUp);
                float targetDepth = 1.0;
                vec3 ray = normalize(cSide * p.x + cUp * p.y + cDir * targetDepth);

                // marching loop
                float tmp, dist;
                tmp = 0.0;
                vec3 dPos = cPos;
                for(int i = 0; i < 256; i++){
                    dist = distFunc(dPos);
                    tmp += dist;
                    dPos = cPos + tmp * ray;
                }

                // hit check
                if (abs(dist) < 0.001) {
                    vec3  normal = genNormal(dPos);
                    // 法線の出力
                    if ( bool(u_showNormal) ) {
                        o_FragColor = vec4(normal,1.0);
                    }
                    // ライティング
                    else {
                        float diff  = clamp(dot(lightDir,normal), 0.1, 1.0);
                        o_FragColor = vec4(vec3(diff), 1.0);
                    }
                }
                else {
                    o_FragColor = vec4(vec3(0.0), 1.0);
                }
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

        // uniform(法線の出力をするかどうか)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_showNormal")
        MyGLES32Func.checkGlError("u_showNormal:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_time: Float,
             u_mouse: FloatArray,
             u_resolution: FloatArray,
             u_showNormal: Int) {
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

        // uniform(法線の出力をするかどうか)
        GLES32.glUniform1i(hUNI[3],u_showNormal)
        MyGLES32Func.checkGlError("u_showNormal",this,model)

        // モデル描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
