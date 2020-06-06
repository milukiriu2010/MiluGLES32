package milu.kiriu2010.milugles32.w7x.w75

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------
// インスタンシング
// -----------------------------------------
// https://wgld.org/d/webgl/w075.html
// https://wgld.org/d/webgl2/w008.html
// -----------------------------------------
class W75Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(トーラス)
    private var modelTorus = Torus01Model()

    // VAO(トーラス)
    private val vaoTorus = W75VAO()

    // シェーダ
    private val shader = W75Shader(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    override fun onDrawFrame(gl: GL10?) {
        //Log.d(javaClass.simpleName,"onDrawFrame:start")

        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,15f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,50f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // フレームバッファを初期化
        GLES32.glClearColor(0.75f,0.75f,0.75f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // トーラスをレンダリング
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,1f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        shader.draw(vaoTorus,matMVP,matI,vecLight,vecEye)

        //Log.d(javaClass.simpleName,"onDrawFrame:end")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //Log.d(javaClass.simpleName,"onSurfaceChanged:start")

        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        //Log.d(javaClass.simpleName,"onSurfaceChanged:end")
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
                "iradius" to 0.08f,
                "oradius" to 0.15f
        ))

        // VAO(トーラス)
        vaoTorus.makeVIBO(modelTorus)

        // 光源位置
        vecLight[0] = -0.577f
        vecLight[1] =  0.577f
        vecLight[2] =  0.577f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorus.deleteVIBO()
        shader.deleteShader()
    }
}