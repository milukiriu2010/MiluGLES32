package milu.kiriu2010.milugles32.w1x.w15

import android.content.Context
import android.opengl.GLES32
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.Matrix
import milu.kiriu2010.gui.model.d2.Triangle01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.shader.es32.ES32Simple01Shader
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpc

// ----------------------------------------------
// ポリゴンに色を塗る(頂点色の指定)
// OpenGL ES 3.2
// ----------------------------------------------
// https://wgld.org/d/webgl/w015.html
// https://android.googlesource.com/platform/development/+/master/samples/OpenGL/HelloOpenGLES32/src/com/example/android/opengl/MyGLRenderer.java
// https://android.keicode.com/basics/opengl-drawing-basic-shapes.php
// https://developer.android.com/training/graphics/opengl/draw
// ----------------------------------------------
class W15Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画モデル
    private val model = Triangle01Model()

    // VAO
    private val vao = ES32VAOIpc()

    // シェーダ
    private val shader = ES32Simple01Shader(ctx)

    override fun onDrawFrame(gl: GL10) {
        // canvasを初期化
        // canvasを初期化する色を設定する
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        // canvasを初期化する際の深度を設定する
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 回転角度
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        /*
        // Create a rotation for the triangle
        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        val time = SystemClock.uptimeMillis() % 10000L;
        mAngle = (360f/10000f) * time.toFloat();
        */

        Matrix.setIdentityM(matM,0)
        Matrix.setRotateM(matM, 0, t0, 0f, 0f, 1.0f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)

        // Draw triangle
        shader.draw(vao,matMVP)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        val ratio = width.toFloat()/height.toFloat()

        // プロジェクション座標変換行列
        Matrix.perspectiveM(matP, 0, 90f,ratio,0.1f,100f)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {

        // シェーダ
        shader.loadShader()

        // モデル生成
        model.createPath()

        // VBO生成
        vao.makeVIBO(model)

        // カメラの位置
        vecEye[0] = 0f
        vecEye[1] = 1f
        vecEye[2] = 2f

        // ビュー座標変換行列
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vao.deleteVIBO()
        shader.deleteShader()
    }
}
