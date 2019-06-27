package milu.kiriu2010.milugles32.w4x.w48

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.Sphere01Model
import milu.kiriu2010.gui.model.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import milu.kiriu2010.milugles32.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------
// トゥーンレンダリング
// -----------------------------------------
// https://wgld.org/d/webgl/w048.html
// -----------------------------------------
class W48Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()

    // VAO(球体)
    private val vaoSphere = ES32VAOIpnc()
    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpnc()

    // シェーダ
    private val shader = W48Shader(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    init {
        // テクスチャ
        textures = IntArray(1)

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.toon_w48)
        bmpArray.add(bmp0)
    }

    override fun onDrawFrame(gl: GL10?) {
        // 回転角度
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // canvasを初期化
        GLES32.glClearColor(0.0f, 0.7f, 0.7f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,10f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,100f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // テクスチャのバインド
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textures[0])

        // -------------------------------------------------------
        // トーラス
        // -------------------------------------------------------
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,1f,1f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)

        // モデルをレンダリング
        // 表面をモデルの色で描画
        GLES32.glCullFace(GLES32.GL_BACK)
        shader.draw(vaoTorus,matMVP,matI,vecLight,0,0, floatArrayOf(0f,0f,0f,0f))

        // エッジ用モデルをレンダリング
        // 裏面を黒で描画
        GLES32.glCullFace(GLES32.GL_FRONT)
        shader.draw(vaoTorus,matMVP,matI,vecLight,0,1, floatArrayOf(0f,0f,0f,1f))

        // -------------------------------------------------------
        // 球体
        // -------------------------------------------------------
        Matrix.setIdentityM(matM,0)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)

        // モデルをレンダリング
        // 表面をモデルの色で描画
        GLES32.glCullFace(GLES32.GL_BACK)
        shader.draw(vaoSphere,matMVP,matI,vecLight,0,0, floatArrayOf(0f,0f,0f,0f))

        // エッジ用モデルをレンダリング
        // 裏面を黒で描画
        GLES32.glCullFace(GLES32.GL_FRONT)
        shader.draw(vaoSphere,matMVP,matI,vecLight,0,1, floatArrayOf(0f,0f,0f,1f))
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // canvasを初期化する色を設定する
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // canvasを初期化する際の深度を設定する
        GLES32.glClearDepthf(1f)

        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // テクスチャに使うビットマップをロード
        GLES32.glGenTextures(1,textures,0)
        MyGLES32Func.createTexture(0,textures,bmpArray[0])

        // シェーダ
        shader.loadShader()

        // モデル生成(球体)
        modelSphere.createPath(mapOf(
                "row"    to 32f,
                "column" to 32f,
                "radius" to 1.5f
        ))

        // モデル生成(トーラス)
        modelTorus.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 0.5f,
                "oradius" to 2.5f
        ))

        // VAO(球体)
        vaoSphere.makeVIBO(modelSphere)

        // VAO(トーラス)
        vaoTorus.makeVIBO(modelTorus)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoSphere.deleteVIBO()
        vaoTorus.deleteVIBO()
        shader.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
    }
}
