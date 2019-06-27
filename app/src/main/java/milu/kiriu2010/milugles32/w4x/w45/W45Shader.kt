package milu.kiriu2010.milugles32.w4x.w45

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -------------------------------------------------
// シェーダ for キューブ環境バンプマッピング
// -------------------------------------------------
// バンプマッピングでは法線マップを参照するために、
// 頂点属性としてテクスチャ座標を定義する必要がある
// -------------------------------------------------
// https://wgld.org/d/webgl/w045.html
// -------------------------------------------------
class W45Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec4  a_Color;
            layout (location = 3) in vec2  a_TextureCoord;
            
            uniform   mat4  u_matM;
            uniform   mat4  u_matMVP;
            
            out  vec3  v_Position;
            out  vec4  v_Color;
            out  vec2  v_TextureCoord;
            out  vec3  v_tNormal;
            out  vec3  v_tTangent;

            void main() {
                v_Position     = (u_matM * vec4(a_Position,1.0)).xyz;
                v_Color        = a_Color;
                v_TextureCoord = a_TextureCoord;
                // 法線ベクトル
                v_tNormal      = (u_matM * vec4(a_Normal  ,0.0)).xyz;
                // 接線ベクトル
                // = 法線ベクトルとY軸の外積
                v_tTangent     = cross(v_tNormal,vec3(0.0,1.0,0.0));
                gl_Position    = u_matMVP * vec4(a_Position,1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform   vec3        u_vecEye;
            uniform   sampler2D   u_normalMap;
            uniform   samplerCube u_CubeTexture;
            uniform   int         u_Reflection;
            
            in  vec3  v_Position;
            in  vec4  v_Color;
            in  vec2  v_TextureCoord;
            // 法線ベクトル
            in  vec3  v_tNormal;
            // 接線ベクトル
            in  vec3  v_tTangent;
            
            out vec4  o_FragColor;

            void main() {
                // 法線ベクトルと接線ベクトルを使って従法線ベクトルを算出
                vec3  tBinormal = cross(v_tNormal, v_tTangent);
                // 法線マップから抜き出したバンプマッピング用の法線情報を
                // 視線空間上へと変換するために3x3の行列を生成
                // この行列を法線マップから抜き出した法線ベクトルと掛け合わせることで、
                // 接空間上にある法線ベクトルを視線空間へ変換する。
                mat3  mView     = mat3(v_tTangent, tBinormal, v_tNormal);
                // 法線マップから法線ベクトルを抜き出す＆接空間上の法線ベクトルを視線空間へ変換する
                vec3  mNormal   = mView * (texture(u_normalMap,v_TextureCoord)*2.0-1.0).rgb;
                vec3  ref;
                if (bool(u_Reflection)) {
                    ref = reflect(v_Position - u_vecEye, mNormal);
                }
                else {
                    ref = v_tNormal;
                }
                // キューブマップテクスチャからフラグメントの情報を抜き出す
                // u_Reflection=1 ⇒ 反射に対応する色
                // u_Reflection=0 ⇒ 背景の色
                vec4  envColor  = texture(u_CubeTexture, ref);
                vec4  destColor = v_Color * envColor;
                o_FragColor    = destColor;
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
        // uniform(モデル)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matM")
        MyGLES32Func.checkGlError("u_matM:glGetUniformLocation")

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(視点座標)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_vecEye")
        MyGLES32Func.checkGlError("u_vecEye:glGetUniformLocation")

        // uniform(法線マップテクスチャ)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle, "u_normalMap")

        // uniform(キューブテクスチャユニット)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle, "u_CubeTexture")
        MyGLES32Func.checkGlError("u_CubeTexture:glGetUniformLocation")

        // uniform(反射するかどうか)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle, "u_Reflection")
        MyGLES32Func.checkGlError("u_Reflection:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matMVP: FloatArray,
             u_vecEye: FloatArray,
             u_normalMap: Int,
             u_CubeTexture: Int,
             u_Reflection: Int) {
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

        // uniform(視点座標)
        GLES32.glUniform3fv(hUNI[2],1,u_vecEye,0)
        MyGLES32Func.checkGlError("u_vecEye",this,model)

        if ( u_normalMap != -1 ) {
            // uniform(法線マップテクスチャ)
            GLES32.glUniform1i(hUNI[3], u_normalMap)
            MyGLES32Func.checkGlError("u_normalMap",this,model)
        }

        if ( u_CubeTexture != -1 ) {
            // uniform(キューブテクスチャ)
            GLES32.glUniform1i(hUNI[4], u_CubeTexture)
            MyGLES32Func.checkGlError("u_CubeTexture",this,model)
        }

        // uniform(反射するかどうか)
        GLES32.glUniform1i(hUNI[5],u_Reflection)
        MyGLES32Func.checkGlError("u_Reflection",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
