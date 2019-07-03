package milu.kiriu2010.milugles32.w8x.w87

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import milu.kiriu2010.gui.model.d3.Sphere01Model
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpc
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// --------------------------------------
// シェーダ(フラットシェーディング)
// --------------------------------------
// https://wgld.org/d/webgl/w087.html
// --------------------------------------
class W87Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()
    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()

    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpc()
    // VAO(球体)
    private val vaoSphere = ES32VAOIpc()

    // シェーダ
    private val shader = W87Shader(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // canvasを初期化
        GLES20.glClearColor(0.3f,0.7f,0.7f,1f)
        GLES20.glClearDepthf(1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,20f,0f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,0f,-1f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,90f,ratio,0.1f,50f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // 球体を描画
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shader.draw(vaoSphere,matM,matMVP,vecLight)

        // -------------------------------------------------------
        // トーラス描画(9個)
        // -------------------------------------------------------
        (0..8).forEach { i ->
            Matrix.setIdentityM(matM,0)
            Matrix.rotateM(matM,0,i.toFloat()*360f/9f,0f,1f,0f)
            Matrix.translateM(matM,0,0f,0f,10f)
            Matrix.rotateM(matM,0,t0,1f,1f,0f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            shader.draw(vaoTorus,matM,matMVP,vecLight)
        }
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

        // シェーダ
        shader.loadShader()

        // モデル生成(トーラス)
        modelTorus.createPath(mapOf(
                "row"     to 8f,
                "column"  to 16f,
                "iradius" to 1.7f,
                "oradius" to 2f,
                "colorR"  to 1f,
                "colorG"  to 1f,
                "colorB"  to 1f,
                "colorA"  to 1f
        ))

        // モデル生成(球体)
        modelSphere.createPath(mapOf(
                "row" to 8f,
                "column" to 8f,
                "radius" to 5f,
                "colorR"  to 1f,
                "colorG"  to 1f,
                "colorB"  to 1f,
                "colorA"  to 1f
        ))

        // VAO(トーラス)
        vaoTorus.makeVIBO(modelTorus)

        // VAO(球体)
        vaoSphere.makeVIBO(modelSphere)

        // ライトの向き
        vecLight[0] = -0.5f
        vecLight[1] =  1f
        vecLight[2] =  2f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorus.deleteVIBO()
        vaoSphere.deleteVIBO()
        shader.deleteShader()
    }
}