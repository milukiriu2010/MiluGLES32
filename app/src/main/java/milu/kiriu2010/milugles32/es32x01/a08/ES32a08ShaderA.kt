package milu.kiriu2010.milugles32.es32x01.a08

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------
// gl_VertexIDとgl_InstanceID
// シェーダA
// ------------------------------------
// https://wgld.org/d/webgl2/w008.html
// ------------------------------------
class ES32a08ShaderA(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es

            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec2  a_TexCoord;
            layout (location = 3) in vec3  a_Offset;

            uniform  mat4  u_matM;
            uniform  mat4  u_matMVP;
            uniform  mat4  u_matN;

            out vec3  v_Position;
            out vec3  v_Normal;
            out vec2  v_TexCoord;

            void main() {
                vec3 pos    = a_Position + a_Offset;
                // ------------------------------------------------------
                // gl_VertexID(入力専用:頂点シェーダのみで利用可能)
                // 頂点のインデックスをシェーダ内で利用することができる変数
                // ------------------------------------------------------
                // ４つに１つの割合で、頂点が少しだけ外側に飛び出る
                // ------------------------------------------------------
                if (mod(float(gl_VertexID),4.0) == 0.0) {
                    pos += a_Normal * 0.05;
                }
                v_Position  = (u_matM  * vec4(pos     ,1.0)).xyz;
                v_Normal    = (u_matN  * vec4(a_Normal,0.0)).xyz;
                v_TexCoord  = a_TexCoord;
                // ------------------------------------------------------
                // gl_InstanceID(入力専用:頂点シェーダのみで利用可能)
                // インスタンスのインデックスが取得できる
                // インスタンス描画を行っている場合にのみ意味を持つ
                // ------------------------------------------------------
                // 描画ごとにテクスチャの上下を反転させている
                // ------------------------------------------------------
                if (mod(float(gl_InstanceID),2.0) == 0.0) {
                    v_TexCoord  = 1.0 - v_TexCoord;
                }
                gl_Position = u_matMVP * vec4(pos, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es

            precision highp   float;

            uniform  vec3      u_vecLight;
            uniform  vec3      u_vecEye;
            uniform  sampler2D u_Texture;

            in  vec3  v_Position;
            in  vec3  v_Normal;
            in  vec2  v_TexCoord;

            out vec4  o_FragColor;

            void main() {
                vec3  light    = normalize(u_vecLight - v_Position);
                vec3  eye      = normalize(v_Position - u_vecEye);
                vec3  ref      = normalize(reflect(eye,v_Normal));
                float diffuse  = max(dot(light,v_Normal),0.2);
                float specular = max(dot(light,ref)     ,0.0);
                specular = pow(specular,20.0);
                vec4 samplerColor = texture(u_Texture,v_TexCoord);
                o_FragColor = vec4(samplerColor.rgb*diffuse + specular, samplerColor.a);
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
        hUNI = IntArray(6)

        // uniform(モデル)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matM")
        MyGLES32Func.checkGlError("u_matM:glGetUniformLocation")

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform()
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_matN")
        MyGLES32Func.checkGlError("u_matN:glGetUniformLocation")

        // uniform(光源位置)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_vecLight")
        MyGLES32Func.checkGlError("u_vecLight:glGetUniformLocation")

        // uniform(視点座標)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_vecEye")
        MyGLES32Func.checkGlError("u_vecEye:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle, "u_Texture")
        MyGLES32Func.checkGlError("u_Texture:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matMVP: FloatArray,
             u_matN: FloatArray,
             u_vecLight: FloatArray,
             u_vecEye: FloatArray,
             u_Texture: Int) {
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

        // uniform(光源位置)
        GLES32.glUniform3fv(hUNI[3],1,u_vecLight,0)
        MyGLES32Func.checkGlError("u_vecLight",this,model)
        //Log.d(javaClass.simpleName,"draw:u_vecLight")

        // uniform(視点座標)
        GLES32.glUniform3fv(hUNI[4],1,u_vecEye,0)
        MyGLES32Func.checkGlError("u_vecEye",this,model)
        //Log.d(javaClass.simpleName,"draw:u_vecEye")

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[5], u_Texture)
        MyGLES32Func.checkGlError("u_Texture",this,model)
        //Log.d(javaClass.simpleName,"draw:u_Texture")

        // モデルを描画
        //GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, model.bufIdx)
        //GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)
        //MyGLES32Func.checkGlError2("glDrawElements",this,model)
        //Log.d(javaClass.simpleName,"draw:glDrawElements")
        GLES32.glDrawElementsInstanced(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0,model.datOff.size/3)
        MyGLES32Func.checkGlError("glDrawElementsInstanced",this,model)

        // リソース解放
        //GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,0)
        //GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER,0)

        // VAO解放
        GLES32.glBindVertexArray(0)
        //MyGLES32Func.checkGlError2("draw:glBindVertexArray0",this,model)
    }
}
