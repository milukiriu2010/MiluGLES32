package milu.kiriu2010.milugles32.w3x.w34

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyQuaternion
import milu.kiriu2010.gui.model.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.shader.es32.ES32PointLight01Shader
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// --------------------------------------
// クォータニオンによる球面線形補間
// --------------------------------------
// https://wgld.org/d/webgl/w034.html
// --------------------------------------
class W34Renderer(ctx: Context): MgRenderer(ctx) {

    // モデル(トーラス)
    private val model = Torus01Model()

    // VAO(トーラス)
    private val vao = ES32VAOIpnc()

    // シェーダ(点光源)
    private val shader = ES32PointLight01Shader(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    // 経過時間係数
    var ktime = 0.5f

    override fun onDrawFrame(gl: GL10?) {
        // 回転角度
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // canvasを初期化
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // ビュー×プロジェクション
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // 回転クォータニオンの生成
        var aQuaternion = MyQuaternion.rotate(t0, floatArrayOf(1f,0f,0f))
        var bQuaternion = MyQuaternion.rotate(t0, floatArrayOf(0f,1f,0f))
        var sQuaternion = MyQuaternion.slerp(aQuaternion,bQuaternion,ktime)

        // モデル描画
        // 赤(X軸を中心に回転)
        draw(aQuaternion, floatArrayOf(0.5f,  0f,  0f,1f))
        // 緑(Y軸を中心に回転)
        draw(bQuaternion, floatArrayOf(  0f,0.5f,  0f,1f))
        // 青(線形補間軸を中心に回転)
        draw(sQuaternion, floatArrayOf(  0f,  0f,0.5f,1f))
    }

    // ------------------------------------
    // モデル描画
    // ------------------------------------
    // ambientColor: 環境光の色
    // ------------------------------------
    private fun draw(qtn: MyQuaternion, ambientColor: FloatArray) {
        // モデルを単位行列にする
        Matrix.setIdentityM(matM,0)
        // モデル座標変換行列×クォータニオンが適用された座標変換行列
        var matQ = qtn.toMatIV()
        Matrix.multiplyMM(matM,0,matM,0,matQ,0)
        Matrix.translateM(matM,0,0f,0f,-5f)
        // モデル×ビュー×プロジェクション
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        // モデル座標変換行列から逆行列を生成
        Matrix.invertM(matI,0,matM,0)

        // モデルを描画
        shader.draw(vao,matMVP,matM,matI,vecLight,vecEye,ambientColor)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        Matrix.perspectiveM(matP,0,60f,ratio,0.1f,100f)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダ
        shader.loadShader()

        // モデル生成
        model.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 0.5f,
                "oradius" to 1.5f,
                "colorR"  to 0.5f,
                "colorG"  to 0.5f,
                "colorB"  to 0.5f,
                "colorA"  to 1f
        ))

        // VAO(トーラス)
        vao.makeVIBO(model)

        // 点光源の位置
        vecLight[0] = 15f
        vecLight[1] = 10f
        vecLight[2] = 15f

        // カメラの初期位置
        vecEye[0] = 0f
        vecEye[1] = 0f
        vecEye[2] = 20f

        // カメラの位置
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                0f, 0f, 0f,
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vao.deleteVIBO()
        shader.deleteShader()
    }
}