package milu.kiriu2010.milugles32.w8x.w80

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.opengl.GLES32
import android.opengl.Matrix
import android.view.Surface
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d2.Board01Model
import milu.kiriu2010.gui.model.d3.Cube01Model
import milu.kiriu2010.gui.model.d3.Sphere01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpct
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpt
import milu.kiriu2010.milugles32.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// --------------------------------------
// シェーダ(ビデオ:クロマキー)
// --------------------------------------
// https://wgld.org/d/webgl/w080.html
// --------------------------------------
class W80Renderer(ctx: Context): MgRenderer(ctx), SurfaceTexture.OnFrameAvailableListener {

    // 描画オブジェクト(立方体)
    private val modelCube = Cube01Model()
    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()
    // 描画オブジェクト(板ポリゴン)
    private val modelBoard = Board01Model()

    // VAO(立方体)
    private val vaoCube = ES32VAOIpct()
    // VAO(球体)
    private val vaoSphere = ES32VAOIpct()
    // VAO(板ポリゴン)
    private val vaoBoard = ES32VAOIpt()

    // シェーダ(メイン)
    private val shaderMain = W80ShaderMain(ctx)
    // シェーダ(ビデオ)
    private val shaderVideo = W80ShaderVideo(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // 正射影用座標変換行列
    private val matO = FloatArray(16)

    // プレイヤー
    private val player = MediaPlayer()

    private lateinit var surfaceTexture: SurfaceTexture
    private var updateSurface = false

    private val GL_TEXTURE_EXTERNAL_OES = 0x8D65

    //
    var u_difference = 0.5f

    init {
        // テクスチャ
        textures = IntArray(1)

        val afd = ctx.resources.openRawResourceFd(R.raw.w80_video)
        player.setDataSource(afd.fileDescriptor, afd.startOffset,afd.length)
        afd.close()
    }

    override fun onDrawFrame(gl: GL10?) {
        synchronized(this,{
            if (updateSurface) {
                surfaceTexture.updateTexImage()
                //surfaceTexture.getTransformMatrix()
                updateSurface = false
            }
        })

        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // テクスチャをバインドする
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textures[0])

        // フレームバッファを初期化
        GLES32.glClearColor(0f,0f,0f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,7f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,10f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // -------------------------------------------------------
        // 球体をレンダリング
        // -------------------------------------------------------
        Matrix.setIdentityM(matM,0)
        Matrix.translateM(matM,0,2f,0f,0f)
        Matrix.rotateM(matM,0,t0,1f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderMain.draw(vaoSphere,matMVP)

        // -------------------------------------------------------
        // 立方体をレンダリング
        // -------------------------------------------------------
        Matrix.setIdentityM(matM,0)
        Matrix.translateM(matM,0,-2f,0f,0f)
        Matrix.rotateM(matM,0,t0,1f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderMain.draw(vaoCube,matMVP)

        // -------------------------------------------------------
        // 板ポリゴンをレンダリング
        // -------------------------------------------------------
        shaderVideo.draw(vaoBoard,matO,0,u_difference)
    }

    @Synchronized override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        updateSurface = true
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // シェーダ(メイン)
        shaderMain.loadShader()
        // シェーダ(ビデオ)
        shaderVideo.loadShader()

        // モデル生成(立方体)
        modelCube.createPath()

        // モデル生成(球体)
        modelSphere.createPath(mapOf(
            "row"    to 32f,
            "column" to 32f,
            "radius" to 1.5f
        ))

        // モデル生成(板ポリゴン)
        modelBoard.createPath(mapOf(
            "pattern" to 53f
        ))

        // VAO(立方体)
        vaoCube.makeVIBO(modelCube)

        // VAO(球体)
        vaoSphere.makeVIBO(modelSphere)

        // VAO(板ポリゴン)
        vaoBoard.makeVIBO(modelBoard)

        // 正射影用座標変換行列
        Matrix.setLookAtM(matV, 0,
            0f,0f,0.5f,
            vecCenter[0], vecCenter[1], vecCenter[2],
            vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.orthoM(matP,0,-1f,1f,-1f,1f,0.1f,1f)
        Matrix.multiplyMM(matO,0,matP,0,matV,0)

        // テクスチャ作成し、idをtexturesに保存
        GLES32.glGenTextures(1,textures,0)
        //MyGLES32Func.createTexture(0,textures,bmpArray[0])
        GLES32.glBindTexture(GL_TEXTURE_EXTERNAL_OES,textures[0])
        MyGLES32Func.checkGlError("BindTexture")
        GLES32.glTexParameteri(GL_TEXTURE_EXTERNAL_OES,GLES32.GL_TEXTURE_MIN_FILTER,GLES32.GL_NEAREST)
        GLES32.glTexParameteri(GL_TEXTURE_EXTERNAL_OES,GLES32.GL_TEXTURE_MAG_FILTER,GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GL_TEXTURE_EXTERNAL_OES,GLES32.GL_TEXTURE_WRAP_S,GLES32.GL_CLAMP_TO_EDGE)
        GLES32.glTexParameteri(GL_TEXTURE_EXTERNAL_OES,GLES32.GL_TEXTURE_WRAP_T,GLES32.GL_CLAMP_TO_EDGE)

        surfaceTexture = SurfaceTexture(textures[0])
        surfaceTexture.setOnFrameAvailableListener(this)

        val surface = Surface(surfaceTexture)
        player.setSurface(surface)
        player.setScreenOnWhilePlaying(true)
        surface.release()

        player.prepare()

        synchronized( this ,{
            updateSurface = false
        })

        player.start()
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoCube.deleteVIBO()
        vaoSphere.deleteVIBO()
        vaoBoard.deleteVIBO()
        shaderMain.deleteShader()
        shaderVideo.deleteShader()
    }
}
