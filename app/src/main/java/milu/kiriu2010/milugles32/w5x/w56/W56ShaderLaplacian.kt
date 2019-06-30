package milu.kiriu2010.milugles32.w5x.w56
        
import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -------------------------------------------------------------------------------
// シェーダ(laplacianフィルタ)
// -------------------------------------------------------------------------------
// laplacianフィルタは、エッジ(色の諧調が極端に変化しているところ)の検出が可能になる。
// 二次微分を計算することで、色の諧調差を計算する
// sobelフィルタに比べ繊細で細い線によるエッジの検出ができる
// -------------------------------------------------------------------------------
// // https://wgld.org/d/webgl/w056.html
// -------------------------------------------------------------------------------
class W56ShaderLaplacian(ctx: Context): ES32MgShader(ctx) {
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
            uniform   int         u_laplacian;
            uniform   int         u_laplacianGray;
            uniform   float       u_Coef[9];
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

                vec3  destColor     = vec3(0.0);

                destColor  += texture(u_Texture0, (fc + offset[0]) * tFrag).rgb * u_Coef[0];
                destColor  += texture(u_Texture0, (fc + offset[1]) * tFrag).rgb * u_Coef[1];
                destColor  += texture(u_Texture0, (fc + offset[2]) * tFrag).rgb * u_Coef[2];
                destColor  += texture(u_Texture0, (fc + offset[3]) * tFrag).rgb * u_Coef[3];
                destColor  += texture(u_Texture0, (fc + offset[4]) * tFrag).rgb * u_Coef[4];
                destColor  += texture(u_Texture0, (fc + offset[5]) * tFrag).rgb * u_Coef[5];
                destColor  += texture(u_Texture0, (fc + offset[6]) * tFrag).rgb * u_Coef[6];
                destColor  += texture(u_Texture0, (fc + offset[7]) * tFrag).rgb * u_Coef[7];
                destColor  += texture(u_Texture0, (fc + offset[8]) * tFrag).rgb * u_Coef[8];

                // laplacianフィルタを適用
                if(bool(u_laplacian)){
                    destColor = max(destColor, 0.0);
                }else{
                    destColor = texture(u_Texture0, v_TexCoord).rgb;
                }

                // グレースケールを適用
                if(bool(u_laplacianGray)){
                    float grayColor = dot(destColor.rgb, monochromeScale);
                    destColor = vec3(grayColor);
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
        MyGLES32Func.checkGlError("u_Texture:glGetUniformLocation")

        // uniform(laplacianフィルタを使うかどうか)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle, "u_laplacian")
        MyGLES32Func.checkGlError("u_laplacian:glGetUniformLocation")

        // uniform(グレースケールを使うかどうか)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle, "u_laplacianGray")
        MyGLES32Func.checkGlError("u_laplacianGray:glGetUniformLocation")

        // uniform(カーネル)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle, "u_Coef")
        MyGLES32Func.checkGlError("u_Coef:glGetUniformLocation")

        // uniform(レンダリング領域の大きさ)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle, "u_renderWH")
        MyGLES32Func.checkGlError("u_renderWH:glGetUniformLocation")

        return this
    }


    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_Texture0: Int,
             u_laplacian: Int,
             u_laplacianGray: Int,
             u_Coef: FloatArray,
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

        // uniform(laplacianフィルタを使うかどうか)
        GLES32.glUniform1i(hUNI[2], u_laplacian)
        MyGLES32Func.checkGlError("u_laplacian",this,model)

        // uniform(グレースケールを使うかどうか)
        GLES32.glUniform1i(hUNI[3], u_laplacianGray)
        MyGLES32Func.checkGlError("u_laplacianGray",this,model)

        // uniform(カーネル)
        GLES32.glUniform1fv(hUNI[4], 9,u_Coef,0)
        MyGLES32Func.checkGlError("u_Coef",this,model)

        // uniform(レンダリング領域の大きさ)
        GLES32.glUniform1f(hUNI[5], u_renderWH)
        MyGLES32Func.checkGlError("u_renderWH",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
