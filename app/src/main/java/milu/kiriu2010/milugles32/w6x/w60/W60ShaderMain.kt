package milu.kiriu2010.milugles32.w6x.w60

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// --------------------------------------------------------------
// シェーダ(メイン)
// --------------------------------------------------------------
// https://wgld.org/d/webgl/w060.html
// --------------------------------------------------------------
class W60ShaderMain(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec4  a_Color;
            
            uniform   mat4  u_matM;
            uniform   mat4  u_matMVP;
            uniform   mat4  u_matINV;
            uniform   vec3  u_vecLight;
            uniform   vec3  u_vecEye;
            uniform   vec4  u_ambientColor;
            uniform   float u_fogStart;
            uniform   float u_fogEnd;
            
            out  vec4  v_Color;
            out  float v_fogFactor;

            const float near = 0.1;
            const float far  = 30.0;
            const float linearDepth = 1.0/(far-near);

            void main() {
                vec3   invLight  = normalize(u_matINV * vec4(u_vecLight, 0.0)).xyz;
                vec3   invEye    = normalize(u_matINV * vec4(u_vecEye  , 0.0)).xyz;
                vec3   halfLE    = normalize(invLight + invEye);
                float  diffuse   = clamp(dot(a_Normal,invLight), 0.0, 1.0);
                float  specular  = pow(clamp(dot(a_Normal, halfLE), 0.0, 1.0), 50.0);
                vec4   amb       = a_Color * u_ambientColor;
                v_Color          = amb * vec4(vec3(diffuse),1.0) + vec4(vec3(specular),1.0);

                // モデル座標変換を適用した頂点の座標位置
                vec3   pos       = (u_matM * vec4(a_Position,1.0)).xyz;
                // "カメラとモデル頂点の距離"に定数をかけて正規化する
                //   こうすることで
                //   今処理しようとしている頂点が
                //   シーン全体のどの程度の深度にあるか
                //   0～1の範囲で表される
                float  linearPos = length(u_vecEye-pos) * linearDepth;
                v_fogFactor      = clamp((u_fogEnd-linearPos)/(u_fogEnd-u_fogStart), 0.0, 1.0);

                gl_Position      = u_matMVP   * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform   vec4   u_fogColor;
            
            in  vec4   v_Color;
            in  float  v_fogFactor;
            
            out vec4  o_FragColor;

            void main() {
                // mix(x,y,a)
                // x(1-a)+y*aを返す(つまり線形補間)
                o_FragColor = mix(u_fogColor, v_Color, v_fogFactor);
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
        hUNI = IntArray(9)

        // uniform(モデル座標変換行列)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matM")
        MyGLES32Func.checkGlError("u_matM:glGetUniformLocation")

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(逆行列)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_matINV")
        MyGLES32Func.checkGlError("u_matINV:glGetUniformLocation")

        // uniform(光源位置)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_vecLight")
        MyGLES32Func.checkGlError("u_vecLight:glGetUniformLocation")

        // uniform(視点座標)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_vecEye")
        MyGLES32Func.checkGlError("u_vecEye:glGetUniformLocation")

        // uniform(環境色)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle, "u_ambientColor")
        MyGLES32Func.checkGlError("u_ambientColor:glGetUniformLocation")

        // uniform(フォグが掛かり始める最初の位置)
        hUNI[6] = GLES32.glGetUniformLocation(programHandle, "u_fogStart")
        MyGLES32Func.checkGlError("u_fogStart:glGetUniformLocation")

        // uniform(完全にフォグが掛かりモデルが見えなくなってしまう位置)
        hUNI[7] = GLES32.glGetUniformLocation(programHandle, "u_fogEnd")
        MyGLES32Func.checkGlError("u_fogEnd:glGetUniformLocation")

        // uniform(フォグの色)
        hUNI[8] = GLES32.glGetUniformLocation(programHandle, "u_fogColor")
        MyGLES32Func.checkGlError("u_fogColor:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matMVP: FloatArray,
             u_matI: FloatArray,
             u_vecLight: FloatArray,
             u_vecEye: FloatArray,
             u_ambientColor: FloatArray,
             u_fogStart: Float,
             u_fogEnd: Float,
             u_fogColor: FloatArray) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル座標変換行列)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matM,0)
        MyGLES32Func.checkGlError("u_matM",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(逆行列)
        GLES32.glUniformMatrix4fv(hUNI[2],1,false,u_matI,0)
        MyGLES32Func.checkGlError("u_matINV",this,model)

        // uniform(光源位置)
        GLES32.glUniform3fv(hUNI[3],1,u_vecLight,0)
        MyGLES32Func.checkGlError("u_vecLight",this,model)

        // uniform(視点座標)
        GLES32.glUniform3fv(hUNI[4],1,u_vecEye,0)
        MyGLES32Func.checkGlError("u_vecEye",this,model)

        // uniform(環境色)
        GLES32.glUniform4fv(hUNI[5], 1,u_ambientColor,0)
        MyGLES32Func.checkGlError("u_ambientColor",this,model)

        // uniform(フォグが掛かり始める最初の位置)
        GLES32.glUniform1f(hUNI[6], u_fogStart)
        MyGLES32Func.checkGlError("u_fogStart",this,model)

        // uniform(完全にフォグが掛かりモデルが見えなくなってしまう位置)
        GLES32.glUniform1f(hUNI[7], u_fogEnd)
        MyGLES32Func.checkGlError("u_fogEnd",this,model)

        // uniform(フォグの色)
        GLES32.glUniform4fv(hUNI[8], 1,u_fogColor,0)
        MyGLES32Func.checkGlError("u_fogColor",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}