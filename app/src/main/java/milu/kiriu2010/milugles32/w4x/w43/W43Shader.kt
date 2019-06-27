package milu.kiriu2010.milugles32.w4x.w43

import android.content.Context
import android.opengl.GLES32
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.shader.es32.ES32MgShader
import milu.kiriu2010.gui.vbo.es32.ES32VAOAbs

// -------------------------------------
// シェーダ for 視差マッピング
// -------------------------------------
// https://wgld.org/d/webgl/w043.html
// -------------------------------------
class W43Shader(ctx: Context): ES32MgShader(ctx) {
    // 頂点シェーダ
    private val scv =
            """#version 300 es
            layout (location = 0) in vec3  a_Position;
            layout (location = 1) in vec3  a_Normal;
            layout (location = 2) in vec4  a_Color;
            layout (location = 3) in vec2  a_TextureCoord;
            
            uniform   mat4  u_matM;
            uniform   mat4  u_matMVP;
            uniform   mat4  u_matINV;
            uniform   vec3  u_vecLight;
            uniform   vec3  u_vecEye;
            
            out  vec4  v_Color;
            out  vec2  v_TextureCoord;
            out  vec3  v_vecLight;
            out  vec3  v_vecEye;

            void main() {
                vec3 pos      = (u_matM   * vec4(a_Position,0.0)).xyz;
                vec3 invEye   = (u_matINV * vec4(u_vecEye  ,0.0)).xyz;
                vec3 invLight = (u_matINV * vec4(u_vecLight,0.0)).xyz;
                vec3 eye      = invEye   - pos;
                vec3 light    = invLight - pos;
                // 法線ベクトル
                vec3 n = normalize(a_Normal);
                // 接線ベクトル
                // Y軸と法線ベクトルとの間で外積を取ることで算出する
                vec3 t = normalize(cross(a_Normal,vec3(0.0,1.0,0.0)));
                // 従法線ベクトル
                // 接線ベクトルと法線ベクトルとの間で外積をとることで算出する
                vec3 b = cross(n,t);
                // 視線ベクトルとライトベクトルを接空間上に変換する
                v_vecEye.x = dot(t,eye);
                v_vecEye.y = dot(b,eye);
                v_vecEye.z = dot(n,eye);
                normalize(v_vecEye);
                v_vecLight.x = dot(t,light);
                v_vecLight.y = dot(b,light);
                v_vecLight.z = dot(n,light);
                normalize(v_vecLight);
                v_Color        = a_Color;
                v_TextureCoord = a_TextureCoord;
                gl_Position    = u_matMVP * vec4(a_Position,1.0);
            }
            """.trimIndent()

    // フラグメントシェーダ
    private val scf =
            """#version 300 es
            precision highp   float;

            // 法線マップのユニット番号
            uniform   sampler2D u_Texture0;
            // 高さマップのユニット番号
            uniform   sampler2D u_Texture1;
            uniform   float     u_Height;
            
            in  vec4  v_Color;
            in  vec2  v_TextureCoord;
            in  vec3  v_vecLight;
            in  vec3  v_vecEye;
            
            out vec4  o_FragColor;

            void main() {
                vec3  light     = normalize(v_vecLight);
                vec3  eye       = normalize(v_vecEye);
                // 高さマップのイメージから、高さに関する情報を抜き出している
                // この高さを使って法線マップへの参照点をずらす
                float hScale    = texture(u_Texture1, v_TextureCoord).r * u_Height;
                // 本来のテクスチャ座標から高さと視点を考慮した分の値を減算してずらす
                // こうして得られたテクスチャ座標を使ってバンプマッピング同様の処理を行うことで、
                // 高さと視線を考慮した視差マッピングを実現する
                vec2  hTexCoord = v_TextureCoord - hScale * eye.xy;
                vec3  halfLE    = normalize(light+eye);
                // 法線マップからRGB値を抜き出し、法線として扱う
                // 法線マップ上の色データは負の値がない(0～1)
                // 一方、法線は-1～1の範囲をとるので、"２倍して１引く"という処理になっている
                vec3  mNormal   = (texture(u_Texture0, hTexCoord) * 2.0 - 1.0).rgb;
                float diffuse   = clamp(dot(mNormal, light), 0.1, 1.0);
                float specular  = pow(clamp(dot(mNormal,halfLE) ,0.0,1.0), 100.0);
                vec4  destColor = v_Color * vec4(vec3(diffuse),1.0) + vec4(vec3(specular),1.0);
                o_FragColor  = destColor;
            }
            """.trimIndent()

