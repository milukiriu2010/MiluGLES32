package milu.kiriu2010.milugles32.w8x.w85

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.color.MgColor
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.model.d3.Cube01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpt
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------------------------
// MRT+多重エッジ
// -----------------------------------------------------------
// 複数のレンダリングターゲットに対して同時に異なる出力を行う
// -----------------------------------------------------------
// https://wgld.org/d/webgl/w085.html
// -----------------------------------------------------------
class W85Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(立方体)
    private val modelCube = Cube01Model()
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = Board00Model()

    // VAO(立方体)
    private val vaoCube = ES32VAOIpnc()
    // VAO(板ポリゴン)
    private val vaoBoard = ES32VAOIpt()

    // シェーダ(MRT)
    private val shaderMRT = W85ShaderMRT(ctx)
    // シェーダ(エッジ)
    private val shaderEdge = W85ShaderEdge(ctx)
    // シェーダ(プレビュー)
    private val shaderPrev = W85ShaderPrev(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // 表示する立方体の数
    val CUBE_COUNT = 100
    // 各立方体のオフセット座標と拡大率
    val u_cubeOffset = FloatArray(CUBE_COUNT*3)
    val u_cubeScale = FloatArray(CUBE_COUNT)

    // 板ポリゴンのオフセット移動量
    val u_boardOffset = floatArrayOf(
        -0.75f, -0.75f, 0f,
        -0.25f, -0.75f, 0f,
         0.25f, -0.75f, 0f
    )

    // カーネル参照のためのオフセット座標
    val u_offsetCoord = floatArrayOf(
        -1f, -1f,
        -1f,  0f,
        -1f,  1f,
         0f, -1f,
         0f,  0f,
         0f,  1f,
         1f, -1f,
         1f,  0f,
         1f,  1f
    )

    // ラプラシアンカーネル
    val u_weight = floatArrayOf(
        -1f, -1f, -1f,
        -1f,  8f, -1f,
        -1f, -1f, -1f
    )

    init {
        // 各立方体のオフセット座標と拡大率をランダムに決め格納
        (0 until CUBE_COUNT).forEach { i->
            u_cubeOffset[i*3+0] = (-10..10).shuffled().first().toFloat()
            u_cubeOffset[i*3+1] = (-10..10).shuffled().first().toFloat()
            u_cubeOffset[i*3+2] = (-10..10).shuffled().first().toFloat()
            u_cubeScale[i] = Math.random().toFloat() + 0.5f
        }


        // フレームバッファ
        frameBuf = IntBuffer.allocate(1)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(1)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(3)
    }

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,25f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,60f,ratio,3f,50f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // フレームバッファのバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(1f,1f,1f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        GLES32.glViewport(0,0,renderW,renderH)

        // 立方体をレンダリング
        (0 until CUBE_COUNT).forEach { i ->
            val ambient = MgColor.hsva(i*(360/CUBE_COUNT),1f,1f,1f)
            Matrix.setIdentityM(matM,0)
            Matrix.translateM(matM,0,u_cubeOffset[3*i],u_cubeOffset[3*i+1],u_cubeOffset[3*i+2])
            Matrix.scaleM(matM,0,u_cubeScale[i],u_cubeScale[i],u_cubeScale[i])
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            shaderMRT.draw(vaoCube,matMVP,ambient.toFloatArray())
        }

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // デフォルトバッファを初期化
        GLES32.glClearColor(1f,1f,1f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // エッジ検出シェーダで
        // 板ポリゴンをレンダリング
        shaderEdge.draw(vaoBoard,
            floatArrayOf(renderW.toFloat(),renderH.toFloat()),
            u_offsetCoord,u_weight,0,1,2)

        // プレビューシェーダで
        // 板ポリゴンをレンダリング
        (0..2).forEach { i ->
            shaderPrev.draw(vaoBoard,
                floatArrayOf(u_boardOffset[3*i],u_boardOffset[3*i+1],u_boardOffset[3*i+2]),
                i
            )
        }

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
        GLES32.glGenTextures(3,frameTex)
        MyGLES32Func.createFrameBuffer4MRT(renderW,renderH,3,0,frameBuf,depthRenderBuf,frameTex)

        // カラーアタッチメントのバッファ登録
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])
        val bufLst = intArrayOf(
            GLES32.GL_COLOR_ATTACHMENT0,
            GLES32.GL_COLOR_ATTACHMENT1,
            GLES32.GL_COLOR_ATTACHMENT2
        )
        GLES32.glDrawBuffers(3,bufLst,0)

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[1])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE2)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[2])
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダ(MRT)
        shaderMRT.loadShader()
        // シェーダ(エッジ)
        shaderEdge.loadShader()
        // シェーダ(プレビュー)
        shaderPrev.loadShader()

        // 描画オブジェクト(立方体)
        modelCube.createPath(mapOf(
            "colorR"  to 1f,
            "colorG"  to 1f,
            "colorB"  to 1f,
            "colorA"  to 1f
        ))

        // モデル生成(板ポリゴン)
        modelBoard.createPath(mapOf(
                "pattern" to 100f
        ))

        // VAO(トーラス)
        vaoCube.makeVIBO(modelCube)

        // VAO(板ポリゴン)
        vaoBoard.makeVIBO(modelBoard)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoCube.deleteVIBO()
        vaoBoard.deleteVIBO()
        shaderMRT.deleteShader()
        shaderEdge.deleteShader()
        shaderPrev.deleteShader()

        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}
