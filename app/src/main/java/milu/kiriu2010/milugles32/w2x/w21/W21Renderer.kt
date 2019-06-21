package milu.kiriu2010.milugles32.w2x.w21

import android.content.Context
import android.opengl.GLES32
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.Matrix
import milu.kiriu2010.gui.model.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.shader.es32.ES32DirectionalLight01Shader
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc

// ---------------------------------------------------
// 平行光源によるライティング
// ---------------------------------------------------
// https://wgld.org/d/webgl/w021.html
// ---------------------------------------------------
class W21Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画モデル(トーラス)
    private val model = Torus01Model()

    // シェーダ(平行光源)
    private val shader = ES32DirectionalLight01Shader(ctx)

    // VAO
    private val vao = ES32VAOIpnc()

    override fun onDrawFrame(gl: GL10) {
        // 回転角度
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // canvasを初期化
        // canvasを初期化する色を設定する
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        // canvasを初期化する際の深度を設定する
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // ビュー×プロジェクション座標変換行列
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // モデルを座標変換
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,1f,1f)
        Matrix.invertM(matI,0,matM,0)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)

        // モデル描画
        shader.draw(vao,matMVP,matI,vecLight)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        val ratio = width.toFloat()/height.toFloat()

        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,100f)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // カメラの位置
        vecEye[0] = 0f
        vecEye[1] = 0f
        vecEye[2] = 20f

        // ビュー座標変換行列
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])

        // シェーダ(平行光源)
        shader.loadShader()

        // 描画モデル(トーラス)
        model.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 1f,
                "oradius" to 2f
        ))

        // VAO生成
        vao.makeVIBO(model)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vao.deleteVIBO()
        shader.deleteShader()
    }
}
