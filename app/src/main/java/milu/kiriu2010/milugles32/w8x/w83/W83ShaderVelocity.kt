package milu.kiriu2010.milugles32.w8x.w83

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// ------------------------------------
// シェーダ(GPGPU:速度)
// ------------------------------------
// https://wgld.org/d/webgl/w083.html
// ------------------------------------
class W83ShaderVelocity(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3 a_Position;

            void main() {
                gl_Position  = vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform  vec2       u_resolution;
            uniform  sampler2D  u_texture;
            uniform  vec2       u_mouse;
            uniform  int        u_mouseFlag;
            uniform  float      u_velocity;
            
            const float SPEED = 0.05;
            
            out vec4 o_FragColor;

            void main() {
                vec2 p = gl_FragCoord.xy/u_resolution;
                vec4 t = texture(u_texture,p);
                vec2 v = normalize(u_mouse-t.xy)*0.2;
                vec2 w = normalize(v+t.zw);
                vec4 destColor = vec4(t.xy + w*SPEED*u_velocity, w);
                if ( bool(u_mouseFlag) == false ) {
                    destColor.zw = t.zw;
                }
            
                o_FragColor = destColor;
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

        // uniform(解像度)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_resolution")
        MyGLES32Func.checkGlError("u_resolution:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_texture")
        MyGLES32Func.checkGlError("u_texture:glGetUniformLocation")

        // uniform(マウス位置)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_mouse")
        MyGLES32Func.checkGlError("u_mouse:glGetUniformLocation")

        // uniform(マウス押下しているかどうか)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_mouseFlg")
        MyGLES32Func.checkGlError("u_mouseFlg:glGetUniformLocation")

        // uniform(速度)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_velocity")
        MyGLES32Func.checkGlError("u_velocity:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_resolution: FloatArray,
             u_texture: Int,
             u_mouse: FloatArray,
             u_mouseFlg: Int,
             u_velocity: Float) {
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

        // uniform(マウス位置)
        GLES32.glUniform2fv(hUNI[2],1,u_mouse,0)
        MyGLES32Func.checkGlError("u_mouse",this,model)

        // uniform(マウス押下しているかどうか)
        GLES32.glUniform1i(hUNI[3], u_mouseFlg)
        MyGLES32Func.checkGlError("u_mouseFlg",this,model)

        // uniform(速度)
        GLES32.glUniform1f(hUNI[4], u_velocity)
        MyGLES32Func.checkGlError("u_velocity",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
