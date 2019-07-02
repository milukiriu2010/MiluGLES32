package milu.kiriu2010.milugles32.w6x.w62

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------
// シェーダ(メイン)
// ------------------------------------
//   鏡面世界側もレンダリングする
// ------------------------------------
// https://wgld.org/d/webgl/w062.html
// ------------------------------------
class W62ShaderMain(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            // 鏡面世界と通常のワールド空間を同じシェーダでレンダリングできるようにするため、
            // モデル座標変換行列とビュー×プロジェクション座標変換行列を渡している
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec4  a_Color;
            
            uniform   mat4  u_matM;
            uniform   mat4  u_matVP;
            uniform   mat4  u_matINV;
            uniform   vec3  u_vecLight;
            uniform   vec3  u_vecEye;
            uniform   vec4  u_ambientColor;
            uniform   int   u_mirror;
            
            out  vec4  v_Color;

            void main() {
                vec3   invLight = normalize(u_matINV * vec4(u_vecLight, 0.0)).xyz;
                vec3   invEye   = normalize(u_matINV * vec4(u_vecEye  , 0.0)).xyz;
                vec3   halfLE   = normalize(invLight + invEye);
                float  diffuse  = clamp(dot(a_Normal,invLight), 0.1, 1.0);
                float  specular = pow(clamp(dot(a_Normal, halfLE), 0.0, 1.0), 50.0);
                v_Color         = a_Color * vec4(vec3(diffuse),1.0) + vec4(vec3(specular),1.0) + u_ambientColor;
                vec4   pos      = u_matM * vec4(a_Position, 1.0);

                // 鏡面の場合、Y成分だけを反転する
                if (bool(u_mirror)) {
                    pos = vec4(pos.x, -pos.y, pos.zw);
                }
                gl_Position     = u_matVP   * pos;
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
        hUNI = IntArray(7)

        // uniform(モデル座標変換行列)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matM")
        MyGLES32Func.checkGlError("u_matM:glGetUniformLocation")

        // uniform(ビュー×プロジェクション)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matVP")
        MyGLES32Func.checkGlError("u_matVP:glGetUniformLocation")

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

        // uniform(ミラーするかどうか)
        hUNI[6] = GLES32.glGetUniformLocation(programHandle, "u_mirror")
        MyGLES32Func.checkGlError("u_mirror:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matVP: FloatArray,
             u_matI: FloatArray,
             u_vecLight: FloatArray,
             u_vecEye: FloatArray,
             u_ambientColor: FloatArray,
             u_mirror: Int) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル座標変換行列)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matM,0)
        MyGLES32Func.checkGlError("u_matM",this,model)

        // uniform(ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matVP,0)
        MyGLES32Func.checkGlError("u_matVP",this,model)

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

        // uniform(ミラーするかどうか)
        GLES32.glUniform1i(hUNI[6], u_mirror)
        MyGLES32Func.checkGlError("u_mirror",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
