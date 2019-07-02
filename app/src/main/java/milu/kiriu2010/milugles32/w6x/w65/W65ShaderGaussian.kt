package milu.kiriu2010.milugles32.w6x.w65

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -----------------------------------------------------
// gaussianフィルタ用シェーダ
// -----------------------------------------------------
// ぼかしフィルタ
// -----------------------------------------------------
// https://wgld.org/d/webgl/w065.html
// -----------------------------------------------------
class W65ShaderGaussian(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec2  a_TextureCoord;
            
            uniform   mat4  u_matO;
            
            out  vec2  v_TexCoord;

            void main() {
                v_TexCoord  = a_TextureCoord;
                gl_Position = u_matO   * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform   sampler2D   u_Texture0;
            uniform   float       u_weight[5];
            uniform   int         u_horizontal;
            uniform   float       u_renderWH;
            
            in  vec2  v_TexCoord;
            
            out vec4  o_FragColor;

            void main() {
                // 512x512pixelの画像フォーマットをそのまま使う場合
                // float tFrag = 1.0 / 512.0;

                // 画像をレンダリングの幅・高さに合わせている場合に、こちらを使う
                float tFrag    = 1.0/u_renderWH;

                vec2  fc = gl_FragCoord.st;
                vec3  destColor = vec3(0.0);

                // 横方向にガウスフィルタをかける
                if(bool(u_horizontal)){
                    destColor += texture(u_Texture0, (fc + vec2(-4.0, 0.0)) * tFrag).rgb * u_weight[4];
                    destColor += texture(u_Texture0, (fc + vec2(-3.0, 0.0)) * tFrag).rgb * u_weight[3];
                    destColor += texture(u_Texture0, (fc + vec2(-2.0, 0.0)) * tFrag).rgb * u_weight[2];
                    destColor += texture(u_Texture0, (fc + vec2(-1.0, 0.0)) * tFrag).rgb * u_weight[1];
                    destColor += texture(u_Texture0, (fc + vec2( 0.0, 0.0)) * tFrag).rgb * u_weight[0];
                    destColor += texture(u_Texture0, (fc + vec2( 1.0, 0.0)) * tFrag).rgb * u_weight[1];
                    destColor += texture(u_Texture0, (fc + vec2( 2.0, 0.0)) * tFrag).rgb * u_weight[2];
                    destColor += texture(u_Texture0, (fc + vec2( 3.0, 0.0)) * tFrag).rgb * u_weight[3];
                    destColor += texture(u_Texture0, (fc + vec2( 4.0, 0.0)) * tFrag).rgb * u_weight[4];
                }
                // 縦方向にガウスフィルタをかける
                else{
                    destColor += texture(u_Texture0, (fc + vec2(0.0, -4.0)) * tFrag).rgb * u_weight[4];
                    destColor += texture(u_Texture0, (fc + vec2(0.0, -3.0)) * tFrag).rgb * u_weight[3];
                    destColor += texture(u_Texture0, (fc + vec2(0.0, -2.0)) * tFrag).rgb * u_weight[2];
                    destColor += texture(u_Texture0, (fc + vec2(0.0, -1.0)) * tFrag).rgb * u_weight[1];
                    destColor += texture(u_Texture0, (fc + vec2(0.0,  0.0)) * tFrag).rgb * u_weight[0];
                    destColor += texture(u_Texture0, (fc + vec2(0.0,  1.0)) * tFrag).rgb * u_weight[1];
                    destColor += texture(u_Texture0, (fc + vec2(0.0,  2.0)) * tFrag).rgb * u_weight[2];
                    destColor += texture(u_Texture0, (fc + vec2(0.0,  3.0)) * tFrag).rgb * u_weight[3];
                    destColor += texture(u_Texture0, (fc + vec2(0.0,  4.0)) * tFrag).rgb * u_weight[4];
                }

                o_FragColor = vec4(vec3(1.0)-destColor, 1.0);
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
        hUNI = IntArray(5)

        // uniform()
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matO")
        MyGLES32Func.checkGlError("u_matO:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_Texture0")
        MyGLES32Func.checkGlError("u_Texture:glGetUniformLocation")

        // uniform(カーネル)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle, "u_weight")
        MyGLES32Func.checkGlError("u_weight:glGetUniformLocation")

        // uniform(水平方向かどうか)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle, "u_horizontal")
        MyGLES32Func.checkGlError("u_horizontal:glGetUniformLocation")

        // uniform(レンダリング領域の大きさ)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle, "u_renderWH")
        MyGLES32Func.checkGlError("u_renderWH:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matO: FloatArray,
             u_Texture0: Int,
             u_weight: FloatArray,
             u_horizontal: Int,
             u_renderWH: Float) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram", this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform()
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matO,0)
        MyGLES32Func.checkGlError("u_matO", this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[1], u_Texture0)
        MyGLES32Func.checkGlError("u_Texture0", this,model)

        // uniform(カーネル)
        GLES32.glUniform1fv(hUNI[2], 5,u_weight,0)
        MyGLES32Func.checkGlError("u_weight", this,model)

        // uniform(水平方向かどうか)
        GLES32.glUniform1i(hUNI[3], u_horizontal)
        MyGLES32Func.checkGlError("u_horizontal", this,model)

        // uniform(画像の大きさ)
        GLES32.glUniform1f(hUNI[4], u_renderWH)
        MyGLES32Func.checkGlError("u_renderWH", this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
