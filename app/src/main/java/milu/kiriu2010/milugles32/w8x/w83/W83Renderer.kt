package milu.kiriu2010.milugles32.w8x.w83

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIp
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import milu.kiriu2010.milugles32.w6x.w69.W69ShaderDepth
import milu.kiriu2010.milugles32.w6x.w69.W69ShaderScreen
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
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = Board00Model()

    // VAO(トーラス＋深度)
    private val vaoTorusDepth = ES32VAOIp()
    // VAO(板ポリゴン＋深度)
    private val vaoBoardDepth = ES32VAOIp()
    // VAO(トーラス＋スクリーン)
    private val vaoTorusScreen = ES32VAOIpnc()
    // VAO(板ポリゴン＋スクリーン)
    private val vaoBoardScreen = ES32VAOIpnc()

    // シェーダ(深度値格納用)
    private val shaderDepth = W69ShaderDepth(ctx)
    // シェーダ(スクリーンレンダリング用)
    private val shaderScreen = W69ShaderScreen(ctx)

    // ライトビューの上方向
    val vecLightUp = floatArrayOf(0f,0f,-1f)

    // 画面縦横比
    var ratio: Float = 0f

    init {
        // フレームバッファ
        frameBuf = IntBuffer.allocate(1)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(1)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(1)
    }

    override fun onDrawFrame(gl: GL10?) {
        // 回転角度
        angle[0] =(angle[0]+1)%360

        // フレームバッファをバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(0f, 0f, 0f, 0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)

        // -----------------------------------------------
        // スクリーンレンダリング
        // -----------------------------------------------

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // フレームバッファをテクスチャとしてバインド
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])

        // canvasを初期化
        GLES32.glClearColor(0.0f, 0.7f, 0.7f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

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
        GLES32.glGenTextures(1,frameTex)
        MyGLES32Func.createFrameBuffer(renderW,renderH,0,frameBuf,depthRenderBuf,frameTex,GLES32.GL_FLOAT)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES32.glDisable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_ONE,GLES32.GL_ONE)

        // シェーダ(深度値格納用)
        shaderDepth.loadShader()
        // シェーダ(スクリーンレンダリング用)
        shaderScreen.loadShader()

        // モデル生成(板ポリゴン)
        modelBoard.createPath(mapOf(
                "pattern" to 51f,
                "colorR"  to 1f,
                "colorG"  to 1f,
                "colorB"  to 1f,
                "colorA"  to 1f
        ))

        // VBO(板ポリゴン＋深度)
        vaoBoardDepth.makeVIBO(modelBoard)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorusDepth.deleteVIBO()
        vaoBoardDepth.deleteVIBO()
        vaoTorusScreen.deleteVIBO()
        vaoBoardScreen.deleteVIBO()
        shaderDepth.deleteShader()
        shaderScreen.deleteShader()

        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}