package milu.kiriu2010.milugles32.w3x.w32

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyQuaternion
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.shader.es32.ES32PointLight01Shader
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// ---------------------------------------
// クォータニオン
//   カメラの位置を回転させる
// ---------------------------------------
// https://wgld.org/d/webgl/w032.html
// ---------------------------------------
class W32Renderer(ctx: Context): MgRenderer(ctx) {
    // モデル(トーラス)
    private val model = Torus01Model()

    // VAO
    private val vao = ES32VAOIpnc()

    // シェーダ(点光源)
    private val shader = ES32PointLight01Shader(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    // X軸
    private var xaxis = floatArrayOf(1f,0f,0f)

    override fun onDrawFrame(gl: GL10?) {

        // 回転角度
        angle[0] =(angle[0]+5)%360
        angle[1] =(angle[1]+2)%360
        //val t0 = angle[0].toFloat()
        val t1 = angle[1].toFloat()

        // canvasを初期化
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // ------------------------------------------------------------
        // カメラをX軸を中心に回転させている
        // ------------------------------------------------------------
        // X軸が回転軸としてt1度回転したクォータニオンを生成
        qtnNow = MyQuaternion.rotate(t1,xaxis)
        // クォータニオンを使って座標変換(カメラの座標)
        val vecEyeT = qtnNow.toVecIII(vecEye)
        // クォータニオンを使って座標変換(カメラの上方向を表すベクトル)
        val vecEyeUpT = qtnNow.toVecIII(vecEyeUp)

        // カメラの位置
        Matrix.setLookAtM(matV, 0,
                vecEyeT[0], vecEyeT[1], vecEyeT[2],
                0f, 0f, 0f,
                vecEyeUpT[0], vecEyeUpT[1], vecEyeUpT[2])
        Matrix.perspectiveM(matP,0,60f,ratio,0.1f,100f)

        // ビュー×プロジェクション
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // モデルを単位行列にする
        Matrix.setIdentityM(matM,0)
        // モデルを"Y軸"を中心に回転する
        if ( isRunning ) {
            Matrix.rotateM(matM, 0, t1, 0f, 1f, 0f)
        }
        // モデル×ビュー×プロジェクション
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)

        // モデル座標変換行列から逆行列を生成
        Matrix.invertM(matI,0,matM,0)

        // モデルを描画
        shader.draw(vao,matMVP,matM,matI,vecLight,vecEyeT,vecAmbientColor)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダ
        shader.loadShader()

        // モデル生成
        model.createPath()

        // VBO(トーラス)
        vao.makeVIBO(model)

        // カメラの初期位置
        vecEye[0] = 0f
        vecEye[1] = 0f
        vecEye[2] = 10f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vao.deleteVIBO()
        shader.deleteShader()
    }
}