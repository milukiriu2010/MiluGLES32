package milu.kiriu2010.milugles32.w8x.w86

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpc
import java.nio.ByteBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// ------------------------------------
// 描画結果から色を取得
// ------------------------------------
// https://wgld.org/d/webgl/w086.html
// ------------------------------------
class W86Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(板ポリゴン)
    private val model = Board00Model()

    // VAO
    private val vao = ES32VAOIpc()

    // シェーダ
    private val shader = W86Shader(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // タッチ位置の色
    val colorBuf = ByteBuffer.allocate(4)

    override fun onDrawFrame(gl: GL10?) {
        // フレームバッファを初期化
        GLES20.glClearColor(0f,0f,0f,1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // -------------------------------------------------------
        // モデル描画
        // -------------------------------------------------------
        shader.draw(vao)

        GLES20.glReadPixels(touchP.x.toInt(),touchP.y.toInt(),1,1,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,colorBuf)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        // プロジェクション座標変換行列
        Matrix.perspectiveM(matP, 0, 90f,ratio,0.1f,100f)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // シェーダ
        shader.loadShader()

        // モデル生成(球体)
        model.createPath(mapOf(
                "pattern" to 29f
        ))

        // VAO
        vao.makeVIBO(model)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vao.deleteVIBO()
        shader.deleteShader()
    }
}