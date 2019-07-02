package milu.kiriu2010.milugles32.w6x.w64

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import milu.kiriu2010.gui.model.d2.Ray01Model
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpc
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// ---------------------------------------------------------------
// リムライティング
//   モデルの後方からライトが当たっている情報を再現する照明効果
// ---------------------------------------------------------------
// https://wgld.org/d/webgl/w064.html
// ---------------------------------------------------------------
class W64Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()
    // 描画オブジェクト(レイモデル)(というよりXYZ軸)
    private val modelRay = Ray01Model()

    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpnc()
    // VAO(レイ)
    private val vaoRay = ES32VAOIpc()

    // シェーダ(メイン)
    private val shaderMain = W64ShaderMain(ctx)
    // シェーダ(レイをレンダリング)
    private val shaderRay = W64ShaderRay(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // リムライトの色
    var colorRim = floatArrayOf(1f,1f,1f,1f)

    // リムライトの強さ係数
    var u_rimCoef = 1f

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,1f,5f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,10f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // canvasを初期化
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1f)
        GLES20.glClearDepthf(1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // レンダリング(トーラス)
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,1f,0f)
        Matrix.rotateM(matM,0,90f,1f,0f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        shaderMain.draw(vaoTorus,matM,matMVP,matI,
                vecLight,vecCenter,vecEye,colorRim,  u_rimCoef )

        // レンダリング(レイモデル)
        shaderRay.draw(vaoRay,matVP)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glEnable(GLES20.GL_CULL_FACE)

        // シェーダ(メイン)
        shaderMain.loadShader()

        // シェーダ(レイをレンダリング)
        shaderRay.loadShader()

        // モデル生成(トーラス)
        modelTorus.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 0.25f,
                "oradius" to 0.5f
        ))

        // モデル生成(レイモデル)
        modelRay.createPath()

        // VAO(トーラス)
        vaoTorus.makeVIBO(modelTorus)

        // VAO(レイ)
        vaoRay.makeVIBO(modelRay)

        // ライトの向き
        vecLight[0] =  0f
        vecLight[1] =  0f
        vecLight[2] = -1f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorus.deleteVIBO()
        vaoRay.deleteVIBO()

        shaderMain.deleteShader()
        shaderRay.deleteShader()
    }
}
