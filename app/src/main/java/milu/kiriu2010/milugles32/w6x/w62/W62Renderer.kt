package milu.kiriu2010.milugles32.w6x.w62

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.model.d3.Sphere01Model
import milu.kiriu2010.gui.model.d3.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpt
import milu.kiriu2010.math.MyMathUtil
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------------------
// ステンシル鏡面反射
// -----------------------------------------------------
// https://wgld.org/d/webgl/w062.html
// -----------------------------------------------------
class W62Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()
    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = Board00Model()

    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpnc()
    // VAO(球体)
    private val vaoSphere = ES32VAOIpnc()
    // VAO(板ポリゴン＋メイン)
    private val vaoBoardMain = ES32VAOIpnc()
    // VAO(板ポリゴン＋反射)
    private val vaoBoardMirror = ES32VAOIpt()

    // シェーダ(メイン)
    private val shaderMain = W62ShaderMain(ctx)
    // シェーダ(ステンシル鏡面反射)
    private val shaderMirror = W62ShaderMirror(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // 映り込み係数
    //   0.0 - 1.0
    var u_alpha = 0.5f

    // プロジェクションxビュー(正射影用の座標変換行列)
    private val matVP4O = FloatArray(16)

    var upDown = 0f

    init {
        // フレームバッファ
        frameBuf = IntBuffer.allocate(1)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(1)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(1)
    }

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()
        upDown = MyMathUtil.sinf(angle[0].toFloat()) * 0.25f

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,5f,5f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,-1f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,10f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // 正射影用の座標変換行列
        Matrix.setLookAtM(matV,0,
                0f,0f,0.5f,
                0f,0f,0f,
                0f,1f,0f)
        Matrix.orthoM(matP,0,-1f,1f,-1f,1f,0.1f,1f)
        Matrix.multiplyMM(matVP4O,0,matP,0,matV,0)

        // --------------------------------------------------------------------------------
        // 【0:映りこむ世界、すなわち上下が反転している鏡の向こう側の世界をレンダリング】
        // --------------------------------------------------------------------------------

        // フレームバッファのバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(0.3f, 0.9f, 0.9f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // ステンシルテストの無効化
        GLES32.glDisable(GLES32.GL_STENCIL_TEST)

        // カリング面の反転
        // 鏡面世界を映しこむため反転させている
        GLES32.glCullFace(GLES32.GL_FRONT)

        // レンダリング(トーラス)
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,1f,0f)
        Matrix.translateM(matM,0,0f,0.75f+upDown,0f)
        Matrix.rotateM(matM,0,90f,1f,0f,0f)
        Matrix.invertM(matI,0,matM,0)
        shaderMain.draw(vaoTorus,matM,matVP,matI,
                vecLight,vecEye, floatArrayOf(0f,0f,0f,0f), 1)

        // レンダリング(球体)
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,-t0,0f,1f,0f)
        Matrix.translateM(matM,0,0f,0.75f,1f)
        Matrix.invertM(matI,0,matM,0)
        shaderMain.draw(vaoSphere,matM,matVP,matI,
                vecLight,vecEye, floatArrayOf(0f,0f,0f,0f), 1)

        // --------------------------------------------------------------------------
        // 【1:通常のワールド空間にモデルを描画】
        // --------------------------------------------------------------------------

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // canvasを初期化
        GLES32.glClearColor(0f, 0.7f, 0.7f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClearStencil(0)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_STENCIL_BUFFER_BIT)

        // ステンシル設定
        // ステンシルテストに全て合格するように設定する
        // maskは~0(0の補数)なので"0.inv()"と思ったが"0xff"がいいっぽい
        // https://juejin.im/post/5acf2e85518825558a070164
        GLES32.glEnable(GLES32.GL_STENCIL_TEST)
        GLES32.glStencilFunc(GLES32.GL_ALWAYS,0,0xff)
        GLES32.glStencilOp(GLES32.GL_KEEP,GLES32.GL_KEEP,GLES32.GL_KEEP)

        // カリング面を元に戻す
        GLES32.glCullFace(GLES32.GL_BACK)

        // レンダリング(トーラス)
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,1f,0f)
        Matrix.translateM(matM,0,0f,0.75f+upDown,0f)
        Matrix.rotateM(matM,0,90f,1f,0f,0f)
        Matrix.invertM(matI,0,matM,0)
        shaderMain.draw(vaoTorus,matM,matVP,matI,
                vecLight,vecEye, floatArrayOf(0f,0f,0f,0f), 0)

        // レンダリング(球体)
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,-t0,0f,1f,0f)
        Matrix.translateM(matM,0,0f,0.75f,1f)
        Matrix.invertM(matI,0,matM,0)
        shaderMain.draw(vaoSphere,matM,matVP,matI,
                vecLight,vecEye, floatArrayOf(0f,0f,0f,0f), 0)

        // ステンシル設定
        // ステンシル値をインクリメント
        GLES32.glEnable(GLES32.GL_STENCIL_TEST)
        GLES32.glStencilFunc(GLES32.GL_ALWAYS,1,0xff)
        GLES32.glStencilOp(GLES32.GL_KEEP,GLES32.GL_KEEP,GLES32.GL_REPLACE)

        // レンダリング(板ポリゴン)
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,270f,1f,0f,0f)
        Matrix.scaleM(matM,0,2f,2f,1f)
        Matrix.invertM(matI,0,matM,0)
        shaderMain.draw(vaoBoardMain,matM,matVP,matI,
                vecLight,vecEye, floatArrayOf(0f,0f,0f,0f), 0)

        // ステンシル設定
        GLES32.glEnable(GLES32.GL_STENCIL_TEST)
        GLES32.glStencilFunc(GLES32.GL_EQUAL,1,0xff)
        GLES32.glStencilOp(GLES32.GL_KEEP,GLES32.GL_KEEP,GLES32.GL_KEEP)

        // テクスチャの設定
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])

        // レンダリング(板ポリゴン)
        shaderMirror.draw(vaoBoardMirror,matVP4O,0,u_alpha)
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
        // canvasを初期化する色を設定する
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // canvasを初期化する際の深度を設定する
        GLES32.glClearDepthf(1f)

        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)
        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFuncSeparate(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA, GLES32.GL_ONE, GLES32.GL_ONE)
        GLES32.glBlendEquationSeparate(GLES32.GL_FUNC_ADD,GLES32.GL_FUNC_ADD)

        // シェーダ(メイン)
        shaderMain.loadShader()

        // シェーダ(ステンシル鏡面反射)
        shaderMirror.loadShader()

        // モデル生成(トーラス)
        modelTorus.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 0.1f,
                "oradius" to 0.4f
        ))

        // モデル生成(球体)
        modelSphere.createPath(mapOf(
                "row" to 32f,
                "column" to 32f,
                "radius" to 0.25f
        ))

        // モデル生成(板ポリゴン)
        modelBoard.createPath(mapOf(
                "pattern" to 62f,
                "colorR" to 0.5f,
                "colorG" to 0.5f,
                "colorB" to 0.5f,
                "colorA" to 1f
        ))

        // VAO(トーラス)
        vaoTorus.makeVIBO(modelTorus)

        // VAO(球体)
        vaoSphere.makeVIBO(modelSphere)

        // VAO(板ポリゴン＋メイン)
        vaoBoardMain.makeVIBO(modelBoard)

        // VAO(板ポリゴン＋ミラー)
        vaoBoardMirror.makeVIBO(modelBoard)

        // 光源位置
        vecLight[0] = -0.577f
        vecLight[1] =  0.577f
        vecLight[2] =  0.577f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorus.deleteVIBO()
        vaoSphere.deleteVIBO()
        vaoBoardMain.deleteVIBO()
        vaoBoardMirror.deleteVIBO()
        shaderMain.deleteShader()
        shaderMirror.deleteShader()

        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}