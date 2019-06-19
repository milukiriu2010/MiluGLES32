package milu.kiriu2010.milugles32.es32x02.a12

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------
// シェーダA
// derivative 関数(dFdx, dFdy)
// ------------------------------------
// https://wgld.org/d/webgl2/w012.html
// ------------------------------------
class ES32a12ShaderA(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es

            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;

            uniform  mat4  u_matM;
            uniform  mat4  u_matMVP;
            // モデル座標変換行列⇒逆行列⇒転置行列
            uniform  mat4  u_matN;

            out vec3  v_Position;
            flat out vec3  v_Normal;

            void main() {
                v_Position  = (u_matM * vec4(a_Position,1.0)).xyz;
                v_Normal    = (u_matN * vec4(a_Normal  ,0.0)).xyz;
                gl_Position = u_matMVP   * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es

            precision highp   float;

            in  vec3  v_Position;
            flat in  vec3  v_Normal;

            layout (location = 0) out vec4  o_FragColor0;
            layout (location = 1) out vec4  o_FragColor1;

            void main() {
                vec3  nx = dFdx(v_Position);
                vec3  ny = dFdy(v_Position);
                vec3  n  = normalize(cross(normalize(nx),normalize(ny)));
                o_FragColor0 = vec4(n       , 1.0);
                o_FragColor1 = vec4(v_Normal, 1.0);
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
        hUNI = IntArray(3)

        // uniform(モデル)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matM")
        MyGLES32Func.checkGlError("u_matM:glGetUniformLocation")

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform()
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_matN")
        MyGLES32Func.checkGlError("u_matN:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matMVP: FloatArray,
             u_matN: FloatArray) {
        //Log.d(javaClass.simpleName,"draw:${model.javaClass.simpleName}")
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)
        //Log.d(javaClass.simpleName,"draw:glUseProgram")

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)
        //Log.d(javaClass.simpleName,"draw:glBindVertexArray")

        // uniform(モデル)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matM,0)
        MyGLES32Func.checkGlError("u_matM",this,model)
        //Log.d(javaClass.simpleName,"draw:u_matM")

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)
        //Log.d(javaClass.simpleName,"draw:u_matMVP")

        // uniform()
        GLES32.glUniformMatrix4fv(hUNI[2],1,false,u_matN,0)
        MyGLES32Func.checkGlError("u_matN",this,model)
        //Log.d(javaClass.simpleName,"draw:u_matN")

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)
        MyGLES32Func.checkGlError("glDrawElements",this,model)
        //Log.d(javaClass.simpleName,"draw:glDrawElements")

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
