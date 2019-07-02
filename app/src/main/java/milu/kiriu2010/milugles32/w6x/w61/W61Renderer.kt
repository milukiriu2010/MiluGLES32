package milu.kiriu2010.milugles32.w6x.w61

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.basic.MyNoiseX
import milu.kiriu2010.gui.model.d2.Box01Model
import milu.kiriu2010.gui.model.d2.Particle01Model
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIp
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpct
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random

// ------------------------------------------------------------
// emuglGLESv2_enc: device/generic/goldfish-opengl/system/GLESv2_enc/GL2Encoder.cpp:s_glVertexAttribPointer:599 GL error 0x501
// WV061ShaderFog:a_Normal:Particle01Model:1281
// ------------------------------------------------------------
// パーティクルフォグ
// ------------------------------------------------------------
//   板状の四角形ポリゴンを３次元空間にたくさん配置し、
//   これら板状のポリゴンに霧のようなテクスチャを適用して、
//   ブレンドを有効にして半透明描画することにより、
//   なんとなく霧っぽく見えるようにしている
// ------------------------------------------------------------
// https://wgld.org/d/webgl/w061.html
// ------------------------------------------------------------
class W61Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()
    // 描画オブジェクト(ボックスモデル)
    private val modelBox = Box01Model()
    // 描画オブジェクト(パーティクル)
    private val modelParticle = Particle01Model()

    // VAO(トーラス＋メイン)
    private val vaoTorusMain = ES32VAOIpnc()
    // VAO(ボックス＋メイン)
    private val vaoBoxMain = ES32VAOIpnc()
    // VAO(トーラス＋深度)
    private val vaoTorusDepth = ES32VAOIp()
    // VBO(ボックス＋深度)
    private val vaoBoxDepth = ES32VAOIp()
    // VBO(パーティクル)
    private val vaoParticle = ES32VAOIpct()

    // シェーダ(メイン)
    private val shaderMain = W61ShaderMain(ctx)
    // シェーダ(深度をレンダリング)
    private val shaderDepth = W61ShaderDepth(ctx)
    // シェーダ(フォグ)
    private val shaderFog = W61ShaderFog(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    // ソフトパーティクルを適用するかどうか
    var u_softParticle = 1

    // 深度値
    var u_depthCoef = 0.05f

    // テクスチャ座標変換行列
    private val matTex = FloatArray(16)
    // ビュー×プロジェクション×テクスチャ座標変換行列
    private val matVPT = FloatArray(16)

    // --------------------------------------------
    // パーティクル用のデータ
    // --------------------------------------------
    // パーティクルの数
    private val particleCount = 30
    // パーティクルの初期X座標
    private val offsetPositionX = FloatArray(particleCount)
    // パーティクルの初期Z座標
    private val offsetPositionZ = FloatArray(particleCount)
    // パーティクルの移動速度
    private val offsetPositionS = FloatArray(particleCount)
    // テクスチャのオフセット座標
    private val offsetTexCoordS = FloatArray(particleCount)
    private val offsetTexCoordT = FloatArray(particleCount)

    init {
        // テクスチャ
        textures = IntArray(1)
        // フレームバッファ
        frameBuf = IntBuffer.allocate(1)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(1)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(1)

        (0 until particleCount).forEach{ i ->
            // Random.nextFloat() => 0 - 1
            offsetPositionX[i] =  Random.nextFloat()*6f - 3f
            offsetPositionZ[i] = -Random.nextFloat()*1.5f + 0.5f
            offsetPositionS[i] =  Random.nextFloat()*0.02f
            offsetTexCoordS[i] =  Random.nextFloat()
            offsetTexCoordT[i] =  Random.nextFloat()
        }
        // Z座標をソートして奥から順番にパーティクルがレンダリングされるようにする
        offsetPositionZ.sort()
        Log.d(javaClass.simpleName,"create noise bitmap start")

        // ノイズを生成するビットマップに描く
        val noise = MyNoiseX(5,2,0.6f)
        noise.seed = (SystemClock.uptimeMillis()/1000).toInt()
        val size = 128
        val noiseColor = FloatArray(size*size)
        (0 until size).forEach { i ->
            (0 until size).forEach { j ->
                noiseColor[i*size+j] = noise.snoise(i.toFloat(),j.toFloat(),size.toFloat())
            }
        }

        bmpArray.clear()
        val bmp0 = noise.createImageGray(size,noiseColor)
        bmpArray.add(0,bmp0)

        Log.d(javaClass.simpleName,"create noise bitmap end")

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
        angle[0] =(angle[0]+1)%360
        val angleF = angle[0].toFloat()

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,5f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,10f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // 行列を掛け合わせる
        val matPT = FloatArray(16)
        Matrix.multiplyMM(matPT,0,matTex,0,matP,0)
        Matrix.multiplyMM(matVPT,0,matPT,0,matV,0)

        // フレームバッファのバインド(1:Scene)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(0f, 0f, 1f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 深度のレンダリング用にブレンドを無効化
        GLES32.glDisable(GLES32.GL_BLEND)

        // -------------------------------------------------------
        // トーラスをレンダリング
        // -------------------------------------------------------
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,angleF,0f,1f,0f)
        Matrix.translateM(matM,0,0f,0.5f,0f)
        Matrix.rotateM(matM,0,90f,1f,0f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderDepth.draw(vaoTorusDepth,matMVP,0f)

        // -------------------------------------------------------
        // ボックスをレンダリング
        // -------------------------------------------------------
        Matrix.setIdentityM(matM,0)
        Matrix.scaleM(matM,0,2f,2f,2f)
        Matrix.translateM(matM,0,0f,-0.25f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderDepth.draw(vaoBoxDepth,matMVP,0f)

        // -----------------------------------------------
        // 【1:メインシーンをレンダリング】
        // -----------------------------------------------

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // canvasを初期化
        GLES32.glClearColor(0f, 0.7f, 0.7f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // ブレンドを有効化
        GLES32.glEnable(GLES32.GL_BLEND)

        // -------------------------------------------------------
        // トーラスをレンダリング
        // -------------------------------------------------------
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,angleF,0f,1f,0f)
        Matrix.translateM(matM,0,0f,0.5f,0f)
        Matrix.rotateM(matM,0,90f,1f,0f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        shaderMain.draw(vaoTorusMain,matM,matMVP,matI,vecLight,vecEye, floatArrayOf(0f,0f,0f,0f))

        // -------------------------------------------------------
        // ボックスをレンダリング
        // -------------------------------------------------------
        Matrix.setIdentityM(matM,0)
        Matrix.scaleM(matM,0,2f,2f,2f)
        Matrix.translateM(matM,0,0f,-0.25f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        shaderMain.draw(vaoBoxMain,matM,matMVP,matI,vecLight,vecEye, floatArrayOf(0f,0f,0f,0f))

        // -----------------------------------------------
        // 【2:フォグをレンダリング】
        // -----------------------------------------------

        // テクスチャのバインド
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])

        // -------------------------------------------------------
        // パーティクルをレンダリング
        // -------------------------------------------------------
        (0 until particleCount).forEach { i ->
            offsetPositionX[i] += offsetPositionS[i]
            if (offsetPositionX[i] > 3f) offsetPositionX[i] = -3f

            Matrix.setIdentityM(matM,0)
            Matrix.translateM(matM,0,offsetPositionX[i],0.5f,offsetPositionZ[i])
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            shaderFog.draw(vaoParticle,matM,matMVP,matVPT,
                    floatArrayOf(offsetTexCoordS[i],offsetTexCoordT[i]),u_depthCoef,0,1,u_softParticle)
        }
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
        GLES32.glGenFramebuffers(1,frameBuf)
        // レンダ―バッファ生成
        GLES32.glGenRenderbuffers(1,depthRenderBuf)
        // フレームバッファを格納するテクスチャ生成
        GLES32.glGenTextures(1,frameTex)
        MyGLES32Func.createFrameBuffer(renderW,renderH,0,frameBuf,depthRenderBuf,frameTex)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリング,深度テスト,ブレンドを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)
        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFuncSeparate(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA, GLES32.GL_ONE, GLES32.GL_ONE)
        GLES32.glBlendEquationSeparate(GLES32.GL_FUNC_ADD,GLES32.GL_FUNC_ADD)

        // シェーダ(メイン)
        shaderMain.loadShader()

        // シェーダ(深度をレンダリング)
        shaderDepth.loadShader()

        // シェーダ(フォグ)
        shaderFog.loadShader()

        // モデル生成(トーラス)
        modelTorus.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 0.25f,
                "oradius" to 0.5f
        ))

        // モデル生成(ボックス)
        modelBox.createPath(mapOf(
                "colorR"  to 0.3f,
                "colorG"  to 0.3f,
                "colorB"  to 0.3f,
                "colorA"  to 1f
        ))

        // 描画オブジェクト(パーティクル)
        modelParticle.createPath(mapOf(
                "colorR"  to 1f,
                "colorG"  to 1f,
                "colorB"  to 1f,
                "colorA"  to 1f
        ))

        // VAO(トーラス＋メイン)
        vaoTorusMain.makeVIBO(modelTorus)

        // VAO(ボックス＋メイン)
        vaoBoxMain.makeVIBO(modelBox)

        // VAO(トーラス＋深度)
        vaoTorusDepth.makeVIBO(modelTorus)

        // VAO(ボックス＋深度)
        vaoBoxDepth.makeVIBO(modelBox)

        // VAO(パーティクル)
        vaoParticle.makeVIBO(modelParticle)

        // 光源位置
        vecLight[0] = -0.577f
        vecLight[1] =  0.577f
        vecLight[2] =  0.577f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorusMain.deleteVIBO()
        vaoBoxMain.deleteVIBO()
        vaoTorusDepth.deleteVIBO()
        vaoBoxDepth.deleteVIBO()
        vaoParticle.deleteVIBO()
        shaderMain.deleteShader()
        shaderDepth.deleteShader()
        shaderFog.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}