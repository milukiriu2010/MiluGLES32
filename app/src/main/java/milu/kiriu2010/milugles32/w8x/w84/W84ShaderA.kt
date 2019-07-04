package milu.kiriu2010.milugles32.w8x.w84

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------
// シェーダA
// MRT(Multiple Render Targets)
// ------------------------------------
// https://wgld.org/d/webgl2/w011.html
// ------------------------------------
class W84ShaderA(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es

            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec4  a_Color;

            uniform  mat4  u_matMVP;
            uniform  mat4  u_matINV;
            uniform  vec3  u_vecLight;
            uniform  vec4  u_vecAmbient;

            out vec4  v_Dest;
            out vec4  v_Color;
            out vec3  v_Normal;
            out float v_Depth;

            void main() {
                gl_Position    = u_matMVP   * vec4(a_Position, 1.0);
                vec3  invLight = normalize(u_matINV*vec4(u_vecLight,0.0)).xyz;
                float diffuse  = clamp(dot(a_Normal,invLight),0.1,1.0);
                v_Dest   = vec4(a_Color.rgb*u_vecAmbient.rgb*diffuse,1.0);
                v_Color  = a_Color * u_vecAmbient;
                v_Normal = a_Normal;
                v_Depth  = gl_Position.z/gl_Position.w;
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es

            precision highp   float;

            in  vec4  v_Dest;
            in  vec4  v_Color;
            in  vec3  v_Normal;
            in  float v_Depth;

            layout (location = 0) out vec4  o_FragColor0;
            layout (location = 1) out vec4  o_FragColor1;
            layout (location = 2) out vec4  o_FragColor2;
            layout (location = 3) out vec4  o_FragColor3;

            void main() {
                o_FragColor0 = v_Dest;
                o_FragColor1 = v_Color;
                o_FragColor2 = vec4((v_Normal+1.0)/2.0,1.0);
                o_FragColor3 = vec4(vec3((v_Depth+1.0)/2.0),1.0);
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
        hUNI = IntArray(4)

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(逆行列)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matINV")
        MyGLES32Func.checkGlError("u_matINV:glGetUniformLocation")

        // uniform(光源位置)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_vecLight")
        MyGLES32Func.checkGlError("u_vecLight:glGetUniformLocation")

        // uniform(環境光)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_vecAmbient")
        MyGLES32Func.checkGlError("u_vecAmbient:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_matI: FloatArray,
             u_vecLight: FloatArray,
             u_vecAmbient: FloatArray) {
        //Log.d(javaClass.simpleName,"draw:${model.javaClass.simpleName}")
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)
        //Log.d(javaClass.simpleName,"draw:glUseProgram")

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)
        //Log.d(javaClass.simpleName,"draw:glBindVertexArray")

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)
        //Log.d(javaClass.simpleName,"draw:u_matMVP")

        // uniform(逆行列)
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matI,0)
        MyGLES32Func.checkGlError("u_matI",this,model)
        //Log.d(javaClass.simpleName,"draw:u_matI")

        // uniform(光源位置)
        GLES32.glUniform3fv(hUNI[2],1,u_vecLight,0)
        MyGLES32Func.checkGlError("u_vecLight",this,model)
        //Log.d(javaClass.simpleName,"draw:u_vecLight")

        // uniform(環境光)
        GLES32.glUniform4fv(hUNI[3],1,u_vecAmbient,0)
        MyGLES32Func.checkGlError("u_vecAmbient",this,model)
        //Log.d(javaClass.simpleName,"draw:u_vecAmbient")

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)
        MyGLES32Func.checkGlError("glDrawElements",this,model)
        //Log.d(javaClass.simpleName,"draw:glDrawElements")

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
