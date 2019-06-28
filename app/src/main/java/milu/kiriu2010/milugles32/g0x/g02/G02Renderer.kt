package milu.kiriu2010.milugles32.g0x.g02

import android.content.Context
import android.opengl.GLES32
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.os.SystemClock
import milu.kiriu2010.gui.model.d2.Square01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIp

// ----------------------------------------------
// 同心円を描く
// 時間経過とマウスカーソル座標を利用したサンプル
// ----------------------------------------------
// https://wgld.org/d/glsl/g002.html
// ----------------------------------------------
class G02Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画モデル
    private val model = Square01Model()

    // VAO
    private val vao = ES32VAOIp()

    // シェーダ
    private val shader = G02Shader(ctx)

    // 時間管理
    private var startTime = SystemClock.uptimeMillis()
    // サンプルが動作する際に、どの程度時間が経過しているのかをシェーダに渡す
    private var u_time = 0f

    // アニメーションのスピード
    var u_speed = 5f

    // 同心円の間隔
    var u_gap = 5f

    override fun onDrawFrame(gl: GL10) {
        // canvasを初期化
        // canvasを初期化する色を設定する
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)

        // サンプルが動作する際に、どの程度時間が経過しているのかをシェーダに渡す
        u_time = (SystemClock.uptimeMillis() - startTime).toFloat() * 0.001f

        // 描画
        shader.draw(vao,
                u_time,
                floatArrayOf(touchP.x,touchP.y),
                floatArrayOf(renderW.toFloat(),renderH.toFloat()),
                u_speed,
                u_gap
        )
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        renderW = width
        renderH = height

        startTime = SystemClock.uptimeMillis()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {

        // シェーダ
        shader.loadShader()

        // モデル生成
        model.createPath(mapOf(
                "pattern" to 1f
        ))

        // VBO
        vao.makeVIBO(model)

        // タッチ位置
        // 左上が原点で0.0～1.0とする
        touchP.also {
            it.x = 0.5f
            it.y = 0.5f
        }
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vao.deleteVIBO()
        shader.deleteShader()
    }
}
