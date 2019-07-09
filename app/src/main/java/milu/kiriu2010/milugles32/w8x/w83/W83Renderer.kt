package milu.kiriu2010.milugles32.w8x.w83

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.color.MgColor
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIp
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.floor

// -------------------------------------------
// GPGPUで描画
// -------------------------------------------
// https://wgld.org/d/webgl/w083.html
// -------------------------------------------
class W83Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(頂点)
    private val modelVtx = W83ModelVertices()
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = W83ModelBoard()

    // VAO(頂点)
    private val vaoVtx = W83VAOi()
    // VAO(板ポリゴン)
    private val vaoBoard = ES32VAOIp()

    // シェーダ()
    private val shaderP = W83ShaderPoint(ctx)
    // シェーダ()
    private val shaderV = W83ShaderVelocity(ctx)
    // シェーダ()
    private val shaderD = W83ShaderDefault(ctx)

    // タッチ位置(-1～1)
    private val m = floatArrayOf(0f,0f)

    // 速度
    private var u_velocity = 0f

    private data class FrameBuf(
        var frameBuf: Int,
        var frameTex: Int
    )

    private lateinit var fbFront: FrameBuf
    private lateinit var fbBack: FrameBuf

    init {
        // フレームバッファ
        frameBuf = IntBuffer.allocate(2)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(2)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(2)
    }

    override fun onDrawFrame(gl: GL10?) {
        angle[0] = (angle[0]+1)%360

        m[0] = (2f*touchP.x - renderW.toFloat())/renderW.toFloat()
        m[1] = -(2f*touchP.y - renderH.toFloat())/renderH.toFloat()

        // フレームバッファをバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,fbFront.frameBuf)

        // ビューポートを設定
        GLES32.glViewport(0,0,renderW,renderH)

        // フレームバッファを初期化
        GLES32.glClearColor(0f, 0f, 0f, 0f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)

        // テクスチャとしてバックバッファをバインド
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,fbBack.frameTex)

        val mouseFlg = if (isRunning){
            u_velocity = 1f
            1
        } else {
            u_velocity *= 0.95f
            0
        }
        // テクスチャへ頂点情報をレンダリング
        shaderV.draw(vaoBoard,
            floatArrayOf(renderW.toFloat(),renderH.toFloat()),
            0,m,mouseFlg,u_velocity)

        // -----------------------------------------------
        // スクリーンレンダリング
        // -----------------------------------------------

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // パーティクルの色
        val ambient = MgColor.hsva(angle[0],1f,0.8f,1f)

        // ブレンドを有効化
        GLES32.glEnable(GLES32.GL_BLEND)

        // フレームバッファをテクスチャとしてバインド
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,fbFront.frameTex)

        // ビューポートを設定
        GLES32.glViewport(0,0,renderW,renderH)

        // canvasを初期化
        GLES32.glClearColor(0f, 0f, 0f, 1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)

        // 頂点を描画
        shaderP.draw(vaoVtx,
            floatArrayOf(renderW.toFloat(),renderH.toFloat()),
            0,u_velocity,ambient.toFloatArray())

        // フレームバッファをフリップ
        var flip = fbBack
        fbBack = fbFront
        fbFront = flip

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        renderW = width
        renderH = height

        // フレームバッファ生成
        GLES32.glGenFramebuffers(2,frameBuf)
        // レンダ―バッファ生成
        GLES32.glGenRenderbuffers(2,depthRenderBuf)
        // フレームバッファを格納するテクスチャ生成
        GLES32.glGenTextures(2,frameTex)
        MyGLES32Func.createFrameBuffer(renderW,renderH,0,frameBuf,depthRenderBuf,frameTex,GLES32.GL_FLOAT)
        MyGLES32Func.createFrameBuffer(renderW,renderH,1,frameBuf,depthRenderBuf,frameTex,GLES32.GL_FLOAT)
        fbBack = FrameBuf(frameBuf[0], frameTex[0])
        fbFront = FrameBuf(frameBuf[1], frameTex[1])

        // ------------------------------------------
        // バックバッファにデフォルトの頂点情報を描く
        // ------------------------------------------
        modelVtx.createPath(mapOf(
            "w" to renderW.toFloat(),
            "h" to renderH.toFloat()
        ))
        vaoVtx.makeVIBO(modelVtx)

        // フレームバッファをバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // ビューポートを設定
        GLES32.glViewport(0,0,renderW,renderH)

        // フレームバッファを初期化
        GLES32.glClearColor(0f, 0f, 0f, 0f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)

        // テクスチャへ頂点情報をレンダリング
        shaderD.draw(vaoBoard, floatArrayOf(renderW.toFloat(),renderH.toFloat()))
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES32.glDisable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_ONE,GLES32.GL_ONE)

        // シェーダ()
        shaderP.loadShader()
        // シェーダ()
        shaderV.loadShader()
        // シェーダ()
        shaderD.loadShader()

        // モデル生成(板ポリゴン)
        modelBoard.createPath()

        // VAO(板ポリゴン)
        vaoBoard.makeVIBO(modelBoard)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoBoard.deleteVIBO()
        vaoVtx.deleteVIBO()
        shaderP.deleteShader()
        shaderD.deleteShader()
        shaderV.deleteShader()

        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}