package milu.kiriu2010.milugles32.w5x.w57

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.color.MgColor
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpt
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w5x.w53.W53ShaderScreen
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.exp

// -------------------------------------------
// gaussianフィルタ
// -------------------------------------------
// https://wgld.org/d/webgl/w057.html
// -------------------------------------------
class W57Renderer(ctx: Context): MgRenderer(ctx) {

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
    // シェーダ(gaussianフィルタ)
    private val shaderGaussian = W57ShaderGaussian(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    // gaussianフィルタを使うかどうか
    var u_gaussian = 1
    // 描画対象のテクスチャ
    var textureType = 0

    // ガウス関数に与えるパラメータ
    var k_gaussian = 5f
    // u_gaussianフィルタの重み係数
    val u_weight = FloatArray(10)

    // 色相用カウンタ
    var cntColor = 0

    init {
        // テクスチャ
        textures = IntArray(2)
        // フレームバッファ
        frameBuf = IntBuffer.allocate(2)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(2)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(2)

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w55_01)
        val bmp1 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w55_02)
        bmpArray.add(bmp0)
        bmpArray.add(bmp1)
    }

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()
        if ( (angle[0]%2) == 0 ) {
            cntColor++
        }

        // フレームバッファのバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        var hsv = MgColor.hsva(cntColor%360,1f,1f,1f)
        GLES32.glClearColor(hsv[0], hsv[1], hsv[2], hsv[3])
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,20f,0f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,0f,-1f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,90f,ratio,0.1f,100f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // -------------------------------------------------------
        // トーラス描画(10個)
        // -------------------------------------------------------
        (0..9).forEach { i ->
            val amb = MgColor.hsva(i*40,1f,1f,1f)
            Matrix.setIdentityM(matM,0)
            Matrix.rotateM(matM,0,i.toFloat()*360f/9f,0f,1f,0f)
            Matrix.translateM(matM,0,0f,0f,10f)
            Matrix.rotateM(matM,0,t0,1f,1f,0f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            Matrix.invertM(matI,0,matM,0)
            shaderScreen.draw(vaoTorus,matMVP,matI,vecLight,vecEye,amb.toFloatArray())
        }

        // 正射影用の座標変換行列
        Matrix.setLookAtM(matV,0,
                0f,0f,0.5f,
                0f,0f,0f,
                0f,1f,0f)
        Matrix.orthoM(matP,0,-1f,1f,-1f,1f,0.1f,1f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        when (textureType) {
            // フレームバッファをテクスチャとしてバインド
            0 -> GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])
            // 画像１
            1 -> GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])
            // 画像２
            2 -> GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[1])
        }

        // gaussianフィルタの重み係数を算出
        // t:重み係数を正規化するために用いる
        var t = 0f
        // dが大きくなれば大きくなるほど
        // ガウス関数によって生成される値は扁平型の釣り鐘になる
        // dとブラーのかかる強さは比例する
        // dがおおきくなればなるほど大きなぼかし処理がかかる
        var d = k_gaussian*k_gaussian
        (0..9).forEach { i ->
            var r = 1f+2f*i.toFloat()
            var w = exp(-0.5f*(r*r)/d)
            u_weight[i] = w
            if ( i > 0 )  w *= 2f
            t += w
        }
        (0..9).forEach { i ->
            u_weight[i] /= t
        }

        when (u_gaussian) {
            // gaussianフィルタをかける
            1 -> {
                // フレームバッファのバインドを変更
                GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[1])
                // レンダリング(横方向ブラー)
                renderBoard(1,1)
                // テクスチャを変更
                GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
                GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[1])
                // フレームバッファのバインドを解除
                GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)
                // レンダリング(縦方向ブラー)
                renderBoard(1,0)
            }
            else -> {
                // フレームバッファのバインドを解除
                GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)
                // レンダリング(ブラーなし)
                renderBoard(0,0)
            }
        }

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
        shaderGaussian.draw(vaoBoard,matVP,0,g,u_weight,h,renderW.toFloat())
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        // テクスチャを作成
        GLES32.glGenTextures(2,textures,0)
        // テクスチャに使う画像をロード
        MyGLES32Func.createTexture(0,textures,bmpArray[0],renderW)
        MyGLES32Func.createTexture(1,textures,bmpArray[1],renderW)

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
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // モデルをレンダリングするシェーダ
        shaderScreen.loadShader()

        // gaussianフィルタ用シェーダ
        shaderGaussian.loadShader()

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

        GLES32.glDeleteTextures(textures.size,textures,0)
        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}