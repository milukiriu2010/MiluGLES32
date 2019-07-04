package milu.kiriu2010.milugles32.w8x.w84

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.color.MgColor
import milu.kiriu2010.gui.model.d3.Sphere01Model
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnt
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.es32x01.a03.ES32a03ShaderA
import milu.kiriu2010.milugles32.es32x01.a03.ES32a03ShaderB
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------------------------
// MRT(Multiple Render Targets)
// -----------------------------------------------------------
// 複数のレンダリングターゲットに対して同時に異なる出力を行う
// -----------------------------------------------------------
// https://wgld.org/d/webgl/w084.html
// -----------------------------------------------------------
class W84Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = Board00Model()

    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpnt()
    // VAO(板ポリゴン)
    private val vaoBoard = ES32VAOIpnt()

    // シェーダA
    private val shaderA = W84ShaderA(ctx)
    // シェーダB
    private val shaderB = ES32a03ShaderB(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    init {
        // フレームバッファ
        frameBuf = IntBuffer.allocate(1)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(1)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(4)
    }

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,20f,0f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,0f,-1f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,75f,ratio,5f,35f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // フレームバッファのバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(1f,1f,1f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        GLES32.glViewport(0,0,renderW,renderH)

        // トーラス
        (0..8).forEach { i ->
            val ii = i.toFloat()
            val ambient = MgColor.hsva(i*40,1f,1f,1f)
            Matrix.setIdentityM(matM,0)
            Matrix.rotateM(matM,0,ii*40f,0f,1f,0f)
            Matrix.translateM(matM,0,0f,0f,10f)
            Matrix.rotateM(matM,0,t0,1f,1f,0f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            Matrix.invertM(matI,0,matM,0)
            shaderA.draw(vaoTorus,matMVP,matI,vecLight,ambient.toFloatArray())
        }

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // デフォルトバッファを初期化
        GLES32.glClearColor(1f,1f,1f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 板ポリゴンをレンダリング
        GLES32.glViewport(0,0,renderW/2,renderH/2)
        shaderB.draw(vaoBoard,0)
        GLES32.glViewport(0,renderH/2,renderW/2,renderH/2)
        shaderB.draw(vaoBoard,1)
        GLES32.glViewport(renderW/2,0,renderW/2,renderH/2)
        shaderB.draw(vaoBoard,2)
        GLES32.glViewport(renderW/2,renderH/2,renderW/2,renderH/2)
        shaderB.draw(vaoBoard,3)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        // フレームバッファ生成
        GLES32.glGenFramebuffers(1,frameBuf)
        // レンダ―バッファ生成
        GLES32.glGenRenderbuffers(1,depthRenderBuf)
        // フレームバッファを格納するテクスチャ生成
        GLES32.glGenTextures(4,frameTex)
        MyGLES32Func.createFrameBuffer4MRT(renderW,renderH,4,0,frameBuf,depthRenderBuf,frameTex)

        // カラーアタッチメントのバッファ登録
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])
        val bufLst = intArrayOf(
            GLES32.GL_COLOR_ATTACHMENT0,
            GLES32.GL_COLOR_ATTACHMENT1,
            GLES32.GL_COLOR_ATTACHMENT2,
            GLES32.GL_COLOR_ATTACHMENT3
        )
        GLES32.glDrawBuffers(4,bufLst,0)

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[1])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE2)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[2])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE3)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[3])
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダA
        shaderA.loadShader()

        // シェーダB
        shaderB.loadShader()

        // 描画オブジェクト(トーラス)
        modelTorus.createPath(mapOf(
            "row"     to 32f,
            "column"  to 32f,
            "iradius" to 1f,
            "oradius" to 2f
        ))

        // モデル生成(板ポリゴン)
        modelBoard.createPath(mapOf(
                "pattern" to 100f
        ))

        // VAO(トーラス)
        vaoTorus.makeVIBO(modelTorus)

        // VAO(板ポリゴン)
        vaoBoard.makeVIBO(modelBoard)

        // ライトの向き
        vecLight[0] = -0.577f
        vecLight[1] =  0.577f
        vecLight[2] =  0.577f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorus.deleteVIBO()
        vaoBoard.deleteVIBO()
        shaderA.deleteShader()
        shaderB.deleteShader()

        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}
