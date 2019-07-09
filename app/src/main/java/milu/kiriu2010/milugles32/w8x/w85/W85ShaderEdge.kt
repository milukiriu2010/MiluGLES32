package milu.kiriu2010.milugles32.w8x.w85

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------
// エッジ
// MRT(Multiple Render Targets)
// ------------------------------------
// https://wgld.org/d/webgl/w085.html
// ------------------------------------
class W85ShaderEdge(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es

            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec2  a_TexCoord;

            out vec2  v_TexCoord;

            void main() {
                v_TexCoord  = a_TexCoord;
                gl_Position = vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es

            precision highp   float;
            
            uniform  vec2  u_resolution;
            uniform  vec2  u_offsetCoord[9];
            uniform  float u_weight[9];
            uniform  sampler2D  u_textureColor;
            uniform  sampler2D  u_textureDepth;
            uniform  sampler2D  u_textureNormal;

            in  vec2  v_TexCoord;

            out vec4  o_FragColor;

            void main() {
                vec2 offsetScale = 1.0/u_resolution;
                vec4 destColor = texture(u_textureColor, v_TexCoord);
                vec3 normalColor = vec3(0.0);
                vec3 tmpColor    = vec3(1.0);
                float depthEdge  = 0.0;
                float normalEdge = 0.0;
                for (int i = 0; i < 9; i++) {
                    vec2 offset = v_TexCoord + u_offsetCoord[i] * offsetScale;
                    depthEdge   += texture(u_textureDepth ,offset).r   * u_weight[i];
                    normalColor += texture(u_textureNormal,offset).rgb * u_weight[i];
                }
                normalEdge = dot(abs(normalColor), tmpColor)/3.0;
                if (abs(depthEdge) > 0.02) {
                    depthEdge = 1.0;
                }
                else {
                    depthEdge = 0.0;
                }
                float edge = (1.0-depthEdge)*(1.0-normalEdge);
                o_FragColor = vec4(destColor.rgb*edge, destColor.a);
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

        // uniform(解像度)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_resolution")
        MyGLES32Func.checkGlError("u_resolution:glGetUniformLocation")

        // uniform()
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_offsetCoord")
        MyGLES32Func.checkGlError("u_offsetCoord:glGetUniformLocation")

        // uniform()
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_weight")
        MyGLES32Func.checkGlError("u_weight:glGetUniformLocation")

        // uniform()
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_textureColor")
        MyGLES32Func.checkGlError("u_textureColor:glGetUniformLocation")

        // uniform()
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_textureDepth")
        MyGLES32Func.checkGlError("u_textureDepth:glGetUniformLocation")

        // uniform()
        hUNI[5] = GLES32.glGetUniformLocation(programHandle,"u_textureNormal")
        MyGLES32Func.checkGlError("u_textureNormal:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_resolution: FloatArray,
             u_offsetCoord: FloatArray,
             u_weight: FloatArray,
             u_textureColor: Int,
             u_textureDepth: Int,
             u_textureNormal: Int) {
        //Log.d(javaClass.simpleName,"draw:${model.javaClass.simpleName}")
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)
        //Log.d(javaClass.simpleName,"draw:glUseProgram")

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)
        //Log.d(javaClass.simpleName,"draw:glBindVertexArray")

        // uniform(解像度)
        GLES32.glUniform2fv(hUNI[0],1,u_resolution,0)
        MyGLES32Func.checkGlError("u_resolution",this,model)
        //Log.d(javaClass.simpleName,"draw:u_resolution")

        // uniform()
        GLES32.glUniform2fv(hUNI[1],9,u_offsetCoord,0)
        MyGLES32Func.checkGlError("u_offsetCoord",this,model)
        //Log.d(javaClass.simpleName,"draw:u_offsetCoord")

        // uniform()
        GLES32.glUniform1fv(hUNI[2],9,u_weight,0)
        MyGLES32Func.checkGlError("u_weight",this,model)
        //Log.d(javaClass.simpleName,"draw:u_weight")

        // uniform()
        GLES32.glUniform1i(hUNI[3],u_textureColor)
        MyGLES32Func.checkGlError("u_textureColor",this,model)
        //Log.d(javaClass.simpleName,"draw:u_textureColor")

        // uniform()
        GLES32.glUniform1i(hUNI[4],u_textureDepth)
        MyGLES32Func.checkGlError("u_textureDepth",this,model)
        //Log.d(javaClass.simpleName,"draw:u_textureDepth")

        // uniform()
        GLES32.glUniform1i(hUNI[5],u_textureNormal)
        MyGLES32Func.checkGlError("u_textureNormal",this,model)
        //Log.d(javaClass.simpleName,"draw:u_textureNormal")

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)
        MyGLES32Func.checkGlError("glDrawElements",this,model)
        //Log.d(javaClass.simpleName,"draw:glDrawElements")

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
