package milu.kiriu2010.milugles32.w5x.w58

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// --------------------------------------------
// gaussianフィルタ用シェーダ
// --------------------------------------------
// ぼかしフィルタ
// --------------------------------------------
// https://wgld.org/d/webgl/w058.html
// --------------------------------------------
class W58ShaderGaussian(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec2  a_TextureCoord;
            
            uniform   mat4  u_matMVP;
            
            out  vec2  v_TexCoord;

            // 正射影での座標変換行列が入ってくるようにする
            void main() {
                v_TexCoord      = a_TextureCoord;
                gl_Position     = u_matMVP   * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform   sampler2D   u_Texture0;
            uniform   int         u_gaussian;
            uniform   float       u_weight[10];
            uniform   int         u_horizontal;
            uniform   float       u_renderWH;
            
            in  vec2  v_TexCoord;
            
            out vec4  o_FragColor;

            void main() {
                // 512x512pixelの画像フォーマットをそのまま使う場合
                // float tFrag = 1.0 / 512.0;

                // 画像をレンダリングの幅・高さに合わせている場合に、こちらを使う
                float tFrag    = 1.0/u_renderWH;

                vec2  fc;
                vec3  destColor = vec3(0.0);

                if(bool(u_gaussian)){
                    // 横方向にガウスフィルタをかける
                    if(bool(u_horizontal)){
                        // 512x512pixelの画像フォーマットをそのまま使う場合
                        // fc = vec2(gl_FragCoord.s, 512.0 - gl_FragCoord.t);

                        // 画像をレンダリングの幅・高さに合わせている場合に、こちらを使う
                        fc = vec2(gl_FragCoord.s, u_renderWH - gl_FragCoord.t);

                        destColor += texture(u_Texture0, (fc + vec2(-9.0, 0.0)) * tFrag).rgb * u_weight[9];
                        destColor += texture(u_Texture0, (fc + vec2(-8.0, 0.0)) * tFrag).rgb * u_weight[8];
                        destColor += texture(u_Texture0, (fc + vec2(-7.0, 0.0)) * tFrag).rgb * u_weight[7];
                        destColor += texture(u_Texture0, (fc + vec2(-6.0, 0.0)) * tFrag).rgb * u_weight[6];
                        destColor += texture(u_Texture0, (fc + vec2(-5.0, 0.0)) * tFrag).rgb * u_weight[5];
                        destColor += texture(u_Texture0, (fc + vec2(-4.0, 0.0)) * tFrag).rgb * u_weight[4];
                        destColor += texture(u_Texture0, (fc + vec2(-3.0, 0.0)) * tFrag).rgb * u_weight[3];
                        destColor += texture(u_Texture0, (fc + vec2(-2.0, 0.0)) * tFrag).rgb * u_weight[2];
                        destColor += texture(u_Texture0, (fc + vec2(-1.0, 0.0)) * tFrag).rgb * u_weight[1];
                        destColor += texture(u_Texture0, (fc + vec2( 0.0, 0.0)) * tFrag).rgb * u_weight[0];
                        destColor += texture(u_Texture0, (fc + vec2( 1.0, 0.0)) * tFrag).rgb * u_weight[1];
                        destColor += texture(u_Texture0, (fc + vec2( 2.0, 0.0)) * tFrag).rgb * u_weight[2];
                        destColor += texture(u_Texture0, (fc + vec2( 3.0, 0.0)) * tFrag).rgb * u_weight[3];
                        destColor += texture(u_Texture0, (fc + vec2( 4.0, 0.0)) * tFrag).rgb * u_weight[4];
                        destColor += texture(u_Texture0, (fc + vec2( 5.0, 0.0)) * tFrag).rgb * u_weight[5];
                        destColor += texture(u_Texture0, (fc + vec2( 6.0, 0.0)) * tFrag).rgb * u_weight[6];
                        destColor += texture(u_Texture0, (fc + vec2( 7.0, 0.0)) * tFrag).rgb * u_weight[7];
                        destColor += texture(u_Texture0, (fc + vec2( 8.0, 0.0)) * tFrag).rgb * u_weight[8];
                        destColor += texture(u_Texture0, (fc + vec2( 9.0, 0.0)) * tFrag).rgb * u_weight[9];
                    }
                    // 縦方向にガウスフィルタをかける
                    else{
                        fc = gl_FragCoord.st;
                        destColor += texture(u_Texture0, (fc + vec2(0.0, -9.0)) * tFrag).rgb * u_weight[9];
                        destColor += texture(u_Texture0, (fc + vec2(0.0, -8.0)) * tFrag).rgb * u_weight[8];
                        destColor += texture(u_Texture0, (fc + vec2(0.0, -7.0)) * tFrag).rgb * u_weight[7];
                        destColor += texture(u_Texture0, (fc + vec2(0.0, -6.0)) * tFrag).rgb * u_weight[6];
                        destColor += texture(u_Texture0, (fc + vec2(0.0, -5.0)) * tFrag).rgb * u_weight[5];
                        destColor += texture(u_Texture0, (fc + vec2(0.0, -4.0)) * tFrag).rgb * u_weight[4];
                        destColor += texture(u_Texture0, (fc + vec2(0.0, -3.0)) * tFrag).rgb * u_weight[3];
                        destColor += texture(u_Texture0, (fc + vec2(0.0, -2.0)) * tFrag).rgb * u_weight[2];
                        destColor += texture(u_Texture0, (fc + vec2(0.0, -1.0)) * tFrag).rgb * u_weight[1];
                        destColor += texture(u_Texture0, (fc + vec2(0.0,  0.0)) * tFrag).rgb * u_weight[0];
                        destColor += texture(u_Texture0, (fc + vec2(0.0,  1.0)) * tFrag).rgb * u_weight[1];
                        destColor += texture(u_Texture0, (fc + vec2(0.0,  2.0)) * tFrag).rgb * u_weight[2];
                        destColor += texture(u_Texture0, (fc + vec2(0.0,  3.0)) * tFrag).rgb * u_weight[3];
                        destColor += texture(u_Texture0, (fc + vec2(0.0,  4.0)) * tFrag).rgb * u_weight[4];
                        destColor += texture(u_Texture0, (fc + vec2(0.0,  5.0)) * tFrag).rgb * u_weight[5];
                        destColor += texture(u_Texture0, (fc + vec2(0.0,  6.0)) * tFrag).rgb * u_weight[6];
                        destColor += texture(u_Texture0, (fc + vec2(0.0,  7.0)) * tFrag).rgb * u_weight[7];
                        destColor += texture(u_Texture0, (fc + vec2(0.0,  8.0)) * tFrag).rgb * u_weight[8];
                        destColor += texture(u_Texture0, (fc + vec2(0.0,  9.0)) * tFrag).rgb * u_weight[9];
                    }
                }else{
                    destColor = texture(u_Texture0, v_TexCoord).rgb;
                }

                o_FragColor = vec4(destColor, 1.0);
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
        hUNI = IntArray(6)

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_Texture0")
        MyGLES32Func.checkGlError("u_Texture0:glGetUniformLocation")

        // uniform(gaussianフィルタを使うかどうか)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle, "u_gaussian")
        MyGLES32Func.checkGlError("u_gaussian:glGetUniformLocation")

        // uniform(カーネル)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle, "u_weight")
        MyGLES32Func.checkGlError("u_weight:glGetUniformLocation")

        // uniform(水平方向かどうか)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle, "u_horizontal")
        MyGLES32Func.checkGlError("u_horizontal:glGetUniformLocation")

        // uniform(レンダリング領域の大きさ)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle, "u_renderWH")
        MyGLES32Func.checkGlError("u_renderWH:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_Texture0: Int,
             u_gaussian: Int,
             u_weight: FloatArray,
             u_horizontal: Int,
             u_renderWH: Float) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[1],u_Texture0)
        MyGLES32Func.checkGlError("u_Texture0",this,model)

        // uniform(gaussianフィルタを使うかどうか)
        GLES32.glUniform1i(hUNI[2], u_gaussian)
        MyGLES32Func.checkGlError("u_gaussian",this,model)

        // uniform(カーネル)
        GLES32.glUniform1fv(hUNI[3], 10,u_weight,0)
        MyGLES32Func.checkGlError("u_weight",this,model)

        // uniform(水平方向かどうか)
        GLES32.glUniform1i(hUNI[4], u_horizontal)
        MyGLES32Func.checkGlError("u_horizontal",this,model)

        // uniform(レンダリング領域の大きさ)
        GLES32.glUniform1f(hUNI[5], u_renderWH)
        MyGLES32Func.checkGlError("u_renderWH",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
