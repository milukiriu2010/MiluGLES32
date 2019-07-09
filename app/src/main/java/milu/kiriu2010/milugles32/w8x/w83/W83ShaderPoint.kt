package milu.kiriu2010.milugles32.w8x.w83

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------
// シェーダ(GPGPU:点描画)
// ------------------------------------
// https://wgld.org/d/webgl/w083.html
// ------------------------------------
class W83ShaderPoint(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in float a_Index;
            
            uniform  vec2       u_resolution;
            uniform  sampler2D  u_texture;
            uniform  float      u_pointScale;

            void main() {
                vec2 p = vec2(
                    mod(a_Index  ,u_resolution.x)/u_resolution.x,
                    floor(a_Index/u_resolution.x)/u_resolution.y
                );
                vec4 t = texture(u_texture,p);
                gl_Position  = vec4(t.xy, 0.0, 1.0);
                gl_PointSize = 0.1 + u_pointScale;
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform vec4 u_vecAmbient;
            
            out vec4 o_FragColor;

            void main() {
                o_FragColor = u_vecAmbient;
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

        // uniform(解像度)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_resolution")
        MyGLES32Func.checkGlError("u_resolution:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_texture")
        MyGLES32Func.checkGlError("u_texture:glGetUniformLocation")

        // uniform(描画点の大きさ)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_pointScale")
        MyGLES32Func.checkGlError("u_pointScale:glGetUniformLocation")

        // uniform(環境光)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_vecAmbient")
        MyGLES32Func.checkGlError("u_vecAmbient:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_resolution: FloatArray,
             u_texture: Int,
             u_pointScale: Float,
             u_vecAmbient: FloatArray) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(解像度)
        GLES32.glUniform2fv(hUNI[0], 1, u_resolution,0)
        MyGLES32Func.checkGlError("u_resolution",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[1], u_texture)
        MyGLES32Func.checkGlError("u_texture",this,model)

        // uniform(描画点の大きさ)
        GLES32.glUniform1f(hUNI[2],u_pointScale)
        MyGLES32Func.checkGlError("u_pointSize",this,model)

        // uniform(環境光)
        GLES32.glUniform4fv(hUNI[3], 1, u_vecAmbient,0)
        MyGLES32Func.checkGlError("u_vecAmbient",this,model)

        // モデルを描画
        GLES32.glDrawArrays(GLES32.GL_POINTS, 0, model.datPos.size)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
