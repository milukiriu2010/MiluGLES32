package milu.kiriu2010.milugles32.w5x.w58

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.color.MgColor
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.model.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpt
import milu.kiriu2010.math.MyMathUtil
import milu.kiriu2010.milugles32.w5x.w53.W53ShaderScreen
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// --------------------------------------------
// グレアフィルタ
// --------------------------------------------
// まぶしい光や反射光などがあふれて見える現象
// 別名ライトブルーム
// --------------------------------------------
// https://wgld.org/d/webgl/w058.html
// --------------------------------------------
class W58Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = Board00Model()

    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpnc()
    // VBO(板ポリゴン)
    private val vaoBoard = ES32VAOIpt()

    // シェーダ(モデルをレンダリング)
    private val shaderScreen = W53ShaderScreen(ctx)
    // シェーダ(反射光)
    private val shaderSpecular = W58ShaderSpecular(ctx)
    // シェーダ(正射影で板ポリゴンをgaussianフィルタでレンダリング)
    private val shaderGaussian = W58ShaderGaussian(ctx)
    // シェーダ(正射影でレンダリング結果を合成する)
    private val shaderFinal = W58ShaderFinal(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    // ガウス関数に与えるパラメータ
    var k_gaussian = 5f
    // u_gaussianフィルタの重み係数
    lateinit var u_weight: FloatArray

    // グレアフィルタをかけるかどうか
    var u_glare = 0

    // プロジェクションxビュー(正射影用の座標変換行列)
    private val matVP4O = FloatArray(16)

    init {
        // フレームバッファ
        //   0: 反射光+ぼかしのレンダリング
        //   1: 通常のレンダリング
        frameBuf = IntBuffer.allocate(2)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(2)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(2)
    }

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360

        // 各トーラスの傾き
        val angleF = FloatArray(10)
        (0..9).forEach { i ->
            angleF[i] = ((angle[0]+40*i)%360).toFloat()
        }

        // ビュー×プロジェクション座標変換行列(パースあり)
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,20f,0f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,0f,-1f))
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

        // gaussianフィルタの重み係数を算出
        u_weight = MyMathUtil.gaussianWeigt(10,k_gaussian,1f)

        // -----------------------------------------------
        // 【0:スペキュラ成分のみをレンダリング】
        // -----------------------------------------------

        // フレームバッファのバインド(0:スペキュラ成分)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(0f, 0f, 0f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // -------------------------------------------------------
        // スペキュラ成分のみをレンダリング(トーラス)(10個)
        // -------------------------------------------------------
        (0..9).forEach { i ->
            Matrix.setIdentityM(matM,0)
            Matrix.rotateM(matM,0,i.toFloat()*360f/9f,0f,1f,0f)
            Matrix.translateM(matM,0,0f,0f,10f)
            Matrix.rotateM(matM,0,angleF[i],1f,1f,0f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            Matrix.invertM(matI,0,matM,0)
            shaderSpecular.draw(vaoTorus,matMVP,matI,vecLight,vecEye)
        }

        // ---------------------------------------------------------------------------------
        // 【1:スペキュラ成分のみでレンダリングしたものに横方向のgaussianフィルタをかける】
        // ---------------------------------------------------------------------------------
        // フレームバッファのバインド(1:gaussianフィルタ)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[1])

        // フレームバッファを初期化
        GLES32.glClearColor(0f, 0f, 0f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // テクスチャのバインド(0:スペキュラ成分のみ)
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])

        // 板ポリゴンのレンダリング(横方向ブラー)
        renderBoard(1,1)

        // ----------------------------------------------------------
        // 【2:"スペキュラ成分＋横方向ブラー"に縦方向ブラーをかける】
        // ----------------------------------------------------------

        // フレームバッファのバインド(0:スペキュラ成分のみ)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // テクスチャのバインド(1:スペキュラ成分+横方向ブラー)
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[1])

        // 板ポリゴンのレンダリング(縦方向のブラー)
        renderBoard(1,0)

        // --------------------------------------------------------------------------
        // 【3:通常のレンダリング】
        // --------------------------------------------------------------------------

        // フレームバッファのバインド()
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[1])

        // フレームバッファを初期化
        GLES32.glClearColor(0f, 0f, 0f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // -------------------------------------------------------
        // 通常のレンダリング(トーラス)(10個)
        // -------------------------------------------------------
        (0..9).forEach { i ->
            val amb = MgColor.hsva(i*40,1f,1f,1f)
            Matrix.setIdentityM(matM,0)
            Matrix.rotateM(matM,0,i.toFloat()*360f/9f,0f,1f,0f)
            Matrix.translateM(matM,0,0f,0f,10f)
            Matrix.rotateM(matM,0,angleF[i],1f,1f,0f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            Matrix.invertM(matI,0,matM,0)
            shaderScreen.draw(vaoTorus,matMVP,matI,vecLight,vecEye,amb.toFloatArray())
        }

        // -----------------------------------------------
        // 【4:合成結果をレンダリング】
        // -----------------------------------------------

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // テクスチャ0のバインド
        // フルカラートーラスのレンダリング結果
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[1])

        // テクスチャ1のバインド
        // スペキュラ成分のブラー
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])

        // canvasを初期化
        GLES32.glClearColor(0f, 0f, 0f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 板ポリゴンのレンダリング
        shaderFinal.draw(vaoBoard,matVP4O,0,1,u_glare)
    }

    // 板ポリゴンを描画
    //   g:0 => gaussianフィルタ使わない
    //     1 => gaussianフィルタ使う
    //   h:0 => 縦方向
    //     1 => 横方向
    private fun renderBoard(g: Int, h:Int ) {
        // canvasを初期化
        GLES32.glClearColor(0f, 0f, 0f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 板ポリゴンの描画
        shaderGaussian.draw(vaoBoard,matVP4O,0,g,u_weight,h,renderW.toFloat())
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        // フレームバッファ生成
        GLES32.glGenFramebuffers(2,frameBuf)
        // 深度バッファ用レンダ―バッファ生成
        GLES32.glGenRenderbuffers(2,depthRenderBuf)
        // フレームバッファ用テクスチャ生成
        GLES32.glGenTextures(2,frameTex)
        MyGLES32Func.createFrameBuffer(renderW,renderH,0,frameBuf,depthRenderBuf,frameTex)
        MyGLES32Func.createFrameBuffer(renderW,renderH,1,frameBuf,depthRenderBuf,frameTex)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // canvasを初期化する色を設定する
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // canvasを初期化する際の深度を設定する
        GLES32.glClearDepthf(1f)

        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダ(シーン)
        shaderScreen.loadShader()

        // シェーダ(反射光)
        shaderSpecular.loadShader()

        // シェーダ(正射影で板ポリゴンをgaussianフィルタでレンダリング)
        shaderGaussian.loadShader()

        // シェーダ(正射影でレンダリング結果を合成する)
        shaderFinal.loadShader()

        // モデル生成(トーラス)
        modelTorus.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 1f,
                "oradius" to 2f,
                "colorR"  to 1f,
                "colorG"  to 1f,
                "colorB"  to 1f,
                "colorA"  to 1f
        ))

        // モデル生成(板ポリゴン)
        modelBoard.createPath(mapOf(
                "pattern" to 53f
        ))

        // VAO(トーラス)
        vaoTorus.makeVIBO(modelTorus)

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
        vaoTorus.deleteVIBO()
        vaoBoard.deleteVIBO()
        shaderScreen.deleteShader()
        shaderGaussian.deleteShader()
        shaderSpecular.deleteShader()
        shaderFinal.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}