package milu.kiriu2010.milugles32.w8x.w81

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.model.d3.Sphere01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIp
import milu.kiriu2010.math.MyMathUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// ------------------------------------
// VBOを逐次更新
// ------------------------------------
// https://wgld.org/d/webgl/w081.html
// ------------------------------------
class W81Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(球体)
    private val model = Sphere01Model()

    // VAO
    private val vao = ES32VAOIp()

    // シェーダ
    private val shader = W81Shader(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // 描画点サイズ
    // 10.0-40.0
    var u_pointSize = 25f

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()
        val scale = MyMathUtil.cosf(t0) + 2.0f


        // フレームバッファを初期化
        GLES32.glClearColor(0.7f,0.7f,0.7f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // クォータニオンを行列に適用
        val matQ = qtnNow.toMatIV()

        // ビュー×プロジェクション座標変換行列
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.multiplyMM(matV,0,matV,0,matQ,0)
        Matrix.perspectiveM(matP,0,90f,ratio,0.1f,15f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // -------------------------------------------------------
        // モデル描画
        // -------------------------------------------------------
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0.1f,1f,1f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shader.draw(vao,matMVP,u_pointSize,t0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFuncSeparate(GLES32.GL_ONE,GLES32.GL_ONE,GLES32.GL_ONE,GLES32.GL_ONE)

        // シェーダ
        shader.loadShader()

        // モデル生成(球体)
        model.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "scale"   to 2f,
                "radius"  to 1f
        ))

        // VAO生成
        vao.usagePos = GLES32.GL_DYNAMIC_DRAW
        vao.makeVIBO(model)

        // 視点座標
        vecEye[0] = 0f
        vecEye[1] = 0f
        vecEye[2] = 5f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vao.deleteVIBO()
        shader.deleteShader()
    }
}