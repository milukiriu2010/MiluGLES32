package milu.kiriu2010.milugles32.w4x.w43

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d3.Sphere01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnct
import milu.kiriu2010.milugles32.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------------------
// 視差マッピング
// -----------------------------------------------------
// 視線や高さを考慮したバンプマッピング
// -----------------------------------------------------
// 視差マッピングを行うためには、
// 法線マップと高さマップの２つのテクスチャが必要
// 高さマップは、画像データに高さデータを格納したもので
// 通常モノクロで扱う
// 黒⇒0(最も低い),白⇒1(最も高い)
// -----------------------------------------------------
// https://wgld.org/d/webgl/w043.html
// -----------------------------------------------------
class W43Renderer(ctx: Context): MgRenderer(ctx) {

    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()

    // VBO(球体)
    private val vaoSphere = ES32VAOIpnct()

    // シェーダ
    private val shader = W43Shader(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    // 高さ情報
    var hScale = 0.5f

    init {
        // テクスチャ
        textures = IntArray(2)

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w43_0)
        val bmp1 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w43_1)
        bmpArray.add(bmp0)
        bmpArray.add(bmp1)
    }

    override fun onDrawFrame(gl: GL10?) {
        // 回転角度
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // canvasを初期化
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,5f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,100f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // 球体をレンダリング
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[1])
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,-t0,0f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        shader.draw(vaoSphere,matM,matMVP,matI,vecLight,vecEye,0,1,hScale)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)

        // シェーダ
        shader.loadShader()

        // モデル生成(球体)
        modelSphere.createPath(mapOf(
                "row" to 32f,
                "column" to 32f,
                "radius" to 1f
        ))

        // VAO
        vaoSphere.makeVIBO(modelSphere)

        // テクスチャ作成し、idをtexturesに保存
        GLES32.glGenTextures(2,textures,0)
        MyGLES32Func.checkGlError("glGenTextures")
        // 法線マップテクスチャ
        MyGLES32Func.createTexture(0,textures,bmpArray[0])
        // 高さマップテクスチャ
        MyGLES32Func.createTexture(1,textures,bmpArray[1])

        // 光源位置
        vecLight[0] = -10f
        vecLight[1] = 10f
        vecLight[2] = 10f
        // 視点位置
        vecEye[0] = 0f
        vecEye[1] = 0f
        vecEye[2] = 5f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoSphere.deleteVIBO()
        shader.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
    }
}