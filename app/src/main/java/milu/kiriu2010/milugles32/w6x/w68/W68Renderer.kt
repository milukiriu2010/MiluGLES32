package milu.kiriu2010.milugles32.w6x.w68

import android.content.Context
import android.graphics.Bitmap
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
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------
// emuglGLESv2_enc: device/generic/goldfish-opengl/system/GLESv2_enc/GL2Encoder.cpp:s_glVertexAttribPointer:599 GL error 0x501
//    Info: Invalid vertex attribute index. Wanted index: 4294967295. Max index: 16
// WV068ShaderZoomBlur:a_TextureCoord:Board00Model:1281
// -----------------------------------------
// ゴッドレイフィルタ
// -----------------------------------------
// // https://wgld.org/d/webgl/w068.html
// -----------------------------------------
class W68Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = Board00Model()

    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpnc()
    // VAO(板ポリゴン)
    private val vaoBoard = ES32VAOIpt()

    // シェーダ(メイン)
    private val shaderScreen = W68ShaderScreen(ctx)
    // シェーダ(ズームブラー)
    private val shaderZoomBlur = W68ShaderZoomBlur(ctx)
    // シェーダ(正射影)
    private val shaderOrth = W68ShaderOrth(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // 色相用カウンタ
    var cntColor = 0

    // 描画対象のテクスチャ
    var textureType = 0

    var u_strength = 5f

    // 正射影用の座標変換行列(合成用)
    //   ビュー×プロジェクション(正射影)
    val matOVP = FloatArray(16)

    // マウス位置
    val mouseP = FloatArray(2)

    init {
        // テクスチャ
        textures = IntArray(1)
        // フレームバッファ
        // 0:マスク
        // 1:ブラー
        frameBuf = IntBuffer.allocate(2)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(2)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(2)

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w68)
        bmpArray.add(bmp0)
    }

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()
        if ( (angle[0]%2) == 0 ) {
            cntColor++
        }

        // テクスチャの適用
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])

        // フレームバッファのバインド(マスク用)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(1f,1f,1f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 一度深度バッファへの描きこみを無効にする
        GLES32.glDepthMask(false)

        // 板ポリゴンをレンダリングしテクスチャを画面いっぱいに貼り付ける
        shaderOrth.draw(vaoBoard,matOVP,0)

        // 深度バッファへの描きこみを有効化する
        GLES32.glDepthMask(true)

        // -------------------------------------------------------
        // トーラス描画(9個)
        // -------------------------------------------------------
        (0..8).forEach { i ->
            val amb = MgColor.hsva(i*40,1f,1f,1f)
            Matrix.setIdentityM(matM,0)
            Matrix.rotateM(matM,0,i.toFloat()*360f/9f,0f,1f,0f)
            Matrix.translateM(matM,0,0f,0f,10f)
            Matrix.rotateM(matM,0,t0,1f,1f,0f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            Matrix.invertM(matI,0,matM,0)
            shaderScreen.draw(vaoTorus,matMVP,matI,vecLight,vecEye,amb.toFloatArray(),0)
        }

        // フレームバッファのバインド(ブラー用)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[1])

        // フレームバッファを初期化
        GLES32.glClearColor(0f,0f,0f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // フレームバッファ(マスク用)をテクスチャとして適用
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])

        // ズームブラーをかける
        shaderZoomBlur.draw(vaoBoard,matOVP,0,u_strength,renderW.toFloat(),mouseP)

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // canvasを初期化
        GLES32.glClearColor(0f, 0f, 0.7f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 背景用に読み込んだ画像をテクスチャとして適用
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])

        // 一度深度バッファへの描きこみを無効にする
        GLES32.glDepthMask(false)

        // 板ポリゴンをレンダリングしテクスチャを画面いっぱいに貼り付ける
        shaderOrth.draw(vaoBoard,matOVP,0)

        // 深度バッファへの描きこみを有効化する
        GLES32.glDepthMask(true)

        // -------------------------------------------------------
        // トーラス(9個)をライティング有効でレンダリング
        // -------------------------------------------------------
        (0..8).forEach { i ->
            val amb = MgColor.hsva(i*40,1f,1f,1f)
            Matrix.setIdentityM(matM,0)
            Matrix.rotateM(matM,0,i.toFloat()*360f/9f,0f,1f,0f)
            Matrix.translateM(matM,0,0f,0f,10f)
            Matrix.rotateM(matM,0,t0,1f,1f,0f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            Matrix.invertM(matI,0,matM,0)
            shaderScreen.draw(vaoTorus,matMVP,matI,vecLight,vecEye,amb.toFloatArray(),0)
        }

        // フレームバッファ(ブラー用)をテクスチャとして適用
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[1])

        // 加算合成するためにブレンドを有効化する
        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFuncSeparate(GLES32.GL_SRC_ALPHA,GLES32.GL_ONE,GLES32.GL_ONE,GLES32.GL_ONE)

        // ブラーを合成
        shaderOrth.draw(vaoBoard,matOVP,0)

        // ブレンドの無効化
        GLES32.glDisable(GLES32.GL_BLEND)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        // マウス位置
        mouseP[0] = renderW/2f
        mouseP[1] = renderH/2f

        // テクスチャを作成
        GLES32.glGenTextures(1,textures,0)
        // テクスチャに使う画像をロード
        MyGLES32Func.createTexture(0,textures,bmpArray[0],renderW)

        // フレームバッファ生成
        GLES32.glGenFramebuffers(2,frameBuf)
        // レンダ―バッファ生成
        GLES32.glGenRenderbuffers(2,depthRenderBuf)
        // フレームバッファを格納するテクスチャ生成
        GLES32.glGenTextures(2,frameTex)

        // 0:マスク
        MyGLES32Func.createFrameBuffer(renderW,renderH,0,frameBuf,depthRenderBuf,frameTex)
        // 1:ブラー
        MyGLES32Func.createFrameBuffer(renderW,renderH,1,frameBuf,depthRenderBuf,frameTex)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダ(メイン)
        shaderScreen.loadShader()

        // シェーダ(ズームブラー)
        shaderZoomBlur.loadShader()

        // シェーダ(正射影)
        shaderOrth.loadShader()

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

        // ライトの向き
        vecLight[0] = -0.577f
        vecLight[1] =  0.577f
        vecLight[2] =  0.577f

        // 視点座標
        vecEye[0] =  0f
        vecEye[1] = 20f
        vecEye[2] =  0f

        // 視点の上方向
        vecEyeUp[0] =  0f
        vecEyeUp[1] =  0f
        vecEyeUp[2] = -1f

        // ビュー×プロジェクション座標変換行列
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,90f,ratio,0.1f,100f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // 正射影用の座標変換行列(合成用)
        Matrix.setLookAtM(matV,0,
                0f,0f,0.5f,
                0f,0f,0f,
                0f,1f,0f)
        Matrix.orthoM(matP,0,-1f,1f,-1f,1f,0.1f,1f)
        Matrix.multiplyMM(matOVP,0,matP,0,matV,0)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorus.deleteVIBO()
        vaoBoard.deleteVIBO()
        shaderScreen.deleteShader()
        shaderZoomBlur.deleteShader()
        shaderOrth.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}
