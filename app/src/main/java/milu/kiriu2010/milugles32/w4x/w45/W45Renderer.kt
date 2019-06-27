package milu.kiriu2010.milugles32.w4x.w45

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.Cube01Model
import milu.kiriu2010.gui.model.Sphere01Model
import milu.kiriu2010.gui.model.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnct
import milu.kiriu2010.milugles32.R
import java.nio.ByteBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// ----------------------------------------------------------------------------------
// キューブ環境バンプマッピング
// ----------------------------------------------------------------------------------
// バンプマッピングでは、法線マップの参照を行う上で"接空間"での計算を行う必要がある。
// 一方、キューブマップ環境では"視線空間"での計算を行う必要がある。
// この２つの空間上での変換をいかにして行うのかが、キューブ環境マッピングの肝
// ----------------------------------------------------------------------------------
// https://wgld.org/d/webgl/w045.html
// http://opengles2learning.blogspot.com/2011/06/texturing-cube-different-textures-on.html
// ----------------------------------------------------------------------------------
class W45Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(立方体)
    private val modelCube = Cube01Model()
    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()

    // VAO(立方体)
    private val vaoCube = ES32VAOIpnct()
    // VAO(球体)
    private val vaoSphere = ES32VAOIpnct()
    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpnct()

    // シェーダ
    private val shader = W45Shader(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    // キューブマップ用のターゲットを格納する配列
    val targetArray = arrayListOf<Int>(
            GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
            GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
            GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
            GLES32.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
            GLES32.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
            GLES32.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
    )

    // テクスチャ配列
    val normalTextures = IntArray(2)
    val cubeTextures = IntArray(2)

    init {
        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.cube_w44_px)
        val bmp1 = BitmapFactory.decodeResource(ctx.resources, R.drawable.cube_w44_py)
        val bmp2 = BitmapFactory.decodeResource(ctx.resources, R.drawable.cube_w44_pz)
        val bmp3 = BitmapFactory.decodeResource(ctx.resources, R.drawable.cube_w44_nx)
        val bmp4 = BitmapFactory.decodeResource(ctx.resources, R.drawable.cube_w44_ny)
        val bmp5 = BitmapFactory.decodeResource(ctx.resources, R.drawable.cube_w44_nz)
        val bmp6 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w45)
        bmpArray.add(bmp0)
        bmpArray.add(bmp1)
        bmpArray.add(bmp2)
        bmpArray.add(bmp3)
        bmpArray.add(bmp4)
        bmpArray.add(bmp5)
        bmpArray.add(bmp6)
    }

    override fun onDrawFrame(gl: GL10?) {
        // 回転角度
        angle[0] =(angle[0]+1)%360
        angle[1] =(angle[0]+180)%360
        val t0 = angle[0].toFloat()
        val t1 = angle[1].toFloat()

        // canvasを初期化
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,20f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,200f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // 法線マップテクスチャ
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,normalTextures[0])

        // キューブマップテクスチャ
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_CUBE_MAP,cubeTextures[0])

        // 背景用キューブをレンダリング
        Matrix.setIdentityM(matM,0)
        Matrix.scaleM(matM,0,100f,100f,100f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shader.draw(vaoCube,matM,matMVP,vecEye,0,1,0)

        // 球体をレンダリング
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t0,0f,0f,1f)
        Matrix.translateM(matM,0,5f,0f,0f)
        Matrix.rotateM(matM,0,t0,0f,-1f,0f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shader.draw(vaoSphere,matM,matMVP,vecEye,0,1,1)

        // トーラスをレンダリング
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t1,0f,0f,1f)
        Matrix.translateM(matM,0,5f,0f,0f)
        Matrix.rotateM(matM,0,t1,1f,-1f,1f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shader.draw(vaoTorus,matM,matMVP,vecEye,0,1,1)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)

        // シェーダプログラム登録
        shader.loadShader()

        // モデル生成(立方体)
        modelCube.createPath(mapOf(
                "pattern" to 2f,
                "scale"   to 2f,
                "colorR"  to 1f,
                "colorG"  to 1f,
                "colorB"  to 1f,
                "colorA"  to 1f
        ))

        // モデル生成(球体)
        modelSphere.createPath(mapOf(
                "row"    to 32f,
                "column" to 32f,
                "radius" to 2.5f,
                "colorR" to 1f,
                "colorG" to 1f,
                "colorB" to 1f,
                "colorA" to 1f
        ))

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

        // VAO(立方体)
        vaoCube.makeVIBO(modelCube)

        // VAO(球体)
        vaoSphere.makeVIBO(modelSphere)

        // VAO(トーラス)
        vaoTorus.makeVIBO(modelTorus)

        // 法線マップテクスチャを生成
        GLES32.glGenTextures(1,normalTextures,0)
        MyGLES32Func.createTexture(0,normalTextures,bmpArray[6])

        // キューブマップを生成
        generateCubeMap()
    }

    // キューブマッピング用テクスチャ
    private fun generateCubeMap() {
        // テクスチャ作成し、idをcubeTexturesに保存
        GLES32.glGenTextures(1,cubeTextures,0)
        MyGLES32Func.checkGlError("glGenTextures")

        // テクスチャをキューブマップにバインド
        GLES32.glBindTexture(GLES32.GL_TEXTURE_CUBE_MAP,cubeTextures[0])

        // テクスチャへimageを適用
        (0..5).forEach { id ->
            val bitmap = bmpArray[id]
            val bw = bitmap.width
            val bh = bitmap.height
            val buffer = ByteBuffer.allocateDirect(bw*bh*4)
            bitmap.copyPixelsToBuffer(buffer)
            buffer.position(0)

            GLES32.glTexImage2D(targetArray[id],0,GLES32.GL_RGBA,
                    bw,bh,0,GLES32.GL_RGBA,
                    GLES32.GL_UNSIGNED_BYTE,buffer)
            if ( bitmap.isRecycled == false ) {
                bitmap.recycle()
            }
        }

        // ミニマップを生成
        GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_CUBE_MAP)

        // テクスチャのパラメータを設定
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_CUBE_MAP, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_CUBE_MAP, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_CUBE_MAP, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_CUBE_MAP, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE)

        // テクスチャのバインド無効化
        GLES32.glBindTexture(GLES32.GL_TEXTURE_CUBE_MAP,0)
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vaoCube.deleteVIBO()
        vaoSphere.deleteVIBO()
        vaoTorus.deleteVIBO()
        shader.deleteShader()

        GLES32.glDeleteTextures(normalTextures.size,normalTextures,0)
        GLES32.glDeleteTextures(cubeTextures.size,cubeTextures,0)
    }
}