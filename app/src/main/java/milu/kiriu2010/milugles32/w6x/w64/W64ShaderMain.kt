package milu.kiriu2010.milugles32.w6x.w64

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -------------------------------------
// シェーダ(メイン)
// -------------------------------------
// https://wgld.org/d/webgl/w064.html
// -------------------------------------
class W64ShaderMain(ctx: Context): ES32MgShader(ctx) {
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
            // カメラの注視点
            uniform   vec3  u_vecCenter;
            uniform   vec3  u_vecEye;
            uniform   vec4  u_colorRim;
            // リムライトの強さ係数
            uniform   float u_rimCoef;
            
            out  vec4  v_Color;

            void main() {
                vec3   invLight   = normalize(u_matINV * vec4(u_vecLight, 0.0)).xyz;
                vec3   invEye     = normalize(u_matINV * vec4(u_vecEye  , 0.0)).xyz;
                vec3   halfLE     = normalize(invLight + invEye);
                float  diffuse    = clamp(dot(a_Normal,invLight), 0.1, 1.0);
                float  specular   = pow(clamp(dot(a_Normal, halfLE), 0.0, 1.0), 50.0);
                // リムライティングの係数
                // ----------------------------------------------------------------------
                // 視線ベクトルと法線ベクトルの角度が直角に近づけば近づくほど、
                // ライティングの係数が大きくなるようにする
                // ----------------------------------------------------------------------
                // rimの値が0の場合、
                // 視線ベクトルとライトベクトルの計算がどのような結果でも
                // リムライトは当たらない
                // ----------------------------------------------------------------------
                // powを使ってコントラストを強くしている
                // ----------------------------------------------------------------------
                float  rim        = pow(1.0 - clamp(dot(a_Normal,invEye),0.0,1.0), 5.0);
                // ----------------------------------------------------------------------
                // 視線ベクトルとライトベクトルとの間で内積をとることで、
                // ２つのベクトルがどの程度向かいあっているかを係数化する
                // ----------------------------------------------------------------------
                // powを使ってコントラストを強くしている
                // ----------------------------------------------------------------------
                float  dotLE      = pow(max(dot(normalize(u_vecCenter-u_vecEye),normalize(u_vecLight)), 0.0), 30.0);
                vec4   ambient    = u_colorRim * u_rimCoef * rim * dotLE;
                v_Color         = a_Color * vec4(vec3(diffuse),1.0) + vec4(vec3(specular),1.0) + vec4(ambient.rgb, 1.0);
                gl_Position     = u_matMVP * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            in  vec4  v_Color;
            
            out vec4  o_FragColor;

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

        // uniform(光源位置)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_vecLight")
        MyGLES32Func.checkGlError("u_vecLight:glGetUniformLocation")

        // uniform()
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_vecCenter")
        MyGLES32Func.checkGlError("u_vecCenter:glGetUniformLocation")

        // uniform(視点座標)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle,"u_vecEye")
        MyGLES32Func.checkGlError("u_vecEye:glGetUniformLocation")

        // uniform()
        hUNI[6] = GLES32.glGetUniformLocation(programHandle, "u_colorRim")
        MyGLES32Func.checkGlError("u_colorRim:glGetUniformLocation")

        // uniform()
        hUNI[7] = GLES32.glGetUniformLocation(programHandle, "u_rimCoef")
        MyGLES32Func.checkGlError("u_rimCoef:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matMVP: FloatArray,
             u_matI: FloatArray,
             u_vecLight: FloatArray,
             u_vecCenter: FloatArray,
             u_vecEye: FloatArray,
             u_colorRim: FloatArray,
             u_rimCoef: Float) {
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

        // uniform()
        GLES32.glUniform3fv(hUNI[4],1,u_vecCenter,0)
        MyGLES32Func.checkGlError("u_vecCenter",this,model)

        // uniform(視点座標)
        GLES32.glUniform3fv(hUNI[5],1,u_vecEye,0)
        MyGLES32Func.checkGlError("u_vecEye",this,model)

        // uniform()
        GLES32.glUniform4fv(hUNI[6], 1,u_colorRim,0)
        MyGLES32Func.checkGlError("u_colorRim",this,model)

        // uniform()
        GLES32.glUniform1f(hUNI[7], u_rimCoef)
        MyGLES32Func.checkGlError("u_rimCoef",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}