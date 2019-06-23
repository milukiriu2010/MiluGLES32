package milu.kiriu2010.milugles32.w5x.w51

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.model.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIp
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.floor

// -----------------------------------------------------------
// シャドウマッピング
// -----------------------------------------------------------
// フレームバッファにライトから見たときの各頂点の深度値を描く
// 本番のレンダリングを行う際に、
// フレームバッファから読みだした深度値と
// 実際にレンダリングしようとしている頂点の深度を比較すれば
// その頂点が影にあるのかどうかを判別できる
// -----------------------------------------------------------
// デプスバッファの値は0.0～1.0の範囲
// カメラに最も近いところ  ⇒0.0
// カメラから最も遠いところ⇒1.0
// -----------------------------------------------------------
// emuglGLESv2_enc: Out of bounds vertex attribute info: clientArray? 1 attribute 1 vbo 10 allocedBufferSize 48 bufferDataSpecified? 1 wantedStart 0 wantedEnd 13068
// -----------------------------------------------------------
// https://wgld.org/d/webgl/w051.html
// -----------------------------------------------------------
class W51Renderer(ctx: Context): MgRenderer(ctx) {

    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()
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
    private val shaderDepth = W51ShaderDepth(ctx)
    // シェーダ(スクリーンレンダリング用)
    private val shaderScreen = W51ShaderScreen(ctx)

    // ライトビューの上方向
    val vecLightUp = floatArrayOf(0f,0f,-1f)

    // 画面縦横比
    var ratio: Float = 0f

    // テクスチャ座標変換行列
    private val matTex = FloatArray(16)
    // ビュー×プロジェクション×テクスチャ座標変換行列
    private val matVPT = FloatArray(16)
    // ライトから見たモデル×ビュー×プロジェクション座標変換行列
    private val matMVP4L = FloatArray(16)
    // ライトから見たビュー座標変換行列
    private val matV4L = FloatArray(16)
    // ライトから見たプロジェクション座標変換行列
    private val matP4L = FloatArray(16)
    // ライトから見たビュー×プロジェクション座標変換行列
    private val matVP4L = FloatArray(16)

    // ライトの位置補正用係数
    //   k:20-40
    var k = 30f

    // 深度値を使うかどうか
    var u_depthBuffer = 1

    init {
        // フレームバッファ
        frameBuf = IntBuffer.allocate(1)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(1)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(1)

        // -------------------------------------------------------
        // テクスチャ変換用行列
        // -------------------------------------------------------
        // matTex[5]は
        // 画像から読み込んだ場合は、-0.5fだが、
        // フレームバッファに描いた風景は、初めから上下が判定しているので0.5f
        // -------------------------------------------------------
        matTex[0]  = 0.5f;  matTex[1]  =   0f;  matTex[2]  = 0f;  matTex[3]  = 0f;
        matTex[4]  =   0f;  matTex[5]  = 0.5f;  matTex[6]  = 0f;  matTex[7]  = 0f;
        matTex[8]  =   0f;  matTex[9]  =   0f;  matTex[10] = 1f;  matTex[11] = 0f;
        matTex[12] = 0.5f;  matTex[13] = 0.5f;  matTex[14] = 0f;  matTex[15] = 1f;
    }

