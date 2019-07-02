package milu.kiriu2010.milugles32.w6x.w65

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ----------------------------------------
// シェーダ(メイン)
// ----------------------------------------
// https://wgld.org/d/webgl/w065.html
// ----------------------------------------
class W65ShaderMain(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec4  a_Color;
            
            uniform   mat4  u_matM;
            uniform   mat4  u_matMVP;
            uniform   mat4  u_matINV;
            uniform   mat4  u_matO;
            uniform   vec3  u_vecLight;
            uniform   vec3  u_vecCenter;
            uniform   vec3  u_vecEye;
            uniform   vec4  u_ambientColor;
            
            out  vec4  v_Color;
            out  vec4  v_TextureCoord;
            out  float v_DotLE;

            void main() {
                vec3   pos      = (u_matM * vec4(a_Position,1.0)).xyz;
                vec3   invLight = normalize(u_matINV * vec4(u_vecLight-pos, 0.0)).xyz;
                vec3   invEye   = normalize(u_matINV * vec4(u_vecEye      , 0.0)).xyz;
                vec3   halfLE   = normalize(invLight + invEye);
                float  diffuse  = clamp(dot(a_Normal,invLight), 0.0, 1.0);
                float  specular = pow(clamp(dot(a_Normal, halfLE), 0.0, 1.0), 50.0);
                v_Color = a_Color*vec4(vec3(diffuse),1.0) + vec4(vec3(specular),0.0) + u_ambientColor;
                v_TextureCoord  = u_matO * vec4(pos,1.0);
                v_DotLE         = pow(max(dot(normalize(u_vecCenter-u_vecEye),normalize(u_vecLight)),0.0), 10.0);
                gl_Position     = u_matMVP   * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp   float;

            uniform   sampler2D u_TextureBlur;
            
            in  vec4  v_Color;
            in  vec4  v_TextureCoord;
            in  float v_DotLE;
            
            out vec4  o_FragColor;

            const vec3 throughColor = vec3(1.0,0.5,0.2);

            void main() {
                float bDepth  = pow(textureProj(u_TextureBlur, v_TextureCoord).r, 20.0);
                vec3  through = throughColor * v_DotLE * bDepth;
                o_FragColor  = vec4(v_Color.rgb + through, v_Color.a);
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
        hUNI = IntArray(9)

        // uniform(モデル座標変換行列)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matM")
        MyGLES32Func.checkGlError("u_matM:glGetUniformLocation")

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(逆行列)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_matINV")
        MyGLES32Func.checkGlError("u_matINV:glGetUniformLocation")

        // uniform(正射影座標行列)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_matO")
        MyGLES32Func.checkGlError("u_matO:glGetUniformLocation")

        // uniform(光源位置)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_vecLight")
        MyGLES32Func.checkGlError("u_vecLight:glGetUniformLocation")

        // uniform(注視点)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle,"u_vecCenter")
        MyGLES32Func.checkGlError("u_vecCenter:glGetUniformLocation")

        // uniform(視点座標)
        hUNI[6] = GLES32.glGetUniformLocation(programHandle,"u_vecEye")
        MyGLES32Func.checkGlError("u_vecEye:glGetUniformLocation")

        // uniform(環境色)
        hUNI[7] = GLES32.glGetUniformLocation(programHandle, "u_ambientColor")
        MyGLES32Func.checkGlError("u_ambientColor:glGetUniformLocation")

        // uniform(テクスチャ)
        hUNI[8] = GLES32.glGetUniformLocation(programHandle, "u_TextureBlur")
        MyGLES32Func.checkGlError("u_TextureBlur:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matMVP: FloatArray,
             u_matI: FloatArray,
             u_matO: FloatArray,
             u_vecLight: FloatArray,
             u_vecCenter: FloatArray,
             u_vecEye: FloatArray,
             u_ambientColor: FloatArray,
             u_TextureBlur: Int) {
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

        // uniform(逆行列)
        GLES32.glUniformMatrix4fv(hUNI[2],1,false,u_matI,0)
        MyGLES32Func.checkGlError("u_matINV",this,model)

        // uniform(正射影座標行列)
        GLES32.glUniformMatrix4fv(hUNI[3],1,false,u_matO,0)
        MyGLES32Func.checkGlError("u_matO", this,model)

        // uniform(光源位置)
        GLES32.glUniform3fv(hUNI[4],1,u_vecLight,0)
        MyGLES32Func.checkGlError("u_vecLight", this,model)

        // uniform(注視点)
        GLES32.glUniform3fv(hUNI[5],1,u_vecCenter,0)
        MyGLES32Func.checkGlError("u_vecCenter", this,model)

        // uniform(視点座標)
        GLES32.glUniform3fv(hUNI[6],1,u_vecEye,0)
        MyGLES32Func.checkGlError("u_vecEye", this,model)

        // uniform(環境色)
        GLES32.glUniform4fv(hUNI[7], 1,u_ambientColor,0)
        MyGLES32Func.checkGlError("u_ambientColor", this,model)

        // uniform(テクスチャ)
        GLES32.glUniform1i(hUNI[8], u_TextureBlur)
        MyGLES32Func.checkGlError("u_TextureBlur", this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}