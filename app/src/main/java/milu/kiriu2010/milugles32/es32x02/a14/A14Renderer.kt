package milu.kiriu2010.milugles32.es32x02.a14

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d2.Image01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VBOIpc
import milu.kiriu2010.milugles32.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------------------------
// Transform Feedback
// -----------------------------------------------------------
//
// -----------------------------------------------------------
// https://wgld.org/d/webgl2/w014.html
// -----------------------------------------------------------
class A14Renderer(ctx: Context): MgRenderer(ctx) {
    // 描画オブジェクト(画像モデル)
    private val modelImg = Image01Model()
    // 描画オブジェクト(Feedback)
    private val modelFeedback = Image01Model()

    // VBO(画像モデル)
    private val vboImg = ES32VBOIpc()
    // VBO(Feedback)
    private val vboFeedback = ES32VBOIpc()

    // シェーダA
    private val shaderA = ES32a14ShaderA(ctx)
    // シェーダB
    private val shaderB = ES32a14ShaderB(ctx)

    // 画面縦横比
    var ratio: Float = 1f

    // ビットマップの大きさ
    var bmpSize = 0

    val s_time = System.currentTimeMillis()

    val hTransformFeedback = IntArray(1)

    init {
        // テクスチャ
        textures = IntArray(1)

        // https://stackoverflow.com/questions/8855036/incorrect-image-dimensions-in-android-when-using-bitmap
        // http://y-anz-m.blogspot.com/2012/08/android.html
        val options = BitmapFactory.Options()
        options.inScaled = false

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_lenna_a03,options)
        bmpArray.add(bmp0)
    }

    override fun onDrawFrame(gl: GL10?) {
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        val u_time = (System.currentTimeMillis()-s_time).toFloat()*0.001f
        // 0-1の範囲に正規化
        val u_mouse = floatArrayOf(touchP.x/renderW*2f-1f,-(touchP.y/renderH*2f-1f))

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,5f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,60f,0.5f,0.1f,20f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // モデルをレンダリング(画像)
        shaderA.draw(vboImg,vboFeedback,u_time,u_mouse,bmpSize)

        // フレームバッファを初期化
        GLES32.glClearColor(0f,0f,0f,1f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // モデルをレンダリング(feedback)
        shaderB.draw(vboFeedback,matVP,bmpSize)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        // 初期位置はスクリーン中央とする
        touchP.x = renderW*0.5f
        touchP.y = renderH*0.5f

        bmpSize = bmpArray[0].width*bmpArray[0].height

        // 768
        // 256
        Log.d(javaClass.simpleName,"bmp:width    :"+bmpArray[0].width)
        // 768
        // 256
        Log.d(javaClass.simpleName,"bmp:height   :"+bmpArray[0].height)
        // 589824
        // 65536
        Log.d(javaClass.simpleName,"bmp:w*h      :"+bmpSize)
        // 3072
        // 1024
        Log.d(javaClass.simpleName,"bmp:rowBytes :"+bmpArray[0].rowBytes)
        // 262144
        Log.d(javaClass.simpleName,"bmp:byteCount:"+bmpArray[0].byteCount)

        // モデル生成(画像)
        modelImg.createPath(bmpArray[0],false)
        // モデル生成(Feedback)
        modelFeedback.createPath(bmpArray[0],true)

        // VBO(画像)
        vboImg.makeVIBO(modelImg)
        // VBO(Feedback)
        vboFeedback.usagePos = GLES32.GL_DYNAMIC_COPY
        vboFeedback.usageCol = GLES32.GL_DYNAMIC_COPY
        vboFeedback.makeVIBO(modelFeedback)

        // テクスチャを作成
        GLES32.glGenTextures(1,textures,0)
        // テクスチャに使う画像をロード
        MyGLES32Func.createTexture(0,textures,bmpArray[0])

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // カリングと深度テストを無効にする
        GLES32.glDisable(GLES32.GL_DEPTH_TEST)
        GLES32.glDisable(GLES32.GL_CULL_FACE)
        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFuncSeparate(GLES32.GL_SRC_ALPHA,GLES32.GL_ONE,GLES32.GL_ONE,GLES32.GL_ONE)
        GLES32.glDisable(GLES32.GL_RASTERIZER_DISCARD)


        GLES32.glGenTransformFeedbacks(1,hTransformFeedback,0)
        GLES32.glBindTransformFeedback(GLES32.GL_TRANSFORM_FEEDBACK,hTransformFeedback[0])

        // シェーダA
        shaderA.loadShader()

        // シェーダB
        shaderB.loadShader()

        // ライトの向き
        vecLight[0] = 5f
        vecLight[1] = 2f
        vecLight[2] = 5f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vboImg.deleteVIBO()
        vboFeedback.deleteVIBO()
        shaderA.deleteShader()
        shaderB.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
    }
}