package milu.kiriu2010.milugles32.w6x.w66

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

// ---------------------------------------
// emuglGLESv2_enc: device/generic/goldfish-opengl/system/GLESv2_enc/GL2Encoder.cpp:s_glVertexAttribPointer:599 GL error 0x501
// WV066ShaderMosaic:a_TexCoord:Board00Model:1281
// ---------------------------------------
// モザイク
// ---------------------------------------
// https://wgld.org/d/webgl/w066.html
// ---------------------------------------
class W66Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = Board00Model()

    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpnc()
    // VAO(板ポリゴン)
    private val vaoBoard = ES32VAOIpt()

    // シェーダ(メイン)
    private val shaderScreen = W66ShaderScreen(ctx)
    // シェーダ(モザイク)
    private var shaderMosaic = W66ShaderMosaic(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // 色相用カウンタ
    var cntColor = 0

    // 描画対象のテクスチャ
    var textureType = 0

    init {
        // テクスチャ
        textures = IntArray(2)
        // フレームバッファ
        frameBuf = IntBuffer.allocate(1)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(1)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(1)

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
        val hsv = MgColor.hsva(cntColor%360,1f,1f,1f)
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

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // canvasを初期化
        GLES32.glClearColor(0f, 0f, 0f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

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

        // 板ポリゴンの描画
        shaderMosaic.draw(vaoBoard,matVP,0,renderW.toFloat())
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
        GLES32.glGenFramebuffers(1,frameBuf)
        // レンダ―バッファ生成
        GLES32.glGenRenderbuffers(1,depthRenderBuf)
        // フレームバッファを格納するテクスチャ生成
        GLES32.glGenTextures(1,frameTex)
        MyGLES32Func.createFrameBuffer(renderW,renderH,0,frameBuf,depthRenderBuf,frameTex)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダ(メイン)
        shaderScreen.loadShader()

        // シェーダ(モザイク)
        shaderMosaic.loadShader()

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
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorus.deleteVIBO()
        vaoBoard.deleteVIBO()
        shaderScreen.deleteShader()
        shaderMosaic.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}