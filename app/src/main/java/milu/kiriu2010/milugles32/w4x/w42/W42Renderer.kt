package milu.kiriu2010.milugles32.w4x.w42

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.Sphere01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnct
import milu.kiriu2010.milugles32.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// ---------------------------------------------------------------------
// バンプマッピング
// ---------------------------------------------------------------------
// 法線マップなどを用いることによって、
// あたかも凸凹があるかのように見せることができるライティングテクニック
// ---------------------------------------------------------------------
// 法線マップは、法線に関する情報を格納した特殊なテクスチャ
//   RGB=>XYZとして扱う
// テクスチャ上にある法線の情報は接空間と呼ばれる空間上に存在している
// ---------------------------------------------------------------------
// バンプマッピングではテクスチャから得られた法線と正しく演算を行うため
// モデルがもともと持っている頂点法線を使って３つのベクトルを定義する。
//
// 法線ベクトル
//   頂点法線をそのまま使う
// 接線ベクトル
//   頂点法線に対して垂直なベクトルで、
//   かつテクスチャの横方向に対して平行となるベクトル
//   Y軸と法線ベクトルとの間で外積を取ることで算出する
// 従法線ベクトル
//   頂点法線に対して垂直なベクトルで、
//   かつテクスチャの縦方向に対して平行となるベクトル
//   接線ベクトルと法線ベクトルとの間で外積をとることで算出する
// ---------------------------------------------------------------------
// 法線ベクトルにモデルの法線をそのまま使うことで、
// テクスチャ上にあるZ値(つまりRGBのうちB成分)と
// 本来モデルが持っている法線の向きをそろえることができる。
// ---------------------------------------------------------------------
// https://wgld.org/d/webgl/w042.html
// ---------------------------------------------------------------------
class W42Renderer(ctx: Context): MgRenderer(ctx) {

    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()

    // VAO(球体)
    private val vaoSphere = ES32VAOIpnct()

    // シェーダ
    private val shader = W42Shader(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    init {
        // テクスチャ
        textures = IntArray(1)

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w42)
        bmpArray.add(bmp0)
    }

    override fun onDrawFrame(gl: GL10?) {
        // 回転角度
        angle[0] =(angle[0]+2)%360
        angle[1] =(angle[1]+1)%360
        val t0 = angle[0].toFloat()
        val t1 = angle[1].toFloat()

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
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,-t0,0f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        shader.draw(vaoSphere,matM,matMVP,matI,vecLight,vecEye,0)
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
        GLES32.glGenTextures(1,textures,0)
        MyGLES32Func.checkGlError("glGenTextures")
        MyGLES32Func.createTexture(0,textures,bmpArray[0])

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