    override fun onDrawFrame(gl: GL10?) {
        // 回転角度
        angle[0] =(angle[0]+1)%360

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,70f,0f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,0f,-1f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,150f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // ライトの距離を係数で調整
        //   k=20-40
        vecLight[0] = 0f * k
        vecLight[1] = 1f * k
        vecLight[2] = 0f * k

        // ライトから見たビュー座標変換行列
        Matrix.setLookAtM(matV4L, 0,
                vecLight[0], vecLight[1], vecLight[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecLightUp[0], vecLightUp[1], vecLightUp[2])
        // ライトから見たプロジェクション座標変換行列
        Matrix.perspectiveM(matP4L,0,90f,ratio,0.1f,150f)

        // ライトから見た座標変換行列を掛け合わせ
        // ビュー×プロジェクション×テクスチャ座標変換行列を求める
        val matPT = FloatArray(16)
        Matrix.multiplyMM(matPT,0,matTex,0,matP4L,0)
        Matrix.multiplyMM(matVPT,0,matPT,0,matV4L,0)

        // ライトから見たビュー×プロジェクション座標変換行列
        Matrix.multiplyMM(matVP4L,0,matP4L,0,matV4L,0)

        // ------------------------------------------------------
        // フレームバッファにはライトから見た時の深度値のみを描く
        // デプスバッファの範囲は0.0～1.0
        //   カメラに最も近いところ⇒0.0
        //   　　　　最も遠いところ⇒1.0
        // ------------------------------------------------------
        // フレームバッファをバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(0.0f, 1f, 1f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // -------------------------------------------------------
        // シャドウマップ
        // -------------------------------------------------------
        // トーラス描画(10個)
        // 上段・下段５個ずつ、五角形の配置
        // -------------------------------------------------------
        (0..9).forEach { i ->
            // 回転角度
            val angleT1 =(angle[0]+i*36)%360
            val angleT2 =((i%5)*72)%360
            val t1 = angleT1.toFloat()
            val t2 = angleT2.toFloat()
            // ------------------------
            // i:0-4 ⇒ ifl:1.0
            // i:5-9 ⇒ ifl:0.0
            // ------------------------
            val ifl = -floor(i.toFloat()/5f) +1f
            Matrix.setIdentityM(matM,0)
            Matrix.rotateM(matM,0,t2,0f,1f,0f)
            Matrix.translateM(matM,0,0f,ifl*10f+10f,(ifl-2f)*7f)
            Matrix.rotateM(matM,0,t1,1f,1f,0f)
            Matrix.multiplyMM(matMVP4L,0,matVP4L,0,matM,0)
            // フレームバッファに描く深度値は
            // ライトの位置を視点としてみたときの各頂点の深度値のため、
            // ライトからみたモデル×ビュー×プロジェクション座標変換行列を渡している
            shaderDepth.draw(vaoTorusDepth,matMVP4L,u_depthBuffer)
        }

        // 板ポリゴンの描画(底面)
        Matrix.setIdentityM(matM,0)
        Matrix.translateM(matM,0,0f,-10f,0f)
        Matrix.scaleM(matM,0,30f,0f,30f)
        Matrix.multiplyMM(matMVP4L,0,matVP4L,0,matM,0)
        // フレームバッファに描く深度値は
        // ライトの位置を視点としてみたときの各頂点の深度値のため、
        // ライトからみたモデル×ビュー×プロジェクション座標変換行列を渡している
        shaderDepth.draw(vaoBoardDepth,matMVP4L,0)

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

        // -------------------------------------------------------
        // トーラス描画(10個)
        // -------------------------------------------------------
        Log.d(javaClass.simpleName,"==============================")
        (0..9).forEach { i ->
            // 回転角度
            val angleT1 =(angle[0]+i*36)%360
            val angleT2 =((i%5)*72)%360
            val t1 = angleT1.toFloat()
            val t2 = angleT2.toFloat()
            // ------------------------
            // i:0-4 ⇒ ifl: 1.0
            // i:5-9 ⇒ ifl: 0.0
            // ------------------------
            val ifl = -floor(i.toFloat()/5f) +1f
            Matrix.setIdentityM(matM,0)
            Matrix.rotateM(matM,0,t2,0f,1f,0f)
            Matrix.translateM(matM,0,0f,ifl*10f+10f,(ifl-2f)*7f)
            Matrix.rotateM(matM,0,t1,1f,1f,0f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            Matrix.invertM(matI,0,matM,0)
            Matrix.multiplyMM(matMVP4L,0,matVP4L,0,matM,0)
            shaderScreen.draw(vaoTorusScreen,matM,matMVP,matI,matVPT,matMVP4L,vecLight,0,u_depthBuffer)
            //Log.d(javaClass.simpleName,"i[$i]ifl[$ifl]")
        }

        // 板ポリゴンの描画(底面)
        Matrix.setIdentityM(matM,0)
        Matrix.translateM(matM,0,0f,-10f,0f)
        Matrix.scaleM(matM,0,30f,0f,30f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        Matrix.multiplyMM(matMVP4L,0,matVP4L,0,matM,0)
        shaderScreen.draw(vaoBoardScreen,matM,matMVP,matI,matVPT,matMVP4L,vecLight,0,0)
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
        MyGLES32Func.createFrameBuffer(renderW,renderH,0,frameBuf,depthRenderBuf,frameTex)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダ(深度値格納用)
        shaderDepth.loadShader()
        // シェーダ(スクリーンレンダリング用)
        shaderScreen.loadShader()

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
                "pattern" to 51f,
                "colorR"  to 1f,
                "colorG"  to 1f,
                "colorB"  to 1f,
                "colorA"  to 1f
        ))

        // VBO(トーラス＋深度値格納)
        vaoTorusDepth.makeVIBO(modelTorus)

        // VBO(トーラス＋スクリーン)
        vaoTorusScreen.makeVIBO(modelTorus)

        // VBO(板ポリゴン＋深度値格納)
        vaoBoardDepth.makeVIBO(modelBoard)

        // VBO(板ポリゴン＋スクリーン)
        vaoBoardScreen.makeVIBO(modelBoard)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorusDepth.deleteVIBO()
        vaoTorusScreen.deleteVIBO()
        vaoBoardDepth.deleteVIBO()
        vaoBoardScreen.deleteVIBO()
        shaderDepth.deleteShader()
        shaderScreen.deleteShader()

        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}