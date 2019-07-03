package milu.kiriu2010.milugles32.w7x.w77

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// --------------------------------------
// シェーダ(ラインシェード)
// --------------------------------------
// https://wgld.org/d/webgl/w077.html
// --------------------------------------
class W77Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec4  a_Color;
            
            uniform   mat4  u_matMVP;
            uniform   mat4  u_matINV;
            uniform   vec3  u_vecLight;
            
            out  float v_Diffuse;
            out  vec4  v_Color;

            void main() {
                vec3   invLight  = normalize(u_matINV * vec4(u_vecLight, 0.0)).xyz;
                // 頂点シェーダで計算された拡散光の影響力を
                // フラグメントシェーダに渡している
                v_Diffuse   = clamp(dot(a_Normal,invLight), 0.0, 1.0);
                v_Color     = a_Color;
                gl_Position = u_matMVP   * vec4(a_Position, 1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp     float;

            uniform   float  u_lineScale;
            
            in  float v_Diffuse;
            in  vec4  v_Color;
            
            out vec4  o_FragColor;

            void main() {
                vec2 v = gl_FragCoord.xy * u_lineScale;

                // cross line
                //	float f = max(sin(v.x + v.y), 0.0);
                //	float g = max(sin(v.x - v.y), 0.0);
                //
                //	float s;
                //	if(v_Diffuse > 0.6){
                //		s = 0.8;
                //	}else if(v_Diffuse > 0.1){
                //		s = 0.6 - pow(f, 5.0);
                //	}else{
                //		s = 0.4 - (pow(f, 5.0) + pow(g, 5.0));
                //	}
                //	gl_FragColor = vec4(vColor.rgb * s, 1.0);

                float f = sin(v.x + v.y);
                float s;
                if(v_Diffuse > 0.7){
                    s = 0.9;
                }else if(v_Diffuse > 0.3){
                    s = 0.6;
                }else{
                    s = 0.3;
                }
                o_FragColor = vec4(v_Color.rgb * (v_Diffuse+f+s), 1.0);
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

        // uniform()
        hUNI[3] = GLES32.glGetUniformLocation(programHandle, "u_lineScale")
        MyGLES32Func.checkGlError("u_lineScale:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matMVP: FloatArray,
             u_matI: FloatArray,
             u_vecLight: FloatArray,
             u_lineScale: Float) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(逆行列)
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matI,0)
        MyGLES32Func.checkGlError("u_matINV",this,model)

        // uniform(光源位置)
        GLES32.glUniform3fv(hUNI[2],1,u_vecLight,0)
        MyGLES32Func.checkGlError("u_vecLight",this,model)

        // uniform()
        GLES32.glUniform1f(hUNI[3], u_lineScale)
        MyGLES32Func.checkGlError("u_lineScale",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
