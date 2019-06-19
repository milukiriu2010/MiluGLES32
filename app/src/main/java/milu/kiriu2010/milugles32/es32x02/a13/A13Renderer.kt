package milu.kiriu2010.milugles32.es32x02.a13

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.Sphere01Model
import milu.kiriu2010.gui.model.Torus01Model
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpn
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnt
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpt
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.es32x01.a03.ES32a03ShaderA
import milu.kiriu2010.milugles32.es32x01.a03.ES32a03ShaderB
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------------------------
// centroid 修飾子
// -----------------------------------------------------------
//
// -----------------------------------------------------------
// https://wgld.org/d/webgl2/w013.html
// -----------------------------------------------------------
class A13Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = Board00Model()

    // VAO(板ポリゴン)
    private val vaoBoard = ES32VAOIpt()

    // シェーダA
    private val shaderA = ES32a13ShaderA(ctx)
    // シェーダB
    private val shaderB = ES32a13ShaderB(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // モデル座標変換行列⇒逆行列⇒転置行列
    val matN = FloatArray(16)

    init {
        // テクスチャ
        textures = IntArray(1)

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_map_a13)
        bmpArray.add(bmp0)
    }

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,5f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,60f,0.5f,0.1f,20f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // フレームバッファを初期化
        GLES32.glClearColor(0.1f,0.1f,0.1f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)


        // 板ポリゴンをレンダリング(default)
        GLES32.glViewport(0,0,renderW/2,renderH)
        shaderA.draw(vaoBoard,matMVP,0)

        // 板ポリゴンをレンダリング(centroid)
        GLES32.glViewport(renderW/2,0,renderW/2,renderH)
        shaderB.draw(vaoBoard,matMVP,0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        // テクスチャを作成
        GLES32.glGenTextures(1,textures,0)
        // テクスチャに使う画像をロード
        MyGLES32Func.createTexture(0,textures,bmpArray[0])

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glDisable(GLES32.GL_CULL_FACE)

        // シェーダA
        shaderA.loadShader()

        // シェーダB
        shaderB.loadShader()

        // モデル生成(板ポリゴン)
        modelBoard.createPath(mapOf(
                "pattern" to 101f
        ))

        // VAO(板ポリゴン)
        vaoBoard.makeVIBO(modelBoard)

        // ライトの向き
        vecLight[0] = 5f
        vecLight[1] = 2f
        vecLight[2] = 5f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoBoard.deleteVIBO()
        shaderA.deleteShader()
        shaderB.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
    }
}