package milu.kiriu2010.milugles32.w4x.w41

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -----------------------------------
// シェーダ(ブラー)
// -----------------------------------
// https://wgld.org/d/webgl/w041.html
// -----------------------------------
class W41ShaderBlur(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec4  a_Color;
            
            uniform   mat4  u_matMVP;
            
            out   vec4  v_Color;

            void main() {
                v_Color        = a_Color;
                gl_Position    = u_matMVP * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp   float;

            uniform   sampler2D u_Texture0;
            uniform   int       u_useBlur;
            uniform   float     u_renderWH;
            
            in   vec4  v_Color;
            
            out  vec4  o_FragColor;

            void main() {
                // 256x256pixelの画像フォーマットをそのまま使う場合
                //vec2 tFrag     = vec2(1.0/256);

                // gl_FragCoord
                //   これから描かれようとしているフラグメントのピクセル単位の座標
                // tFrag
                //   gl_FragCoord を参照して得られた値を、テクスチャ座標に変換するために使う
                // ----------------------------------------------------------------------
                // 画像をレンダリングの幅・高さに合わせている場合に、こちらを使う
                vec2 tFrag     = vec2(1.0/u_renderWH);
                vec4 destColor = texture(u_Texture0, gl_FragCoord.st * tFrag);
                if(bool(u_useBlur)){
                    destColor *= 0.36;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2(-1.0,  1.0)) * tFrag) * 0.04;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 0.0,  1.0)) * tFrag) * 0.04;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 1.0,  1.0)) * tFrag) * 0.04;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2(-1.0,  0.0)) * tFrag) * 0.04;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 1.0,  0.0)) * tFrag) * 0.04;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2(-1.0, -1.0)) * tFrag) * 0.04;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 0.0, -1.0)) * tFrag) * 0.04;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 1.0, -1.0)) * tFrag) * 0.04;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2(-2.0,  2.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2(-1.0,  2.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 0.0,  2.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 1.0,  2.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 2.0,  2.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2(-2.0,  1.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 2.0,  1.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2(-2.0,  0.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 2.0,  0.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2(-2.0, -1.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 2.0, -1.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2(-2.0, -2.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2(-1.0, -2.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 0.0, -2.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 1.0, -2.0)) * tFrag) * 0.02;
                    destColor += texture(u_Texture0, (gl_FragCoord.st + vec2( 2.0, -2.0)) * tFrag) * 0.02;
                }
                o_FragColor = v_Color * destColor;
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
        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(ブラーするかどうか)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_useBlur")
        MyGLES32Func.checkGlError("u_useBlur:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle, "u_Texture0")
        MyGLES32Func.checkGlError("u_Texture0:glGetUniformLocation")

        // uniform(レンダリング領域の幅・高さ)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle, "u_renderWH")
        MyGLES32Func.checkGlError("u_renderWH:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_useBlur: Int,
             u_Texture0: Int,
             u_renderWH: Float) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UserProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(ブラーするかどうか)
        GLES32.glUniform1i(hUNI[1],u_useBlur)
        MyGLES32Func.checkGlError("u_useBlur",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[2], u_Texture0)
        MyGLES32Func.checkGlError("u_Texture0",this,model)

        // uniform(レンダリング領域の幅・高さ)
        GLES32.glUniform1f(hUNI[3], u_renderWH)
        MyGLES32Func.checkGlError("u_renderWH",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
