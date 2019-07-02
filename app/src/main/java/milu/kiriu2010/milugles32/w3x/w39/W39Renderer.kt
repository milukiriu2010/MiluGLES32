package milu.kiriu2010.milugles32.w3x.w39

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d3.Sphere01Model
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnct
import milu.kiriu2010.milugles32.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// ---------------------------------------------
// ステンシルバッファを使ってアウトライン描画
// ---------------------------------------------
// https://wgld.org/d/webgl/w039.html
// ---------------------------------------------
class W39Renderer(ctx: Context): MgRenderer(ctx) {

    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()
    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()

    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpnct()
    // VAO(球体)
    private val vaoSphere = ES32VAOIpnct()

    // シェーダ
    private val shader = W39Shader(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    init {
        // テクスチャ
        textures = IntArray(1)

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w39)
        bmpArray.add(bmp0)
    }

    override fun onDrawFrame(gl: GL10?) {
        // canvasを初期化
        GLES32.glClearColor(0f, 0f, 0f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClearStencil(0)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_STENCIL_BUFFER_BIT)

        // 回転角度
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // クォータニオンを行列に適用
        var matQ = qtnNow.toMatIV()

        // カメラの位置
        // ビュー座標変換行列
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        // ビュー×プロジェクション
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,100f)
        // ビュー座標変換行列にクォータニオンの回転を適用
        Matrix.multiplyMM(matV,0,matV,0,matQ,0)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // テクスチャ0をバインド
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])

        // ステンシルテストを有効にする
        GLES32.glEnable(GLES32.GL_STENCIL_TEST)

        // ステンシルバッファだけに描きこむため、
        // カラーバッファと深度バッファへ描画されないようにする
        GLES32.glColorMask(false,false,false,false)
        GLES32.glDepthMask(false)

        // -----------------------------------------------
        // トーラス(シルエット)用ステンシル設定
        // -----------------------------------------------
        // トーラス(シルエット)が描画されたところの
        // 基準値が１に設定される
        // -----------------------------------------------
        GLES32.glStencilFunc(GLES32.GL_ALWAYS,1, 0.inv())
        GLES32.glStencilOp(GLES32.GL_KEEP,GLES32.GL_REPLACE,GLES32.GL_REPLACE)

        // トーラス(シルエット)をレンダリング
        //   ライティング:OFF
        //   アウトライン:ON
        //   テクスチャ  :OFF
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,1f,1f)
        Matrix.invertM(matI,0,matM,0)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shader.draw(vaoTorus,matMVP,matI,vecLight,0,1,0,0)

        // カラーバッファと深度バッファへ描画されるようにする
        GLES32.glColorMask(true,true,true,true)
        GLES32.glDepthMask(true)

        // -----------------------------------------------
        // 球体モデル用ステンシル設定
        // -----------------------------------------------
        // ステンシルテストで基準値が０のところだけ
        // レンダリングが行われる
        // -----------------------------------------------
        GLES32.glStencilFunc(GLES32.GL_EQUAL,0, 0.inv())
        GLES32.glStencilOp(GLES32.GL_KEEP,GLES32.GL_KEEP,GLES32.GL_KEEP)

        // 球体(背景)をレンダリング
        //   ライティング:OFF
        //   アウトライン:OFF
        //   テクスチャ  :ON
        Matrix.setIdentityM(matM,0)
        Matrix.scaleM(matM,0,50f,50f,50f)
        Matrix.invertM(matI,0,matM,0)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shader.draw(vaoSphere,matMVP,matI,vecLight,0,0,0,1)

        // ステンシルテストを無効にする
        GLES32.glDisable(GLES32.GL_STENCIL_TEST)

        // トーラスをレンダリング
        //   ライティング:ON
        //   アウトライン:OFF
        //   テクスチャ  :OFF
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,1f,1f)
        Matrix.invertM(matI,0,matM,0)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shader.draw(vaoTorus,matMVP,matI,vecLight,1,0,0,0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // ステンシルバッファに設定できる最大値をコンソールに出力
        val stencilBufSize = IntArray(1)
        GLES32.glGetIntegerv(GLES32.GL_STENCIL_BITS,stencilBufSize,0)
        Log.d(javaClass.simpleName,"stencilBufSize:${stencilBufSize[0]}")

        // 深度テストを有効にする
        // 球体(背景)を内側から見るようにしているため、カリングをOFFにしている
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)

        // シェーダ
        shader.loadShader()

        // モデル生成(球体)(背景)
        modelSphere.createPath(mapOf(
                "row"    to 32f,
                "column" to 32f,
                "radius" to 1f,
                "colorR" to 1f,
                "colorG" to 1f,
                "colorB" to 1f,
                "colorA" to 1f
        ))

        // モデル生成(トーラス)(本体とアウトライン)
        modelTorus.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 0.25f,
                "oradius" to 1f
        ))

        // VAO(トーラス)
        vaoTorus.makeVIBO(modelTorus)

        // VAO(球体)
        vaoSphere.makeVIBO(modelSphere)

        // テクスチャ作成し、idをtexturesに保存
        GLES32.glGenTextures(1,textures,0)
        MyGLES32Func.checkGlError("glGenTextures")

        // テクスチャ0をバインド
        MyGLES32Func.createTexture(0,textures,bmpArray[0])

        // 光源位置
        vecLight[0] = 1f
        vecLight[1] = 1f
        vecLight[2] = 1f

        // 視点位置
        vecEye[0] = 0f
        vecEye[1] = 0f
        vecEye[2] = 10f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorus.deleteVIBO()
        vaoSphere.deleteVIBO()
        shader.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
    }
}