package milu.kiriu2010.milugles32.w6x.w62

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -----------------------------------------------------
// シェーダ(ステンシル鏡面反射)
// -----------------------------------------------------
//   正射影で画面全体にかぶさるようにレンダリングする
//   鏡面世界を床面に合成する
// -----------------------------------------------------
// https://wgld.org/d/webgl/w062.html
// -----------------------------------------------------
class W62ShaderMirror(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec2  a_TextureCoord;
            
            // 正射影座標変換行列
            uniform   mat4  u_matO;
            
            out  vec2  v_TextureCoord;

            void main() {
                v_TextureCoord = a_TextureCoord;
                gl_Position    = u_matO * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform   sampler2D u_Texture;
            // 映り込み係数
            // どの程度の透明度で鏡面世界を合成するのかを決める係数
            uniform   float     u_alpha;
            
            in  vec2  v_TextureCoord;
            
            out vec4  o_FragColor;

            void main() {
                vec2 tc = vec2(v_TextureCoord.s, 1.0-v_TextureCoord.t);
                o_FragColor = vec4(texture(u_Texture,tc).rgb, u_alpha);
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

        // uniform(正射影座標変換行列)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matO")
        MyGLES32Func.checkGlError("u_matO:glGetUniformLocation")

        // uniform(テクスチャユニット)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle, "u_Texture")
        MyGLES32Func.checkGlError("u_Texture:glGetUniformLocation")

        // uniform(どの程度の透明度で鏡面世界を合成するのかを決める係数)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle, "u_alpha")
        MyGLES32Func.checkGlError("u_alpha:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matO: FloatArray,
             u_Texture: Int,
             u_alpha: Float) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(正射影座標変換行列)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matO,0)
        MyGLES32Func.checkGlError("u_matO",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[1], u_Texture)
        MyGLES32Func.checkGlError("u_Texture",this,model)

        // uniform(どの程度の透明度で鏡面世界を合成するのかを決める係数)
        GLES32.glUniform1f(hUNI[2], u_alpha)
        MyGLES32Func.checkGlError("u_alpha",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}