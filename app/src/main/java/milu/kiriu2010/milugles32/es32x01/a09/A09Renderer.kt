package milu.kiriu2010.milugles32.es32x01.a09

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.model.Sphere01Model
import milu.kiriu2010.gui.model.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIp
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------
// gl_VertexIDとgl_InstanceID
// -----------------------------------------
// https://wgld.org/d/webgl2/w008.html
// -----------------------------------------
class A09Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(板ポリゴン)
    private var modelBoard = Board00Model()
    // 描画オブジェクト(トーラス)
    private var modelTorus = Torus01Model()
    // 描画オブジェクト(球体)
    private var modelSphere = Sphere01Model()

    // シェーダA
    private val shaderA = ES32a09ShaderA(ctx)
    // シェーダB
    private val shaderB = ES32a09ShaderB(ctx)
    // シェーダC
    private val shaderC = ES32a09ShaderC(ctx)

    // VAO(トーラス)
    private val vaoTorus = ES32VAOIp()
    // VAO(球体)
    private val vaoSphere = ES32VAOIp()
    // VAO(板ポリゴン)
    private val vaoBoard = ES32VAOIp()

    // 画面縦横比
    var ratio: Float = 1f

    // UBOハンドル
    val bufUBO = IntBuffer.allocate(2)

    init {
        // フレームバッファ
        frameBuf = IntBuffer.allocate(1)
        // 深度バッファ用レンダ―バッファ
        depthRenderBuf = IntBuffer.allocate(1)
        // フレームバッファ用のテクスチャ
        frameTex = IntBuffer.allocate(1)
    }

    override fun onDrawFrame(gl: GL10?) {
        //Log.d(javaClass.simpleName,"onDrawFrame:start")

        // フレームバッファのバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(0.3f,0.3f,0.3f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // ---------------------------
        // 球体を描画(左側)
        // ---------------------------
        GLES32.glViewport(0,0,renderW/2,renderH)
        shaderA.draw(vaoSphere,0.9f)

        // ---------------------------
        // トーラスを描画(左側)
        // ---------------------------
        GLES32.glViewport(renderW/2,0,renderW/2,renderH)
        shaderB.draw(vaoTorus,1.1f)

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // canvasを初期化
        GLES32.glClearColor(0f, 0f, 0f, 1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        GLES32.glViewport(0, 0, renderW, renderH)

        // フレームバッファをテクスチャとして適用
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])

        // 板ポリゴンをレンダリング
        shaderC.draw(vaoBoard,0)

        //Log.d(javaClass.simpleName,"onDrawFrame:end")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //Log.d(javaClass.simpleName,"onSurfaceChanged:start")
        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        // モデル座標変換行列
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,90f,1f,0f,0f)

        // ビュー・プロジェクション座標変換行列
        Matrix.setLookAtM(matV,0,
                vecEye[0],vecEye[1],vecEye[2],
                vecCenter[0],vecCenter[1],vecCenter[2],
                vecEyeUp[0],vecEyeUp[1],vecEyeUp[2])
        Matrix.perspectiveM(matP,0,60f,0.5f,0.1f,15f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)

        // UBOを生成
        GLES32.glGenBuffers(2,bufUBO)

        // UBO(u_mat)
        val bufMatMVP = ByteBuffer.allocateDirect(matMVP.size*4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(matMVP)
                position(0)
            }
        }
        GLES32.glBindBuffer(GLES32.GL_UNIFORM_BUFFER,bufUBO[0])
        GLES32.glBufferData(GLES32.GL_UNIFORM_BUFFER,bufMatMVP.capacity()*4,bufMatMVP,GLES32.GL_DYNAMIC_DRAW)
        GLES32.glBindBuffer(GLES32.GL_UNIFORM_BUFFER,0)

        // UBO(u_color)
        val baseColor = floatArrayOf(1f,0.6f,0.1f,1f)
        val bufBaseColor = ByteBuffer.allocateDirect(baseColor.size*4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(baseColor)
                position(0)
            }
        }
        GLES32.glBindBuffer(GLES32.GL_UNIFORM_BUFFER,bufUBO[1])
        GLES32.glBufferData(GLES32.GL_UNIFORM_BUFFER,bufBaseColor.capacity()*4,bufBaseColor,GLES32.GL_DYNAMIC_DRAW)
        GLES32.glBindBuffer(GLES32.GL_UNIFORM_BUFFER,0)

        // UBOをバインド
        GLES32.glBindBufferBase(GLES32.GL_UNIFORM_BUFFER,0,bufUBO[0])
        GLES32.glBindBufferBase(GLES32.GL_UNIFORM_BUFFER,1,bufUBO[1])

        // フレームバッファ生成
        GLES32.glGenFramebuffers(1,frameBuf)
        // レンダ―バッファ生成
        GLES32.glGenRenderbuffers(1,depthRenderBuf)
        // フレームバッファを格納するテクスチャ生成
        GLES32.glGenTextures(1,frameTex)
        MyGLES32Func.createFrameBuffer(renderW,renderH,0,frameBuf,depthRenderBuf,frameTex)

        //Log.d(javaClass.simpleName,"onSurfaceChanged:end")
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

        // シェーダC
        shaderC.loadShader()

        // モデル生成(板ポリゴン)
        modelBoard.createPath(mapOf(
                "pattern" to 100f
        ))

        // モデル生成(トーラス)
        modelTorus.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 0.25f,
                "oradius" to 0.75f
        ))

        // モデル生成(球体)
        modelSphere.createPath(mapOf(
                "row"    to 16f,
                "column" to 16f,
                "radius" to 1f
        ))

        // VAO(トーラス)
        vaoTorus.makeVIBO(modelTorus)
        // VAO(球体)
        vaoSphere.makeVIBO(modelSphere)
        // VAO(板ポリゴン)
        vaoBoard.makeVIBO(modelBoard)

        /*
        // 光源位置
        vecLight[0] = 5f
        vecLight[1] = 2f
        vecLight[2] = 5f
        */

        // 視点座標
        vecEye[0] = 0f
        vecEye[1] = 0f
        vecEye[2] = 3f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoTorus.deleteVIBO()
        vaoSphere.deleteVIBO()
        vaoBoard.deleteVIBO()
        shaderA.deleteShader()
        shaderB.deleteShader()

        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}