package milu.kiriu2010.milugles32.w2x.w28

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES32
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpct
import milu.kiriu2010.milugles32.R

// --------------------------------------------------------------------------------------------
// テクスチャパラメータ
// --------------------------------------------------------------------------------------------
// テクスチャパラメータとは
// レンダリングされる際の品質や性質にかかわる特性
// まったく同じ解像度・同じサイズの画像を適用したテクスチャでも
// テクスチャパラメータが異なっている場合、レンダリング結果が大きく変化する
// --------------------------------------------------------------------------------------------
// テクスチャの品質に関するパラメータ
//   テクスチャが伸縮(拡大縮小)されるときに、どのように扱われるか指定する。
//   これらを変化させることでレンダリングされた際にテクスチャに適用されるフィルタが変化します。
//   結果、美しく補間されたテクスチャを使った高品質なレンダリングを行なうことが可能になります。
//   ミップマップが使われるのは、イメージを縮小表示しなければならないとき。
// --------------------------------------------------------------------------------------------
// テクスチャの性質に関するパラメータ
//   通常の範囲外のテクスチャ座標を指定された際のテクスチャの挙動が変化する。
//   テクスチャ座標は0～1の範囲に収まるようにしなければならないが、
//   実際には、範囲外のテクスチャ座標を指定してもレンダリング自体は行われる。
//   このパラメータを指定することで、範囲外の値をどのように扱うか指定することができる
// --------------------------------------------------------------------------------------------
// https://wgld.org/d/webgl/w028.html
// --------------------------------------------------------------------------------------------
class W28Renderer(ctx: Context): MgRenderer(ctx) {

    // 描画オブジェクト
    private val model = W28Model()

    // VAO
    private val vao = ES32VAOIpct()

    // シェーダ
    private val shader = W28Shader(ctx)

    init {
        // テクスチャ
        textures = IntArray(2)

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w027_0)
        val bmp1 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w028_1)
        bmpArray.add(bmp0)
        bmpArray.add(bmp1)
    }

    override fun onDrawFrame(gl: GL10) {

        // canvasを初期化
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 回転角度
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // テクスチャをバインドする
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textures[0])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textures[1])

        // ビュー×プロジェクション座標変換行列
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)


        // ２つの画像をテクスチャパラメータを変えて描画
        // テクスチャパラメータは、その時点ｄえバインドされているテクスチャに対してのみ有効
        // ２枚目の画像を読み込んだテクスチャが常にバインドされている状態になるため、
        // １枚目の画像を読み込んだテクスチャには、品質・性質に一切の変化が発生しない。
        renderMap(1,t0)
        renderMap(2,t0)
        renderMap(3,t0)
        renderMap(4,t0)
        renderMap(5,t0)
        renderMap(6,t0)
        renderMap(7,t0)
        renderMap(8,t0)
        renderMap(9,t0)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        val ratio = width.toFloat()/height.toFloat()

        Matrix.perspectiveM(matP,0,60f,ratio,0.1f,100f)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        // 深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)

        // シェーダ
        shader.loadShader()

        // モデル生成
        model.createPath()

        // VAO生成
        vao.makeVIBO(model)

        // テクスチャ作成し、idをtexturesに保存
        GLES32.glGenTextures(2,textures,0)
        MyGLES32Func.checkGlError("glGenTextures")

        // テクスチャ0をバインド
        MyGLES32Func.createTexture(0,textures,bmpArray[0])
        // テクスチャ1をバインド
        MyGLES32Func.createTexture(1,textures,bmpArray[1])

        // カメラの座標
        vecEye[0] = 0f
        vecEye[1] = 0f
        vecEye[2] = 8f

        // カメラの位置
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
    }

    private fun renderMap(id: Int, t1: Float) {

        when ( id ) {
            1 -> {
                // -----------------------------------------------
                // １つ目
                // -----------------------------------------------
                // 縮小時の補間設定
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST)
                // 拡大時の補間設定
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_NEAREST)
                // 範囲外のテクスチャ座標が指定されたときの設定(横)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S,GLES32.GL_REPEAT)
                // 範囲外のテクスチャ座標が指定されたときの設定(縦)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T,GLES32.GL_REPEAT)
                render(t1, floatArrayOf(-3f,3f,0f))
            }
            2 -> {
                // -----------------------------------------------
                // ２つ目
                // -----------------------------------------------
                // 縮小時の補間設定
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR)
                // 拡大時の補間設定
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S,GLES32.GL_REPEAT)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T,GLES32.GL_REPEAT)
                render(t1, floatArrayOf(-3f,0f,0f))
            }
            3 -> {
                // -----------------------------------------------
                // ３つ目
                // -----------------------------------------------
                // 縮小時の補間設定
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST_MIPMAP_NEAREST)
                // 拡大時の補間設定
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S,GLES32.GL_REPEAT)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T,GLES32.GL_REPEAT)
                render(t1, floatArrayOf(-3f,-3f,0f))
            }
            4 -> {
                // -----------------------------------------------
                // ４つ目
                // -----------------------------------------------
                // 縮小時の補間設定
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST_MIPMAP_LINEAR)
                // 拡大時の補間設定
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S,GLES32.GL_REPEAT)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T,GLES32.GL_REPEAT)
                render(t1, floatArrayOf(0f,3f,0f))
            }
            5 -> {
                // -----------------------------------------------
                // ５つ目
                // -----------------------------------------------
                // 縮小時の補間設定
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR_MIPMAP_NEAREST)
                // 拡大時の補間設定
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S,GLES32.GL_REPEAT)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T,GLES32.GL_REPEAT)
                render(t1, floatArrayOf(0f,0f,0f))
            }
            6 -> {
                // -----------------------------------------------
                // ６つ目
                // -----------------------------------------------
                // 縮小時の補間設定
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR_MIPMAP_LINEAR)
                // 拡大時の補間設定
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S,GLES32.GL_REPEAT)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T,GLES32.GL_REPEAT)
                render(t1, floatArrayOf(0f,-3f,0f))
            }
            7 -> {
                // -----------------------------------------------
                // ７つ目
                // -----------------------------------------------
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S,GLES32.GL_REPEAT)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T,GLES32.GL_REPEAT)
                render(t1, floatArrayOf(3f,3f,0f))
            }
            8 -> {
                // -----------------------------------------------
                // ８つ目
                // -----------------------------------------------
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S,GLES32.GL_MIRRORED_REPEAT)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T,GLES32.GL_MIRRORED_REPEAT)
                render(t1, floatArrayOf(3f,0f,0f))
            }
            9 -> {
                // -----------------------------------------------
                // ９つ目
                // -----------------------------------------------
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S,GLES32.GL_CLAMP_TO_EDGE)
                GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T,GLES32.GL_CLAMP_TO_EDGE)
                render(t1, floatArrayOf(3f,-3f,0f))
            }
        }
    }

    private fun render(t: Float, trans: FloatArray) {
        // モデル座標変換行列の生成
        Matrix.setIdentityM(matM,0)
        Matrix.translateM(matM,0,trans[0],trans[1],trans[2])
        if ( isRunning == true ) {
            Matrix.rotateM(matM,0,t,0f,1f,0f)
        }
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)

        // uniform変数の登録と描画
        shader.draw(vao,matMVP,0,1)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vao.deleteVIBO()
        shader.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
    }
}
