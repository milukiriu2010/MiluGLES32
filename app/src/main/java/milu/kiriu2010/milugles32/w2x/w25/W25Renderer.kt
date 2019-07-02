package milu.kiriu2010.milugles32.w2x.w25

import android.content.Context
import android.opengl.GLES32
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.Matrix
import milu.kiriu2010.gui.model.d3.Sphere01Model
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.shader.es32.ES32PointLight01Shader
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import milu.kiriu2010.math.MyMathUtil

// ---------------------------------------------------
// 点光源によるライティング
// ---------------------------------------------------
// https://wgld.org/d/webgl/w025.html
// ---------------------------------------------------
class W25Renderer(ctx: Context): MgRenderer(ctx) {

    // 描画モデル(トーラス)
    private val modelTorus = Torus01Model()
    // 描画モデル(球体)
    private val modelSphere = Sphere01Model()

    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpnc()
    // VAO(球体)
    private val vaoSphere = ES32VAOIpnc()

    // シェーダ(点光源)
    private val shader = ES32PointLight01Shader(ctx)

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

        val tx = MyMathUtil.cosf(t0) * 3.5f
        val ty = MyMathUtil.sinf(t0) * 3.5f
        val tz = MyMathUtil.sinf(t0) * 3.5f

        // モデル座標変換行列の生成
        Matrix.setIdentityM(matM,0)
        Matrix.translateM(matM,0,tx,-ty,-tz)
        Matrix.rotateM(matM,0,t0,0f,1f,1f)
        Matrix.invertM(matI,0,matM,0)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)

        // モデル描画(トーラス)
        shader.draw(vaoTorus,matMVP,matM,matI,vecLight,vecEye,vecAmbientColor)

        // モデル座標返還行列の生成
        Matrix.setIdentityM(matM,0)
        Matrix.translateM(matM,0,-tx,ty,tz)
        Matrix.rotateM(matM,0,t0,0f,1f,1f)
        Matrix.invertM(matI,0,matM,0)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)

        // モデル描画(球体)
        shader.draw(vaoSphere,matMVP,matM,matI,vecLight,vecEye,vecAmbientColor)
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

        // 環境光
        vecAmbientColor[0] = 0.1f
        vecAmbientColor[1] = 0.1f
        vecAmbientColor[2] = 0.1f
        vecAmbientColor[3] = 1f

        // ビュー座標変換行列
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])

        // シェーダ(点光源)
        shader.loadShader()

        // 描画モデル(トーラス)
        modelTorus.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 0.5f,
                "oradius" to 1.5f,
                "colorR"  to 0.75f,
                "colorG"  to 0.25f,
                "colorB"  to 0.25f,
                "colorA"  to 1f
        ))

        // 描画モデル(球体)
        modelSphere.createPath(mapOf(
                "row"  to 32f,
                "column" to 32f,
                "radius" to 2f,
                "colorR"  to 0.25f,
                "colorG"  to 0.25f,
                "colorB"  to 0.75f,
                "colorA"  to 1f
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
