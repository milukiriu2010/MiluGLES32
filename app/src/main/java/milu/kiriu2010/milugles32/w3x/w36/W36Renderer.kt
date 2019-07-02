package milu.kiriu2010.milugles32.w3x.w36

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import milu.kiriu2010.gui.model.d2.Line01Model
import milu.kiriu2010.gui.model.d3.Sphere01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpc
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// --------------------------------------
// 点や線のレンダリング
// --------------------------------------
// https://wgld.org/d/webgl/w036.html
// --------------------------------------
class W36Renderer(ctx: Context): MgRenderer(ctx) {

    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()
    // 描画オブジェクト(線)
    private val modelLine = Line01Model()

    // VAO(球体)
    private val vaoSphere = ES32VAOIpc()
    // VAO(線)
    private val vaoLine = ES32VAOIpc()

    // シェーダ
    private val shader = W36Shader(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    // 点のサイズ
    var u_pointSize = 20f

    // 点のサイズの範囲
    val pointSizeRange = FloatBuffer.allocate(2)

    // 線のプリミティブタイプ
    var lineType = GLES32.GL_LINES

    override fun onDrawFrame(gl: GL10?) {
        // canvasを初期化
        GLES32.glClearColor(0f,0f,0f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 回転角度
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // クォータニオンを行列に適用
        var matQ = qtnNow.toMatIV()

        // カメラの位置
        // ビュー座標変換行列
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        // ビュー座標変換行列にクォータニオンの回転を適用
        Matrix.multiplyMM(matV,0,matV,0,matQ,0)
        // ビュー×プロジェクション
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,100f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // 球体をレンダリング
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shader.draw(vaoSphere,matMVP,u_pointSize,GLES32.GL_POINTS)

        // 線をレンダリング
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,90f,1f,0f,0f)
        Matrix.scaleM(matM,0,3f,3f,1f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        GLES32.glLineWidth(5f)
        shader.draw(vaoLine,matMVP,u_pointSize,lineType)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 点の最大ピクセル数をコンソールに出力
        //val pointSizeRange = GLES32.glGetParameter(GLES32.GL_ALIASED_POINT_SIZE_RANGE)
        GLES32.glGetFloatv(GLES32.GL_ALIASED_POINT_SIZE_RANGE,pointSizeRange)
        //callback.receive(pointSizeRange)
        Log.d(javaClass.simpleName,"min:${pointSizeRange.get(0)}")
        Log.d(javaClass.simpleName,"max:${pointSizeRange.get(1)}")

        // 深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)

        // シェーダ
        shader.loadShader()

        // モデル生成(球体)
        modelSphere.createPath(mapOf(
                "row"    to 16f,
                "column" to 16f,
                "radius" to 2f
        ))

        // モデル生成(線)
        modelLine.createPath()

        // VAO(球体)
        vaoSphere.makeVIBO(modelSphere)

        // VAO(線)
        vaoLine.makeVIBO(modelLine)

        // カメラの座標
        vecEye[0] = 0f
        vecEye[1] = 5f
        vecEye[2] = 10f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoSphere.deleteVIBO()
        vaoLine.deleteVIBO()
        shader.deleteShader()
    }
}
