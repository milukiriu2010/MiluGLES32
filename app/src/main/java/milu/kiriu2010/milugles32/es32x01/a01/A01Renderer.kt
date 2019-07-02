package milu.kiriu2010.milugles32.es32x01.a01

import android.content.Context
import android.opengl.GLES32
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.Matrix
import android.util.Log
import milu.kiriu2010.gui.model.*
import milu.kiriu2010.gui.model.d3.Axis01Model
import milu.kiriu2010.gui.model.d3.Cube01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.shader.es32.ES32Simple01Shader
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpc
import milu.kiriu2010.math.MyMathUtil
import kotlin.math.sqrt


class A01Renderer(ctx: Context): MgRenderer(ctx) {

    // 描画モデル(静止している立方体)
    private val modelStill = Cube01Model()
    // 描画モデル(動く立方体)
    private val modelMotion = Cube01Model()
    // 座標軸モデル
    private val modelAxis = Axis01Model()

    // VAO(静止している立方体)
    private val vaoStill = ES32VAOIpc()
    // VAO(動く立方体)
    private val vaoMotion = ES32VAOIpc()
    // VAO(座標軸モデル)
    private val vaoAxis = ES32VAOIpc()

    // シェーダ(特殊効果なし)
    private val shaderSimple = ES32Simple01Shader(ctx)

    override fun onDrawFrame(gl: GL10) {
        // デフォルトバッファをクリア
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 回転角度
        angle[0] =(angle[0]+1)%360
        val t1 = angle[0].toFloat()

        val x = MyMathUtil.cosf(t1)
        val y = MyMathUtil.sinf(t1)

        // ビュー×プロジェクション
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,10f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,45f,1f,0.1f,100f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // ----------------------------------------------
        // 静止したモデルを描画
        // ----------------------------------------------
        Matrix.setIdentityM(matM,0)
        // モデル×ビュー×プロジェクション
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderSimple.draw(vaoStill,matMVP,GLES32.GL_LINES)

        // ----------------------------------------------
        // 回転するモデルを描画(右上)
        // ----------------------------------------------
        // モデルを単位行列にする
        Matrix.setIdentityM(matM,0)
        // モデルを移動する(元の位置に戻す)
        Matrix.translateM(matM,0,1f,1f,0f)
        // モデルをZ軸を中心に回転させる
        Matrix.rotateM(matM,0,t1,0f,0f,1f)
        // モデルを移動する(上にずらす)
        Matrix.translateM(matM,0,-1f,-1f,0F)
        // モデル×ビュー×プロジェクション
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderSimple.draw(vaoMotion,matMVP)

        // ----------------------------------------------
        // 回転するモデルを描画(右下)
        // ----------------------------------------------
        // モデルを単位行列にする
        Matrix.setIdentityM(matM,0)
        // モデルを移動する(元の位置に戻す)
        Matrix.translateM(matM,0,1f,-1f,0f)
        // モデルをZ軸を中心に回転させる
        Matrix.rotateM(matM,0,t1,0f,0f,1f)
        // モデルを移動する(上にずらす)
        Matrix.translateM(matM,0,-1f,1f,0F)
        // モデル×ビュー×プロジェクション
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderSimple.draw(vaoMotion,matMVP)

        // ----------------------------------------------
        // 回転するモデルを描画(左上)
        // ----------------------------------------------
        // モデルを単位行列にする
        Matrix.setIdentityM(matM,0)
        // モデルを移動する(元の位置に戻す)
        Matrix.translateM(matM,0,-1f,1f,0f)
        // モデルをZ軸を中心に回転させる
        Matrix.rotateM(matM,0,t1,0f,0f,1f)
        // モデルを移動する(上にずらす)
        Matrix.translateM(matM,0,1f,-1f,0F)
        // モデル×ビュー×プロジェクション
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderSimple.draw(vaoMotion,matMVP)

        // ----------------------------------------------
        // 回転するモデルを描画(左下)
        // ----------------------------------------------
        // モデルを単位行列にする
        Matrix.setIdentityM(matM,0)
        // モデルを移動する(元の位置に戻す)
        Matrix.translateM(matM,0,-1f,-1f,0f)
        // モデルをZ軸を中心に回転させる
        Matrix.rotateM(matM,0,t1,0f,0f,1f)
        // モデルを移動する(上にずらす)
        Matrix.translateM(matM,0,1f,1f,0f)
        // モデル×ビュー×プロジェクション
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderSimple.draw(vaoMotion,matMVP)

        // ----------------------------------------------
        // 座標軸を描画
        // ----------------------------------------------
        GLES32.glLineWidth(5f)
        Matrix.setIdentityM(matM,0)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderSimple.draw(vaoAxis,matMVP,GLES32.GL_LINES)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        // OpenGL ES 3.0 (4.5.0 - Build 23.20.16.4877)
        Log.d(javaClass.simpleName,"version=${GLES32.glGetString(GLES32.GL_VERSION)}")

        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダ(特殊効果なし)
        shaderSimple.loadShader()

        // 描画モデル(静止している立方体)
        modelStill.createPath(mapOf(
                "pattern" to 20f,
                "colorR" to 1f,
                "colorG" to 0f,
                "colorB" to 0f,
                "colorA" to 1f
        ))

        // 描画モデル(動く立方体)
        modelMotion.createPath()

        // 描画モデル(座標軸)
        modelAxis.createPath(mapOf("scale" to 3f))

        // VAO(静止している立方体)
        vaoStill.makeVIBO(modelStill)
        // VAO(動く立方体)
        vaoMotion.makeVIBO(modelMotion)
        // VAO(座標軸)
        vaoAxis.makeVIBO(modelAxis)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
    }
}