    override fun loadShader(): ES32MgShader {
        // 頂点シェーダを生成
        val svhandle = MyGLES32Func.loadShader(GLES32.GL_VERTEX_SHADER, scv)
        // フラグメントシェーダを生成
        val sfhandle = MyGLES32Func.loadShader(GLES32.GL_FRAGMENT_SHADER, scf)

        // プログラムオブジェクトの生成とリンク
        programHandle = MyGLES32Func.createProgram(svhandle,sfhandle)

        // ----------------------------------------------
        // uniformハンドルに値をセット
        // ----------------------------------------------
        hUNI = IntArray(8)
        // uniform(モデル)
        hUNI[0] = GLES32.glGetUniformLocation(programHandle,"u_matM")
        MyGLES32Func.checkGlError("u_matM:glGetUniformLocation")

        // uniform(モデル×ビュー×プロジェクション)
        hUNI[1] = GLES32.glGetUniformLocation(programHandle,"u_matMVP")
        MyGLES32Func.checkGlError("u_matMVP:glGetUniformLocation")

        // uniform(逆行列)
        hUNI[2] = GLES32.glGetUniformLocation(programHandle,"u_matINV")
        MyGLES32Func.checkGlError("u_matINV:glGetUniformLocation")

        // uniform(光源位置)
        hUNI[3] = GLES32.glGetUniformLocation(programHandle,"u_vecLight")
        MyGLES32Func.checkGlError("u_vecLight:glGetUniformLocation")

        // uniform(視点座標)
        hUNI[4] = GLES32.glGetUniformLocation(programHandle,"u_vecEye")
        MyGLES32Func.checkGlError("u_vecEye:glGetUniformLocation")

        // uniform(テクスチャユニット0)
        hUNI[5] = GLES32.glGetUniformLocation(programHandle, "u_Texture0")
        MyGLES32Func.checkGlError("u_Texture0:glGetUniformLocation")

        // uniform(テクスチャユニット1)
        hUNI[6] = GLES32.glGetUniformLocation(programHandle, "u_Texture1")
        MyGLES32Func.checkGlError("u_Texture1:glGetUniformLocation")

        // uniform(高さ)
        hUNI[7] = GLES32.glGetUniformLocation(programHandle, "u_Height")
        MyGLES32Func.checkGlError("u_Height:glGetUniformLocation")

        return this
    }

    fun draw(vao: ES32VAOAbs,
             u_matM: FloatArray,
             u_matMVP: FloatArray,
             u_matI: FloatArray,
             u_vecLight: FloatArray,
             u_vecEye: FloatArray,
             u_Texture0: Int,
             u_Texture1: Int,
             u_Height: Float) {
        val model = vao.model

        GLES32.glUseProgram(programHandle)
        MyGLES32Func.checkGlError("UseProgram",this,model)

        // VAOをバインド
        GLES32.glBindVertexArray(vao.hVAO[0])
        MyGLES32Func.checkGlError("BindVertexArray",this,model)

        // uniform(モデル)
        GLES32.glUniformMatrix4fv(hUNI[0],1,false,u_matM,0)
        MyGLES32Func.checkGlError("u_matM",this,model)

        // uniform(モデル×ビュー×プロジェクション)
        GLES32.glUniformMatrix4fv(hUNI[1],1,false,u_matMVP,0)
        MyGLES32Func.checkGlError("u_matMVP",this,model)

        // uniform(逆行列)
        GLES32.glUniformMatrix4fv(hUNI[2],1,false,u_matI,0)
        MyGLES32Func.checkGlError("u_matINV",this,model)

        // uniform(光源位置)
        GLES32.glUniform3fv(hUNI[3],1,u_vecLight,0)
        MyGLES32Func.checkGlError("u_vecLight",this,model)

        // uniform(視点座標)
        GLES32.glUniform3fv(hUNI[4],1,u_vecEye,0)
        MyGLES32Func.checkGlError("u_vecEye",this,model)

        // uniform(テクスチャユニット)
        GLES32.glUniform1i(hUNI[5], u_Texture0)
        MyGLES32Func.checkGlError("u_Texture0",this,model)

        // uniform(テクスチャユニット1)
        GLES32.glUniform1i(hUNI[6], u_Texture1)
        MyGLES32Func.checkGlError("u_Texture1",this,model)

        // uniform(高さ)
        GLES32.glUniform1f(hUNI[7], u_Height)
        MyGLES32Func.checkGlError("u_Height",this,model)

        // モデルを描画
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, model.datIdx.size, GLES32.GL_UNSIGNED_SHORT, 0)

        // VAO解放
        GLES32.glBindVertexArray(0)
    }
}
