package milu.kiriu2010.milugles32.w8x.w89

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d3.Sphere01Model
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpn
import milu.kiriu2010.math.MyMathUtil
import milu.kiriu2010.milugles32.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// --------------------------------------
// スフィア環境マッピング
// --------------------------------------
// https://wgld.org/d/webgl/w089.html
// --------------------------------------
class W89Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()
    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()

    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpn()
    // VBO(球体)
    private val vaoSphere = ES32VAOIpn()

    // シェーダ
    private val shader = W89Shader(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // テクスチャID
    var textureID = 0

    init {
        // テクスチャ
        textures = IntArray(8)

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.w89_matcap0)
        val bmp1 = BitmapFactory.decodeResource(ctx.resources, R.drawable.w89_matcap1)
        val bmp2 = BitmapFactory.decodeResource(ctx.resources, R.drawable.w89_matcap2)
        val bmp3 = BitmapFactory.decodeResource(ctx.resources, R.drawable.w89_matcap3)
        val bmp4 = BitmapFactory.decodeResource(ctx.resources, R.drawable.w89_matcap4)
        val bmp5 = BitmapFactory.decodeResource(ctx.resources, R.drawable.w89_matcap5)
        val bmp6 = BitmapFactory.decodeResource(ctx.resources, R.drawable.w89_matcap6)
        val bmp7 = BitmapFactory.decodeResource(ctx.resources, R.drawable.w89_matcap7)
        bmpArray.add(bmp0)
        bmpArray.add(bmp1)
        bmpArray.add(bmp2)
        bmpArray.add(bmp3)
        bmpArray.add(bmp4)
        bmpArray.add(bmp5)
        bmpArray.add(bmp6)
        bmpArray.add(bmp7)
    }

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // フレームバッファを初期化
        GLES32.glClearColor(1f,1f,1f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // テクスチャの適用
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[textureID])

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,10f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,20f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        Matrix.setIdentityM(matM,0)
        Matrix.translateM(matM,0,0f,MyMathUtil.sinf(t0),0f)
        Matrix.rotateM(matM,0,t0,1f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)

        // 法線変換マトリックスの生成
        val matMV = FloatArray(16)
        Matrix.multiplyMM(matMV,0,matV,0,matM,0)
        Matrix.invertM(matI,0,matMV,0)
        val matN = FloatArray(16)
        Matrix.transposeM(matN,0,matI,0)

        // 球体を描画
        shader.draw(vaoSphere,matMVP,matN)

        // トーラスを描画
        shader.draw(vaoTorus,matMVP,matN)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        // テクスチャを作成
        GLES32.glGenTextures(8,textures,0)
        // テクスチャに使う画像をロード
        (0..7).forEach {
            MyGLES32Func.createTexture(it,textures,bmpArray[it])
        }

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダ
        shader.loadShader()

        // モデル生成(トーラス)
        modelTorus.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 0.7f,
                "oradius" to 2f
        ))

        // モデル生成(球体)
        modelSphere.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "radius" to 1f
        ))

        // VAO(トーラス)
        vaoTorus.makeVIBO(modelTorus)

        // VAO(球体)
        vaoSphere.makeVIBO(modelSphere)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorus.deleteVIBO()
        vaoSphere.deleteVIBO()
        shader.deleteShader()
    }
}
