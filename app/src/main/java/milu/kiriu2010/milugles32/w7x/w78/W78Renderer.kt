package milu.kiriu2010.milugles32.w7x.w78

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.opengl.GLES32
import android.opengl.Matrix
import android.view.Surface
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d3.Cube01Model
import milu.kiriu2010.gui.model.d3.Sphere01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpct
import milu.kiriu2010.milugles32.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// --------------------------------------
// シェーダ(ビデオ)
// --------------------------------------
// https://wgld.org/d/webgl/w078.html
// --------------------------------------
class W78Renderer(ctx: Context): MgRenderer(ctx), SurfaceTexture.OnFrameAvailableListener {

    // 描画オブジェクト(立方体)
    private val modelCube = Cube01Model()
    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()

    // VAO(立方体)
    private val vaoCube = ES32VAOIpct()
    // VAO(球体)
    private val vaoSphere = ES32VAOIpct()

    // シェーダ
    private val shader = W78Shader(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // プレイヤー
    private val player = MediaPlayer()

    private lateinit var surfaceTexture: SurfaceTexture
    private var updateSurface = false

    private val GL_TEXTURE_EXTERNAL_OES = 0x8D65

    init {
        // テクスチャ
        textures = IntArray(1)

        /*
        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w26)
        bmpArray.add(bmp0)
        */

        val afd = ctx.resources.openRawResourceFd(R.raw.w78_video)
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
        GLES32.glClearColor(0f,0.7f,0.7f,1f)
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
        Matrix.translateM(matM,0,1.5f,0f,0f)
        Matrix.rotateM(matM,0,t0,1f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shader.draw(vaoSphere,matMVP,0)

        // -------------------------------------------------------
        // 立方体をレンダリング
        // -------------------------------------------------------
        Matrix.setIdentityM(matM,0)
        Matrix.translateM(matM,0,-1.5f,0f,0f)
        Matrix.rotateM(matM,0,t0,1f,1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shader.draw(vaoCube,matMVP,0)
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

        // シェーダ
        shader.loadShader()

        // モデル生成(立方体)
        modelCube.createPath(mapOf(
                "colorR"  to 1f,
                "colorG"  to 1f,
                "colorB"  to 1f,
                "colorA"  to 1f
        ))

        // モデル生成(球体)
        modelSphere.createPath(mapOf(
            "row"    to 32f,
            "column" to 32f,
            "radius" to 1f,
            "colorR"  to 1f,
            "colorG"  to 1f,
            "colorB"  to 1f,
            "colorA"  to 1f
        ))

        // VAO(立方体)
        vaoCube.makeVIBO(modelCube)

        // VAO(球体)
        vaoSphere.makeVIBO(modelSphere)

        // テクスチャ作成し、idをtexturesに保存
        GLES32.glGenTextures(1,textures,0)
        //MyGLES32Func.createTexture(0,textures,bmpArray[0])
        GLES32.glBindTexture(GL_TEXTURE_EXTERNAL_OES,textures[0])
        MyGLES32Func.checkGlError("BindTexture")
        GLES32.glTexParameteri(GL_TEXTURE_EXTERNAL_OES,GLES32.GL_TEXTURE_MIN_FILTER,GLES32.GL_NEAREST)
        GLES32.glTexParameteri(GL_TEXTURE_EXTERNAL_OES,GLES32.GL_TEXTURE_MAG_FILTER,GLES32.GL_LINEAR)

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
        shader.deleteShader()
    }
}
