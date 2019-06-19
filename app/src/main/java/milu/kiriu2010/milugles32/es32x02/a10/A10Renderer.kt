package milu.kiriu2010.milugles32.es32x02.a10

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.Sphere01Model
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnt
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.es32x01.a03.ES32a03ShaderA
import milu.kiriu2010.milugles32.es32x01.a03.ES32a03ShaderB
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------
// Sampler Object
// -----------------------------------------
// https://wgld.org/d/webgl2/w010.html
// -----------------------------------------
class A10Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = Board00Model()

    // VAO(球体)
    private val vaoSphere = ES32VAOIpnt()
    // VAO(板ポリゴン)
    private val vaoBoard = ES32VAOIpnt()

    // シェーダA
    private val shaderA = ES32a03ShaderA(ctx)
    // シェーダB
    private val shaderB = ES32a03ShaderB(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // モデル座標変換行列⇒逆行列⇒転置行列
    val matN = FloatArray(16)

    init {
        // テクスチャ
        textures = IntArray(1)
        // フレームバッファ
        frameBuf = IntBuffer.allocate(1)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(1)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(1)

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_lenna_a03)
        bmpArray.add(bmp0)
    }

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,5f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,60f,ratio,0.1f,20f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // フレームバッファのバインド(マスク用)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(0.3f,0.3f,0.3f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 球体(nearest)
        Matrix.setIdentityM(matM,0)
        Matrix.translateM(matM,0,1f,0f,0f)
        Matrix.rotateM(matM,0,t0,0f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        Matrix.transposeM(matN,0,matI,0)
        shaderA.draw(vaoSphere,matM,matMVP,matN,vecLight,vecEye,1)

        // 球体(linear)
        Matrix.setIdentityM(matM,0)
        Matrix.translateM(matM,0,-1f,0f,0f)
        Matrix.rotateM(matM,0,t0,0f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        Matrix.transposeM(matN,0,matI,0)
        shaderA.draw(vaoSphere,matM,matMVP,matN,vecLight,vecEye,2)

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // デフォルトバッファを初期化
        GLES32.glClearColor(0f, 0f, 0f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // 板ポリゴンをレンダリング
        shaderB.draw(vaoBoard,0)
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

        // サンプラー
        val samplerNearest = IntArray(1)
        GLES32.glGenSamplers(1,samplerNearest,0)
        GLES32.glSamplerParameteri(samplerNearest[0],GLES32.GL_TEXTURE_MIN_FILTER,GLES32.GL_NEAREST)
        GLES32.glSamplerParameteri(samplerNearest[0],GLES32.GL_TEXTURE_MAG_FILTER,GLES32.GL_NEAREST)
        GLES32.glSamplerParameteri(samplerNearest[0],GLES32.GL_TEXTURE_WRAP_S,GLES32.GL_CLAMP_TO_EDGE)
        GLES32.glSamplerParameteri(samplerNearest[0],GLES32.GL_TEXTURE_WRAP_T,GLES32.GL_CLAMP_TO_EDGE)
        val samplerLinear = IntArray(1)
        GLES32.glGenSamplers(1,samplerLinear,0)
        GLES32.glSamplerParameteri(samplerLinear[0],GLES32.GL_TEXTURE_MIN_FILTER,GLES32.GL_LINEAR)
        GLES32.glSamplerParameteri(samplerLinear[0],GLES32.GL_TEXTURE_MAG_FILTER,GLES32.GL_LINEAR)
        GLES32.glSamplerParameteri(samplerLinear[0],GLES32.GL_TEXTURE_WRAP_S,GLES32.GL_CLAMP_TO_EDGE)
        GLES32.glSamplerParameteri(samplerLinear[0],GLES32.GL_TEXTURE_WRAP_T,GLES32.GL_CLAMP_TO_EDGE)

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])
        GLES32.glBindSampler(1,samplerNearest[0])
        GLES32.glActiveTexture(GLES32.GL_TEXTURE2)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])
        GLES32.glBindSampler(2,samplerLinear[0])

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダA
        shaderA.loadShader()

        // シェーダB
        shaderB.loadShader()

        // 描画オブジェクト(球体)
        modelSphere.createPath(mapOf(
            "row" to 16f,
            "column" to 16f,
            "radius" to 1f
        ))

        // モデル生成(板ポリゴン)
        modelBoard.createPath(mapOf(
                "pattern" to 100f
        ))

        // VAO(球体)
        vaoSphere.makeVIBO(modelSphere)

        // VAO(板ポリゴン)
        vaoBoard.makeVIBO(modelBoard)

        // ライトの向き
        vecLight[0] = 5f
        vecLight[1] = 5f
        vecLight[2] = 2f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoSphere.deleteVIBO()
        vaoBoard.deleteVIBO()
        shaderA.deleteShader()
        shaderB.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}