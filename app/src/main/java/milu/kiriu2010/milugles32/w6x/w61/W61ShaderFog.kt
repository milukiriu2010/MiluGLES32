package milu.kiriu2010.milugles32.w6x.w61

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------------------------------
// シェーダ(フォグ)
// ------------------------------------------------------------
// https://wgld.org/d/webgl/w061.html
// ------------------------------------------------------------
class W61ShaderFog(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec4  a_Color;
            layout (location = 2) in vec2  a_TextureCoord;
            
            uniform   mat4  u_matM;
            uniform   mat4  u_matMVP;
            uniform   mat4  u_matTex;
            
            out  vec4  v_Position;
            out  vec4  v_Color;
            out  vec2  v_TextureCoord;
            out  vec4  v_TexProjCoord;

            void main() {
                vec3   pos      = (u_matM * vec4(a_Position, 1.0)).xyz;
                v_Position      = u_matMVP * vec4(a_Position, 1.0);
                v_Color         = a_Color;
                // テクスチャ座標は、
                // フラグメントシェーダ側で半月形に見えるように
                // アルファ値を加工するために使う
                v_TextureCoord  = a_TextureCoord;
                v_TexProjCoord  = u_matTex * vec4(pos, 1.0);
                gl_Position     = v_Position;
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            // ----------------------------------------------------------------
            // テクスチャ座標をずらすために使う
            // 全てのパーティクルが同じノイズを表示しているのは、かっこよくないので、
            // フラグメントシェーダ側でテクスチャ座標をずらすために使う
            // ----------------------------------------------------------------
            uniform   vec2      u_offset;
            // ----------------------------------------------------------------
            // 他のモデルの深度とパーティクルの深度の差が、
            // どのくらい近い場合にアルファ値を操作するのかを決めるための係数
            //   0.0 - 1.0
            // パーティクル以外のモデルの深度、
            // すなわちオフスクリーンレンダリングされたフレームバッファから
            // 読みだした深度が仮に0.5で、u_distLengthが仮に0.1だったとすると、
            // パーティクルの深度が0.4-0.6の範囲に収まるとき、
            // アルファ値に影響を及ぼす
            // ----------------------------------------------------------------
            uniform   float     u_distLength;
            uniform   sampler2D u_TextureDepth;
            uniform   sampler2D u_TextureNoise;
            uniform   int       u_softParticle;
            
            in  vec4  v_Position;
            in  vec4  v_Color;
            in  vec2  v_TextureCoord;
            in  vec4  v_TexProjCoord;
            
            out vec4  o_FragColor;

            // フレームバッファに描きこまれた深度値を本来の値に変換する
            float restDepth(vec4 RGBA) {
                const float rMask = 1.0;
                const float gMask = 1.0/255.0;
                const float bMask = 1.0/(255.0*255.0);
                const float aMask = 1.0/(255.0*255.0*255.0);
                float depth = dot(RGBA, vec4(rMask, gMask, bMask, aMask));
                return depth;
            }

            const float near = 0.1;
            const float far  = 10.0;
            const float linearDepth = 1.0/(far-near);

            void main() {
                // 射影テクスチャマッピングを使ってオフスクリーンレンダリングした深度値
                // すなわち、パーティクル以外のモデルの深度値を読み出す
                float depth      = restDepth(textureProj(u_TextureDepth, v_TexProjCoord));
                // パーティクル自身の深度値
                float linearPos  = linearDepth * length(v_Position);
                // オフセットを適用したノイズテクスチャの色情報を取得
                vec4  noiseColor = texture(u_TextureNoise, v_TextureCoord + u_offset);
                // パーティクルを半月形になるように透明度を調整している
                // (0.5,1.0)というテクスチャ座標位置とパーティクルのテクスチャ座標との距離を
                // クランプ下上で1.0から減算する。
                // こうすると、(0.5,1.0)を中心として、
                // 円形に透明度が徐々に高くなっていくようなアルファ値を適用できる
                // (0.5,1.0)をずらすと、半月形だけでなく円形に透明度を適用することもできる。
                float alpha      = 1.0 - clamp(length(vec2(0.5,1.0) - v_TextureCoord)*2.0, 0.0, 1.0);
                if (bool(u_softParticle)) {
                    float distance = abs(depth-linearPos);
                    if (u_distLength >= distance) {
                        float d = distance/u_distLength;
                        alpha *= d;
                    }
                }
                o_FragColor = vec4(v_Color.rgb, noiseColor.r * alpha);
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
        hUNI = IntArray(8)

        // uniform(モデル)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matM")
        MyGLES32Func.checkGlError("u_matM:glGetUniformLocation")

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform()
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_matTex")
        MyGLES32Func.checkGlError("u_matTex:glGetUniformLocation")

        // uniform()
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_offset")
        MyGLES32Func.checkGlError("u_offset:glGetUniformLocation")

        // uniform()
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_distLength")
        MyGLES32Func.checkGlError("u_distLength:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle, "u_TextureDepth")
        MyGLES32Func.checkGlError("u_TextureDepth:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[6] = GLES32.glGetUniformLocation(programHandle, "u_TextureNoise")
        MyGLES32Func.checkGlError("u_TextureNoise:glGetUniformLocation")

        // uniform()
        hUNI[7] = GLES32.glGetUniformLocation(programHandle, "u_softParticle")
        MyGLES32Func.checkGlError("u_softParticle:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matMVP: FloatArray,
             u_matTex: FloatArray,
             u_offset: FloatArray,
             u_distLength: Float,
             u_TextureDepth: Int,
             u_TextureNoise: Int,
             u_softParticle: Int) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matM,0)
        MyGLES32Func.checkGlError("u_matM",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform()
        GLES32.glUniformMatrix4fv(hUNI[2],1,false,u_matTex,0)
        MyGLES32Func.checkGlError("u_matTex",this,model)

        // uniform()
        GLES32.glUniform2fv(hUNI[3],1,u_offset,0)
        MyGLES32Func.checkGlError("u_offset",this,model)

        // uniform()
        GLES32.glUniform1f(hUNI[4],u_distLength)
        MyGLES32Func.checkGlError("u_distLength",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[5], u_TextureDepth)
        MyGLES32Func.checkGlError("u_TextureDepth",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[6], u_TextureNoise)
        MyGLES32Func.checkGlError("u_TextureNoise",this,model)

        // uniform()
        GLES32.glUniform1i(hUNI[7], u_softParticle)
        MyGLES32Func.checkGlError("u_softParticle",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}