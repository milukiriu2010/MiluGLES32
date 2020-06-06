package milu.kiriu2010.milugles32.w7x.w72

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d3.Sphere01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.milugles32.w7x.w71.W71VAOi
import milu.kiriu2010.milugles32.w7x.w71.W71VAOpi
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -------------------------------------
// いまいちよくわからない
// -------------------------------------
// 浮動小数点数VTF
// -------------------------------------
// https://wgld.org/d/webgl/w072.html
// -------------------------------------
class W72Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()

    // VBO(点)
    private val vaoPoint = W71VAOi()
    // VBO(マッピング)
    private val vaoMapping = W71VAOpi()

    // シェーダ(点のレンダリングを行う)
    private val shaderPoint = W72ShaderPoint(ctx)
    // シェーダ(テクスチャへの描きこみを行う)
    private val shaderMapping = W72ShaderMapping(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // 描画対象のテクスチャ
    var textureType = 0

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

        // ビュー×プロジェクション座標変換行列
        Matrix.setLookAtM(matV,0,
                0f,0f,5f,
                0f,0f,0f,
                0f,1f,0f)
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,10f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // フレームバッファをバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // ビューポートを設定
        GLES32.glViewport(0,0,16,16)

        // フレームバッファを初期化
        GLES32.glClearColor(0f,0f,0f,1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)

        // テクスチャへ頂点情報をレンダリング
        shaderMapping.draw(vaoMapping,vaoMapping.datIndex.size)

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // ビューポートを設定
        GLES32.glViewport(0, 0, renderW, renderH)

        // canvasを初期化
        GLES32.glClearColor(1f,1f,1f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // フレームバッファをテクスチャとしてバインド
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])

        // 点を描画
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderPoint.draw(vaoPoint,matMVP,0,vaoPoint.datIndex.size)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        // フレームバッファ生成
        GLES32.glGenFramebuffers(1,frameBuf)
        // レンダ―バッファ生成
        GLES32.glGenRenderbuffers(1,depthRenderBuf)
        // フレームバッファを格納するテクスチャ生成
        GLES32.glGenTextures(1,frameTex)
        MyGLES32Func.createFrameBuffer(16,16,0,frameBuf,depthRenderBuf,frameTex,GLES32.GL_FLOAT)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // uniform変数の上限数
        // Huawei P20Lite:1024
        val uniformMaxCnt = IntBuffer.allocate(1)
        GLES32.glGetIntegerv(GLES32.GL_MAX_FRAGMENT_UNIFORM_VECTORS,uniformMaxCnt)
        //callback.receive(pointSizeRange)
        Log.d(javaClass.simpleName,"uniformMaxCnt:${uniformMaxCnt.get(0)}")
        // 頂点テクスチャフェッチが可能かどうか調べる
        // Huawei P20Lite:16
        val vertexTextureMaxCnt = IntBuffer.allocate(1)
        GLES32.glGetIntegerv(GLES32.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS,vertexTextureMaxCnt)
        Log.d(javaClass.simpleName,"vertexTextureMaxCnt:${vertexTextureMaxCnt.get(0)}")

        // 深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)

        // シェーダ(点のレンダリングを行う)
        shaderPoint.loadShader()

        // シェーダ(テクスチャへの描きこみを行う)
        shaderMapping.loadShader()

        // モデル生成(球体)
        modelSphere.createPath(mapOf(
                //"pattern" to 2f,
                "row"     to 15f,
                "column"  to 15f,
                "radius" to 1f
        ))

        // VAO(点)
        vaoPoint.makeVIBO(modelSphere)

        // VAO(マッピング)
        vaoMapping.makeVIBO(modelSphere)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoPoint.deleteVIBO()
        vaoMapping.deleteVIBO()
        shaderPoint.deleteShader()
        shaderMapping.deleteShader()

        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}