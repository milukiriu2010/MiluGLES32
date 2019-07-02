package milu.kiriu2010.milugles32.w5x.w59

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.color.MgColor
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.model.d3.Cube01Model
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIp
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnct
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpt
import milu.kiriu2010.math.MyMathUtil
import milu.kiriu2010.milugles32.R
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------------------------------------
// emuglGLESv2_enc: a vertex attribute index out of boundary is detected. Skipping corresponding vertex attribute. buf=0xe7b8f050
// -----------------------------------------------------------------------
// 被写界深度
// -----------------------------------------------------------------------
//   ピントが合っていない部分がぼやけて写るようにすること
//   被写界深度ではピントを合わせたい深度を決め、
//   その深度に応じて、ぼけていないシーンとぼけたシーンとを合成する
// -----------------------------------------------------------------------
// https://wgld.org/d/webgl/w059.html
// -----------------------------------------------------------------------
class W59Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = Board00Model()
    // 描画オブジェクト(立方体)
    private val modelCube = Cube01Model()

    // VAO(トーラス＋メイン)
    private val vaoTorusMain = ES32VAOIpnct()
    // VAO(立方体＋メイン)
    private val vaoCubeMain = ES32VAOIpnct()
    // VAO(トーラス＋深度)
    private val vaoTorusDepth = ES32VAOIp()
    // VAO(立方体＋深度)
    private val vaoCubeDepth = ES32VAOIp()
    // VAO(板ポリゴン)
    private val vaoBoard = ES32VAOIpt()

    // シェーダ(メイン)
    private val shaderMain = W59ShaderMain(ctx)
    // シェーダ(深度をレンダリング)
    private val shaderDepth = W59ShaderDepth(ctx)
    // シェーダ(正射影で板ポリゴンをgaussianフィルタでレンダリング)
    private val shaderGaussian = W59ShaderGaussian(ctx)
    // シェーダ(正射影でレンダリング結果を合成する)
    private val shaderFinal = W59ShaderFinal(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    // u_gaussianフィルタの重み係数
    val u_weight1 = MyMathUtil.gaussianWeigt(10,15f)
    val u_weight2 = MyMathUtil.gaussianWeigt(10,45f)

    // フォーカスする深度値
    // -0.425～0.425
    var u_depthOffset = 0f

    // 選択値
    var u_result = 0

    // プロジェクションxビュー(正射影用の座標変換行列)
    private val matVP4O = FloatArray(16)

    init {
        // テクスチャ
        textures = IntArray(1)
        // フレームバッファ
        // 0:depth:深度マップレンダリング用のバッファ
        // 1:scene:ぼやけてないシーン
        // 2:blur1:小さくぼやけたシーン
        // 3:blur2:大きくぼやけたシーン
        // 4:ぼやけたシーンの一時バッファ
        frameBuf = IntBuffer.allocate(5)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(5)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(5)

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w59)
        bmpArray.add(bmp0)
    }

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360

        // 各トーラスの傾き
        val angleF = FloatArray(10)
        (0..9).forEach { i ->
            angleF[i] = ((angle[0]+40*i)%360).toFloat()
        }

        // ビュー×プロジェクション座標変換行列(パースあり)
        vecEye = qtnNow.toVecIII(floatArrayOf(2f,0f,10f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,90f,ratio,0.1f,30f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // 正射影用の座標変換行列
        Matrix.setLookAtM(matV,0,
                0f,0f,0.5f,
                0f,0f,0f,
                0f,1f,0f)
        Matrix.orthoM(matP,0,-1f,1f,-1f,1f,0.1f,1f)
        Matrix.multiplyMM(matVP4O,0,matP,0,matV,0)

        // -----------------------------------------------
        // 【0:ぼやけていないシーンのレンダリング】
        // -----------------------------------------------

        // フレームバッファのバインド(1:Scene)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[1])

        // フレームバッファを初期化
        GLES32.glClearColor(0f, 0f, 1f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // テクスチャのバインド
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])

        // ぼやけていないシーンをレンダリング(立方体)
        GLES32.glCullFace(GLES32.GL_FRONT)
        Matrix.setIdentityM(matM,0)
        Matrix.scaleM(matM,0,3.5f,2.5f,10f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        shaderMain.draw(vaoCubeMain,matMVP,matI,vecLight,
                floatArrayOf(0f,0f,10f), floatArrayOf(1f,1f,1f,1f), 0)

        // -------------------------------------------------------
        // ぼやけていないシーンをレンダリング(トーラス)(10個)
        // -------------------------------------------------------
        GLES32.glCullFace(GLES32.GL_BACK)
        (0..9).forEach { i ->
            val amb = MgColor.hsva(i*40,1f,1f,1f)
            Matrix.setIdentityM(matM,0)
            Matrix.translateM(matM,0,0.2f*i.toFloat(),0f,8.8f-2.2f*i.toFloat())
            Matrix.rotateM(matM,0,angleF[i],1f,1f,0f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            Matrix.invertM(matI,0,matM,0)
            shaderMain.draw(vaoTorusMain,matMVP,matI,vecLight,vecEye,amb.toFloatArray(),0)
        }

        // --------------------------------------------------------------------------
        // 【1:ぼやけていないシーンから小さくぼやけたシーン生成し一時バッファに格納】
        // --------------------------------------------------------------------------
        // フレームバッファのバインド(4:テンポラリ)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[4])

        // フレームバッファを初期化
        GLES32.glClearColor(0f, 0f, 1f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // テクスチャのバインド(Scene)
        // ボケていないシーンをテクスチャとしてバインド
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[1])

        // 板ポリゴンのレンダリング
        shaderGaussian.draw(vaoBoard,matVP4O,0,1,u_weight1,1,renderW.toFloat())

        // -----------------------------------------------
        // 【2:小さくぼやけたシーンをバッファに描く】
        // -----------------------------------------------

        // フレームバッファのバインド(2:Blur1)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[2])

        // フレームバッファを初期化
        GLES32.glClearColor(0f, 0f, 1f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // テクスチャのバインド(4:テンポラリ)
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[4])

        // 板ポリゴンのレンダリング
        shaderGaussian.draw(vaoBoard,matVP4O,0,1,u_weight1,0,renderW.toFloat())

        // --------------------------------------------------------------------------
        // 【3:ぼやけていないシーンから大きくぼやけたシーン生成し一時バッファに格納】
        // --------------------------------------------------------------------------

        // フレームバッファのバインド(テンポラリ)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[4])

        // フレームバッファを初期化
        GLES32.glClearColor(0f, 0f, 1f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // テクスチャのバインド(1:Scene)
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[1])

        // 板ポリゴンのレンダリング
        shaderGaussian.draw(vaoBoard,matVP4O,0,1,u_weight2,1,renderW.toFloat())

        // -----------------------------------------------
        // 【4:大きくぼやけたシーンをバッファに描く】
        // -----------------------------------------------

        // フレームバッファのバインド(3:Blur2)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[3])

        // フレームバッファを初期化
        GLES32.glClearColor(0f, 0f, 1f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // テクスチャのバインド(4:テンポラリ)
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[4])

        // 板ポリゴンのレンダリング
        shaderGaussian.draw(vaoBoard,matVP4O,0,1,u_weight2,0,renderW.toFloat())

        // -----------------------------------------------
        // 【5:深度値マップをレンダリング】
        // -----------------------------------------------

        // フレームバッファのバインド(0:Depth)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(1f, 1f, 1f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 深度をレンダリング(立方体)
        GLES32.glCullFace(GLES32.GL_FRONT)
        Matrix.setIdentityM(matM,0)
        Matrix.scaleM(matM,0,3.5f,2.5f,10f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderDepth.draw(vaoCubeDepth,matMVP,u_depthOffset)

        // -------------------------------------------------------
        // 深度をレンダリング(トーラス)(10個)
        // -------------------------------------------------------
        GLES32.glCullFace(GLES32.GL_BACK)
        (0..9).forEach { i ->
            val amb = MgColor.hsva(i*40,1f,1f,1f)
            Matrix.setIdentityM(matM,0)
            Matrix.translateM(matM,0,0.2f*i.toFloat(),0f,8.8f-2.2f*i.toFloat())
            Matrix.rotateM(matM,0,angleF[i],1f,1f,0f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            shaderDepth.draw(vaoTorusDepth,matMVP,u_depthOffset)
        }

        // -----------------------------------------------
        // 【6:合成結果をレンダリング】
        // -----------------------------------------------

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // テクスチャのバインド
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[1])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE2)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[2])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE3)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[3])

        // canvasを初期化
        GLES32.glClearColor(0f, 0f, 1f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 板ポリゴンのレンダリング
        shaderFinal.draw(vaoBoard,matVP4O,0,1,2,3,u_result)
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

        // フレームバッファ生成
        GLES32.glGenFramebuffers(5,frameBuf)
        // レンダ―バッファ生成
        GLES32.glGenRenderbuffers(5,depthRenderBuf)
        // フレームバッファを格納するテクスチャ生成
        GLES32.glGenTextures(5,frameTex)
        (0..4).forEach {
            MyGLES32Func.createFrameBuffer(renderW,renderH,it,frameBuf,depthRenderBuf,frameTex)
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダ(メイン)
        shaderMain.loadShader()

        // シェーダ(深度をレンダリング)
        shaderDepth.loadShader()

        // シェーダ(正射影で板ポリゴンをgaussianフィルタでレンダリング)
        shaderGaussian.loadShader()

        // シェーダ(正射影でレンダリング結果を合成する)
        shaderFinal.loadShader()

        // モデル生成(トーラス)
        modelTorus.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 0.3f,
                "oradius" to 0.7f,
                "colorR"  to 1f,
                "colorG"  to 1f,
                "colorB"  to 1f,
                "colorA"  to 1f
        ))

        // モデル生成(板ポリゴン)
        modelBoard.createPath(mapOf(
                "pattern" to 53f
        ))

        // 描画オブジェクト(立方体)
        modelCube.createPath(mapOf(
                "pattern" to 2f,
                "scale"   to 2f,
                "colorR"  to 1f,
                "colorG"  to 1f,
                "colorB"  to 1f,
                "colorA"  to 1f
        ))

        // VAO(トーラス＋メイン)
        vaoTorusMain.makeVIBO(modelTorus)

        // VAO(立方体＋メイン)
        vaoCubeMain.makeVIBO(modelCube)

        // VAO(トーラス＋深度)
        vaoTorusDepth.makeVIBO(modelTorus)

        // VAO(立方体＋深度)
        vaoCubeDepth.makeVIBO(modelCube)

        // VAO(板ポリゴン)
        vaoBoard.makeVIBO(modelBoard)

        // 光源位置
        vecLight[0] = -0.577f
        vecLight[1] =  0.577f
        vecLight[2] =  0.577f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorusMain.deleteVIBO()
        vaoCubeMain.deleteVIBO()
        vaoTorusDepth.deleteVIBO()
        vaoCubeDepth.deleteVIBO()
        vaoBoard.deleteVIBO()

        shaderMain.deleteShader()
        shaderDepth.deleteShader()
        shaderGaussian.deleteShader()
        shaderFinal.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}