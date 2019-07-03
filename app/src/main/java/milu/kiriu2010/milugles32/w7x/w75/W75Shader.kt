package milu.kiriu2010.milugles32.w7x.w75

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------
// インスタンシング
// ------------------------------------
// https://wgld.org/d/webgl/w075.html
// ------------------------------------
class W75Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es

            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec3  a_InstancePosition;
            layout (location = 3) in vec4  a_InstanceColor;

            uniform  mat4  u_matMVP;
            uniform  mat4  u_matINV;
            uniform  vec3  u_vecLight;
            uniform  vec3  u_vecEye;

            out vec4  v_Color;

            void main() {
                vec3  invLight = normalize(u_matINV * vec4(u_vecLight,0.0)).xyz;
                vec3  invEye   = normalize(u_matINV * vec4(u_vecEye  ,0.0)).xyz;
                vec3  halfLE   = normalize(invLight + invEye);
                float diffuse  = clamp(dot(a_Normal, invLight),0.1,1.0);
                float specular = pow(clamp(dot(a_Normal, halfLE),0.1,1.0),30.0);
                v_Color = a_InstanceColor * vec4(vec3(diffuse),1.0) + vec4(vec3(specular),1.0); 
                
                gl_Position = u_matMVP * vec4(a_Position+a_InstancePosition, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es

            precision highp   float;

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

        // uniform(視点座標)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_vecEye")
        MyGLES32Func.checkGlError("u_vecEye:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_matINV: FloatArray,
             u_vecLight: FloatArray,
             u_vecEye: FloatArray) {
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
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matINV,0)
        MyGLES32Func.checkGlError("u_matINV",this,model)
        //Log.d(javaClass.simpleName,"draw:u_matN")

        // uniform(光源位置)
        GLES32.glUniform3fv(hUNI[2],1,u_vecLight,0)
        MyGLES32Func.checkGlError("u_vecLight",this,model)
        //Log.d(javaClass.simpleName,"draw:u_vecLight")

        // uniform(視点座標)
        GLES32.glUniform3fv(hUNI[3],1,u_vecEye,0)
        MyGLES32Func.checkGlError("u_vecEye",this,model)
        //Log.d(javaClass.simpleName,"draw:u_vecEye")

        // モデルを描画
        //GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, model.bufIdx)
        //GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)
        //MyGLES32Func.checkGlError2("glDrawElements",this,model)
        //Log.d(javaClass.simpleName,"draw:glDrawElements")
        GLES32.glDrawElementsInstanced(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0,100)
        MyGLES32Func.checkGlError("glDrawElementsInstanced",this,model)

        // リソース解放
        //GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,0)
        //GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER,0)

        // VAO解放
        GLES32.glBindVertexArray(0)
        //MyGLES32Func.checkGlError2("draw:glBindVertexArray0",this,model)
    }
}
