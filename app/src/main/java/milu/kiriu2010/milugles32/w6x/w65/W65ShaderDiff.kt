package milu.kiriu2010.milugles32.w6x.w65

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ----------------------------------------
// シェーダ(深度値の差分レンダリング)
// ----------------------------------------
// https://wgld.org/d/webgl/w065.html
// ----------------------------------------
class W65ShaderDiff(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            
            uniform   mat4  u_matM;
            uniform   mat4  u_matMVP;
            uniform   mat4  u_matO;
            uniform   vec3  u_vecEye;
            
            out  float v_Depth;
            out  vec4  v_TextureCoord;

            const float near = 0.1;
            const float far  = 15.0;
            const float linearDepth = 1.0/(far-near);

            void main() {
                vec3 pos = (u_matM*vec4(a_Position,1.0)).xyz;
                v_Depth  = length(u_vecEye-pos) * linearDepth;
                v_TextureCoord = u_matO * vec4(pos,1.0);
                gl_Position = u_matMVP * vec4(a_Position,1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform   sampler2D u_TextureBackface;
            
            in  float v_Depth;
            in  vec4  v_TextureCoord;
            
            out vec4  o_FragColor;

            void main() {
                float bDepth     = 1.0 - textureProj(u_TextureBackface, v_TextureCoord).r;
                float difference = 1.0 - clamp(bDepth-v_Depth,0.0, 1.0);
                o_FragColor      = vec4(vec3(difference), 1.0);
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
        hUNI = IntArray(5)

        // uniform(モデル座標変換行列)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matM")
        MyGLES32Func.checkGlError("u_matM:glGetUniformLocation")

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(正射影座標行列)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_matO")
        MyGLES32Func.checkGlError("u_matO:glGetUniformLocation")

        // uniform(視点座標)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_vecEye")
        MyGLES32Func.checkGlError("u_vecEye:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle, "u_TextureBackface")
        MyGLES32Func.checkGlError("u_TextureBackface:glGetUniformLocation")

        return this
    }


    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matMVP: FloatArray,
             u_matO: FloatArray,
             u_vecEye: FloatArray,
             u_TextureBackface: Int) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram", this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル座標変換行列)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matM,0)
        MyGLES32Func.checkGlError("u_matM",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(正射影座標行列)
        GLES32.glUniformMatrix4fv(hUNI[2],1,false,u_matO,0)
        MyGLES32Func.checkGlError("u_matO",this,model)

        // uniform(視点座標)
        GLES32.glUniform3fv(hUNI[3],1,u_vecEye,0)
        MyGLES32Func.checkGlError("u_vecEye",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[4], u_TextureBackface)
        MyGLES32Func.checkGlError("u_TextureBackface",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
