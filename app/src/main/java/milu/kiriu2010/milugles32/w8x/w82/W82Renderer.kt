package milu.kiriu2010.milugles32.w8x.w82

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.color.MgColor
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.math.MyMathUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// ------------------------------------
// VBO逐次更新:パーティクル
// ------------------------------------
// https://wgld.org/d/webgl/w082.html
// ------------------------------------
class W82Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト
    private val model = W82Model()

    // VAO
    private val vao = W82VAO()

    // シェーダ
    private val shader = W82Shader(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // 速度関連
    var velocity = 0f
    val MAX_VELOCITY = 2f
    val SPEED = 0.02f

    // 頂点の色
    private lateinit var u_pointColor: ArrayList<Float>

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%720
        u_pointColor = MgColor.hsva(angle[0]/2,1f,1f,0.5f)

        // フレームバッファを初期化
        GLES32.glClearColor(0f,0f,0f,1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)

        // マウス押下しているかどうかをみて速度を修正
        velocity = when (isRunning) {
            true -> MAX_VELOCITY
            false -> velocity*0.95f
        }

        // -------------------------------------------------------
        // モデル描画
        // -------------------------------------------------------
        shader.draw(vao,velocity*1.25f+0.25f,u_pointColor.toFloatArray(),this)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFuncSeparate(GLES32.GL_SRC_ALPHA,GLES32.GL_ONE,GLES32.GL_ONE,GLES32.GL_ONE)

        // シェーダ
        shader.loadShader()

        // モデル生成
        model.createPath()

        // VAO生成
        vao.usagePos = GLES32.GL_DYNAMIC_DRAW
        vao.makeVIBO(model)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vao.deleteVIBO()
        shader.deleteShader()
    }
}
