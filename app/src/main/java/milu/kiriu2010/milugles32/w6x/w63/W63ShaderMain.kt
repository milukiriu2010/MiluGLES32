package milu.kiriu2010.milugles32.w6x.w63

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ----------------------------------------------------------
// シェーダ(メイン)
// ----------------------------------------------------------
//   半球ライティング
//   ３次元空間を１つの球に見立ててライティングを行う
//   光の乱反射を再現する際に、
//   上空の方に向いている面は空の色
//   地面の方を向いている面は地面の色にそれぞれ塗り分ける。
// ----------------------------------------------------------
// https://wgld.org/d/webgl/w063.html
// ----------------------------------------------------------
class W63ShaderMain(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec4  a_Color;
            
            uniform   mat4  u_matM;
            uniform   mat4  u_matMVP;
            uniform   mat4  u_matINV;
            uniform   vec3  u_vecSky;
            uniform   vec3  u_vecLight;
            uniform   vec3  u_vecEye;
            uniform   vec4  u_colorSky;
            uniform   vec4  u_colorGround;
            
            out  vec4  v_Color;

            // 半球ライティングの式
            //   = 天空の色 x cos(t) - 地面の色 x cos(t)
            // 正規化式
            //   = ((天空の色 x cos(t) - 地面の色 x cos(t)) + 1.0) * 0.5
            void main() {
                vec3   invSky     = normalize(u_matINV * vec4(u_vecSky  , 0.0)).xyz;
                vec3   invLight   = normalize(u_matINV * vec4(u_vecLight, 0.0)).xyz;
                vec3   invEye     = normalize(u_matINV * vec4(u_vecEye  , 0.0)).xyz;
                vec3   halfLE     = normalize(invLight + invEye);
                float  diffuse    = clamp(dot(a_Normal,invLight), 0.1, 1.0);
                float  specular   = pow(clamp(dot(a_Normal, halfLE), 0.0, 1.0), 50.0);
                // 1を足し2で割ることで 0.0～1.0 に収まる。すなわち正規化される。
                float  hemisphere = (dot(a_Normal,invSky)+1.0)*0.5;
                // 色の線形合成を行う
                vec4   ambient    = mix(u_colorGround, u_colorSky, hemisphere);
                v_Color         = a_Color * vec4(vec3(diffuse),1.0) + vec4(vec3(specular),1.0) + ambient;
                gl_Position     = u_matMVP * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            in   vec4  v_Color;
            
            out  vec4  o_FragColor;

            void main() {
                o_FragColor = v_Color;
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

        // uniform(モデル座標変換行列)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matM")
        MyGLES32Func.checkGlError("u_matM:glGetUniformLocation")

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(逆行列)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_matINV")
        MyGLES32Func.checkGlError("u_matINV:glGetUniformLocation")

        // uniform(天空の向き)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_vecSky")
        MyGLES32Func.checkGlError("u_vecSky:glGetUniformLocation")

        // uniform(光源位置)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_vecLight")
        MyGLES32Func.checkGlError("u_vecLight:glGetUniformLocation")

        // uniform(視点座標)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle,"u_vecEye")
        MyGLES32Func.checkGlError("u_vecEye:glGetUniformLocation")

        // uniform(天空の色)
        hUNI[6] = GLES32.glGetUniformLocation(programHandle, "u_colorSky")
        MyGLES32Func.checkGlError("u_colorSky:glGetUniformLocation")

        // uniform(地面の色)
        hUNI[7] = GLES32.glGetUniformLocation(programHandle, "u_colorGround")
        MyGLES32Func.checkGlError("u_colorGround:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matMVP: FloatArray,
             u_matI: FloatArray,
             u_vecSky: FloatArray,
             u_vecLight: FloatArray,
             u_vecEye: FloatArray,
             u_colorSky: FloatArray,
             u_colorGround: FloatArray) {
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

        // uniform(天空の向き)
        GLES32.glUniform3fv(hUNI[3],1,u_vecSky,0)
        MyGLES32Func.checkGlError("u_vecSky",this,model)

        // uniform(光源位置)
        GLES32.glUniform3fv(hUNI[4],1,u_vecLight,0)
        MyGLES32Func.checkGlError("u_vecLight",this,model)

        // uniform(視点座標)
        GLES32.glUniform3fv(hUNI[5],1,u_vecEye,0)
        MyGLES32Func.checkGlError("u_vecEye",this,model)

        // uniform(天空の色)
        GLES32.glUniform4fv(hUNI[6], 1,u_colorSky,0)
        MyGLES32Func.checkGlError("u_colorSky",this,model)

        // uniform(地面の色)
        GLES32.glUniform4fv(hUNI[7], 1,u_colorGround,0)
        MyGLES32Func.checkGlError("u_colorGround",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}