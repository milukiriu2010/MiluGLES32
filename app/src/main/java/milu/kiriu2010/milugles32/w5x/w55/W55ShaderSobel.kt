package milu.kiriu2010.milugles32.w5x.w55

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -------------------------------------------------------------------------------
// sobelフィルタ用シェーダ
// -------------------------------------------------------------------------------
// sobelフィルタは、エッジ(色の諧調が極端に変化しているところ)の検出が可能になる。
// 一次微分を計算することで、色の諧調差を計算する
// -------------------------------------------------------------------------------
// https://wgld.org/d/webgl/w055.html
// -------------------------------------------------------------------------------
class W55ShaderSobel(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec2  a_TextureCoord;
            
            uniform   mat4  u_matMVP;
            
            out  vec2  v_TexCoord;

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
            uniform   int         u_sobel;
            uniform   int         u_sobelGray;
            uniform   float       u_hCoef[9];
            uniform   float       u_vCoef[9];
            uniform   float       u_renderWH;
            
            in  vec2  v_TexCoord;
            
            out vec4  o_FragColor;

            // NTSC系加重平均法
            const float redScale   = 0.298912;
            const float greenScale = 0.586611;
            const float blueScale  = 0.114478;
            const vec3  monochromeScale = vec3(redScale, greenScale, blueScale);

            void main() {
                // 3x3の範囲のテクセルにアクセスするためのオフセット値
                vec2 offset[9];
                offset[0] = vec2(-1.0, -1.0);
                offset[1] = vec2( 0.0, -1.0);
                offset[2] = vec2( 1.0, -1.0);
                offset[3] = vec2(-1.0,  0.0);
                offset[4] = vec2( 0.0,  0.0);
                offset[5] = vec2( 1.0,  0.0);
                offset[6] = vec2(-1.0,  1.0);
                offset[7] = vec2( 0.0,  1.0);
                offset[8] = vec2( 1.0,  1.0);

                // 512x512pixelの画像フォーマットをそのまま使う場合
                // float tFrag = 1.0 / 512.0;
                // vec2  fc = vec2(gl_FragCoord.s, 512.0 - gl_FragCoord.t);

                // ---------------------------------------------------------------
                // gl_FragCoord
                //   テクスチャの各テクセルを参照する
                // gl_FragCoord.s
                //   テクスチャの横幅がピクセル単位で格納されている
                // gl_FragCoord.t
                //   テクスチャの縦幅がピクセル単位で格納されている
                // ---------------------------------------------------------------
                // 画像をレンダリングの幅・高さに合わせている場合に、こちらを使う
                float tFrag    = 1.0/u_renderWH;
                // テクスチャ座標は描画が上下逆なので、
                // 第２引数が"テクスチャの大きさ-テクスチャの縦幅"
                vec2  fc = vec2(gl_FragCoord.s, u_renderWH - gl_FragCoord.t);


                vec3  horizonColor  = vec3(0.0);
                vec3  verticalColor = vec3(0.0);
                vec4  destColor     = vec4(0.0);

                horizonColor  += texture(u_Texture0, (fc + offset[0]) * tFrag).rgb * u_hCoef[0];
                horizonColor  += texture(u_Texture0, (fc + offset[1]) * tFrag).rgb * u_hCoef[1];
                horizonColor  += texture(u_Texture0, (fc + offset[2]) * tFrag).rgb * u_hCoef[2];
                horizonColor  += texture(u_Texture0, (fc + offset[3]) * tFrag).rgb * u_hCoef[3];
                horizonColor  += texture(u_Texture0, (fc + offset[4]) * tFrag).rgb * u_hCoef[4];
                horizonColor  += texture(u_Texture0, (fc + offset[5]) * tFrag).rgb * u_hCoef[5];
                horizonColor  += texture(u_Texture0, (fc + offset[6]) * tFrag).rgb * u_hCoef[6];
                horizonColor  += texture(u_Texture0, (fc + offset[7]) * tFrag).rgb * u_hCoef[7];
                horizonColor  += texture(u_Texture0, (fc + offset[8]) * tFrag).rgb * u_hCoef[8];

                verticalColor += texture(u_Texture0, (fc + offset[0]) * tFrag).rgb * u_vCoef[0];
                verticalColor += texture(u_Texture0, (fc + offset[1]) * tFrag).rgb * u_vCoef[1];
                verticalColor += texture(u_Texture0, (fc + offset[2]) * tFrag).rgb * u_vCoef[2];
                verticalColor += texture(u_Texture0, (fc + offset[3]) * tFrag).rgb * u_vCoef[3];
                verticalColor += texture(u_Texture0, (fc + offset[4]) * tFrag).rgb * u_vCoef[4];
                verticalColor += texture(u_Texture0, (fc + offset[5]) * tFrag).rgb * u_vCoef[5];
                verticalColor += texture(u_Texture0, (fc + offset[6]) * tFrag).rgb * u_vCoef[6];
                verticalColor += texture(u_Texture0, (fc + offset[7]) * tFrag).rgb * u_vCoef[7];
                verticalColor += texture(u_Texture0, (fc + offset[8]) * tFrag).rgb * u_vCoef[8];

                // sobelフィルタを適用
                if(bool(u_sobel)){
                    destColor = vec4(vec3(sqrt(horizonColor * horizonColor + verticalColor * verticalColor)), 1.0);
                }else{
                    destColor = texture(u_Texture0, v_TexCoord);
                }

                // グレースケールを適用
                if(bool(u_sobelGray)){
                    float grayColor = dot(destColor.rgb, monochromeScale);
                    destColor = vec4(vec3(grayColor), 1.0);
                }
                o_FragColor = destColor;
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
        hUNI = IntArray(7)

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_Texture0")
        MyGLES32Func.checkGlError("u_Texture:glGetUniformLocation")

        // uniform(sobelフィルタを使うかどうか)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle, "u_sobel")
        MyGLES32Func.checkGlError("u_sobel:glGetUniformLocation")

        // uniform(グレースケールを使うかどうか)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle, "u_sobelGray")
        MyGLES32Func.checkGlError("u_sobelGray:glGetUniformLocation")

        // uniform(sobelフィルタの横方向カーネル)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle, "u_hCoef")
        MyGLES32Func.checkGlError("u_hCoef:glGetUniformLocation")

        // uniform(sobelフィルタの縦方向カーネル)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle, "u_vCoef")
        MyGLES32Func.checkGlError("u_vCoef:glGetUniformLocation")

        // uniform(レンダリング領域の大きさ)
        hUNI[6] = GLES32.glGetUniformLocation(programHandle, "u_renderWH")
        MyGLES32Func.checkGlError("u_renderWH:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_Texture0: Int,
             u_sobel: Int,
             u_sobelGray: Int,
             u_hCoef: FloatArray,
             u_vCoef: FloatArray,
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

        // uniform(sobelフィルタを使うかどうか)
        GLES32.glUniform1i(hUNI[2], u_sobel)
        MyGLES32Func.checkGlError("u_sobel",this,model)

        // uniform(グレースケールを使うかどうか)
        GLES32.glUniform1i(hUNI[3], u_sobelGray)
        MyGLES32Func.checkGlError("u_sobelGray",this,model)

        // uniform(sobelフィルタの横方向カーネル)
        GLES32.glUniform1fv(hUNI[4], 9,u_hCoef,0)
        MyGLES32Func.checkGlError("u_hCoef",this,model)

        // uniform(sobelフィルタの縦方向カーネル)
        GLES32.glUniform1fv(hUNI[5], 9,u_vCoef,0)
        MyGLES32Func.checkGlError("u_vCoef",this,model)

        // uniform(レンダリング領域の大きさ)
        GLES32.glUniform1f(hUNI[6], u_renderWH)
        MyGLES32Func.checkGlError("u_renderWH",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
