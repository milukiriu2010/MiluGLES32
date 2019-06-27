package milu.kiriu2010.milugles32.w4x.w41

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.model.Sphere01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpc
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnct
import milu.kiriu2010.milugles32.R
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// --------------------------------------------
// フレームバッファを使ってブラーフィルター
// --------------------------------------------
// https://wgld.org/d/webgl/w041.html
// --------------------------------------------
class W41Renderer(ctx: Context): MgRenderer(ctx) {

    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = Board00Model()

    // VBO(球体)
    private val vaoSphere = ES32VAOIpnct()
    // VBO(板ポリゴン)
    private val vaoBoard = ES32VAOIpc()

    // シェーダ(シーン)
    private val shaderScene = W41ShaderScene(ctx)
    // シェーダ(ブラー)
    private val shaderBlur = W41ShaderBlur(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    // ブラー
    var isBlur = false

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
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w41_0)
        val bmp1 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w41_1)
        bmpArray.add(bmp0)
        bmpArray.add(bmp1)
    }

    override fun onDrawFrame(gl: GL10?) {
        // 回転角度
        angle[0] =(angle[0]+1)%360
        angle[1] =(angle[1]+1)%360
        val t0 = angle[0].toFloat()
        val t1 = angle[1].toFloat()

        // ---------------------------------------------------------
        // フレームバッファへ地球と背景をレンダリング
        // ---------------------------------------------------------

        // フレームバッファをバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // カメラの位置
        // ビュー座標変換行列
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        // ビュー×プロジェクション
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,100f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // 背景用球体をフレームバッファにレンダリング
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[1])
        Matrix.setIdentityM(matM,0)
        Matrix.scaleM(matM,0,50f,50f,50f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        shaderScene.draw(vaoSphere,matM,matMVP,matI,vecLight,0,1)

        // 地球本体をフレームバッファにレンダリング
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        shaderScene.draw(vaoSphere,matM,matMVP,matI,vecLight,1,0)

        // ---------------------------------------------------------
        // フレームバッファに描いた内容にブラーをかけ
        // テクスチャとして板ポリゴンにレンダリング
        // ---------------------------------------------------------

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // canvasを初期化
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // フレームバッファに描きこんだ内容をテクスチャとして適用
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])

        // カメラの位置
        // ビュー座標変換行列
        Matrix.setLookAtM(matV, 0,
                0f, 0f, 0.5f,
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        // 正射影
        Matrix.orthoM(matP,0,-1f,1f,-1f,1f,0.1f,1f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // 板ポリゴンをレンダリング
        Matrix.setIdentityM(matM,0)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        val u_useBlur = if (isBlur) 1 else 0
        shaderBlur.draw(vaoBoard,matMVP,u_useBlur,0,renderW.toFloat())
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        // テクスチャ作成し、idをtexturesに保存
        GLES32.glGenTextures(2,textures,0)
        MyGLES32Func.checkGlError("glGenTextures")
        // ------------------------------------------------------------------------------------
        // ブラーフィルターを適用する際、端にあるピクセルが周囲のピクセルを参照するときに、
        // REPEATだと、反対側の端にあるピクセルを参照してしまうため、CLAMP_TO_EDGEを用いている
        // ------------------------------------------------------------------------------------
        // テクスチャ0をバインド
        MyGLES32Func.createTexture(0,textures,bmpArray[0],renderW,GLES32.GL_CLAMP_TO_EDGE)
        // テクスチャ1をバインド
        MyGLES32Func.createTexture(1,textures,bmpArray[1],renderW,GLES32.GL_CLAMP_TO_EDGE)

        // フレームバッファ生成
        GLES32.glGenFramebuffers(1,frameBuf)
        // 深度バッファ用レンダ―バッファ生成
        GLES32.glGenRenderbuffers(1,depthRenderBuf)
        // フレームバッファ用テクスチャ生成
        GLES32.glGenTextures(1,frameTex)
        MyGLES32Func.createFrameBuffer(renderW,renderH,0,frameBuf,depthRenderBuf,frameTex)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)

        // シェーダ(シーン)
        shaderScene.loadShader()
        // シェーダ(ブラー)
        shaderBlur.loadShader()

        // モデル生成(球体)
        modelSphere.createPath(mapOf(
                "row"    to 32f,
                "column" to 32f,
                "radius" to 1f,
                "colorR" to 1f,
                "colorG" to 1f,
                "colorB" to 1f,
                "colorA" to 1f
        ))

        // モデル生成(ブラー)
        modelBoard.createPath()

        // VAO(球体)
        vaoSphere.makeVIBO(modelSphere)

        // VAO(板ポリゴン)
        vaoBoard.makeVIBO(modelBoard)

        // 光源位置
        vecLight[0] = -1f
        vecLight[1] =  2f
        vecLight[2] =  1f

        // 視点位置
        vecEye[0] = 0f
        vecEye[1] = 0f
        vecEye[2] = 5f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoSphere.deleteVIBO()
        vaoBoard.deleteVIBO()
        shaderScene.deleteShader()
        shaderBlur.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}