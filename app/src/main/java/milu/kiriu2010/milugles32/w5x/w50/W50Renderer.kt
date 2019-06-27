package milu.kiriu2010.milugles32.w5x.w50

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.color.MgColor
import milu.kiriu2010.gui.model.Cube01Model
import milu.kiriu2010.gui.model.Torus01Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import milu.kiriu2010.milugles32.R
import java.nio.ByteBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// -----------------------------------------------------------------------------
// 光学迷彩
// -----------------------------------------------------------------------------
// 奥にあるモデルが透けて見えるようにするので、フレームバッファを使う
// モデルに背景を投影するので、射影テクスチャマッピングを使う
// 射影テクスチャマッピングだけでは、モデルが背景に完全に溶け込んでしまうので、
// 投影させるテクスチャの参照座標をモデルの法線を使って少しずつずらす
// -----------------------------------------------------------------------------
// https://wgld.org/d/webgl/w050.html
// -----------------------------------------------------------------------------
class W50Renderer(ctx: Context): MgRenderer(ctx) {

    // 描画オブジェクト(立方体)
    private val modelCube = Cube01Model()
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()

    // VAO(立方体)
    private val vaoCube = ES32VAOIpnc()
    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpnc()

    // シェーダ(光学迷彩)
    private val shaderStealth = W50ShaderStealth(ctx)
    // シェーダ(反射光)
    private val shaderSpecular = W50ShaderSpecular(ctx)
    // シェーダ(キューブ環境マッピング)
    private val shaderCubeMap = W50ShaderCubeMap(ctx)

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

    // ---------------------------------------------
    // ライトを視点とみなした場合の変換行列として使う
    // ---------------------------------------------
    // テクスチャ座標変換行列
    private val matTex = FloatArray(16)
    // ビュー×プロジェクション×テクスチャ座標変換行列
    private val matVPT = FloatArray(16)

    // 光学迷彩にかける補正係数
    //   -1.0 ～ 1.0
    var k = 1f

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
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.cube_w44_px)
        val bmp1 = BitmapFactory.decodeResource(ctx.resources, R.drawable.cube_w44_py)
        val bmp2 = BitmapFactory.decodeResource(ctx.resources, R.drawable.cube_w44_pz)
        val bmp3 = BitmapFactory.decodeResource(ctx.resources, R.drawable.cube_w44_nx)
        val bmp4 = BitmapFactory.decodeResource(ctx.resources, R.drawable.cube_w44_ny)
        val bmp5 = BitmapFactory.decodeResource(ctx.resources, R.drawable.cube_w44_nz)
        bmpArray.add(bmp0)
        bmpArray.add(bmp1)
        bmpArray.add(bmp2)
        bmpArray.add(bmp3)
        bmpArray.add(bmp4)
        bmpArray.add(bmp5)

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
        // 回転角度
        angle[0] =(angle[0]+1)%360
        val t1 = angle[0].toFloat()

        // フレームバッファをバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファを初期化
        GLES32.glClearColor(0.0f, 0.7f, 0.7f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // ビュー×プロジェクション座標変換行列
        vecEye = qtnNow.toVecIII(floatArrayOf(0f,0f,20f))
        vecEyeUp = qtnNow.toVecIII(floatArrayOf(0f,1f,0f))
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,90f,ratio,0.1f,200f)
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // キューブマップテクスチャで背景用キューブをレンダリング
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_CUBE_MAP,textures[0])
        Matrix.setIdentityM(matM,0)
        Matrix.scaleM(matM,0,100f,100f,100f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderCubeMap.draw(vaoCube,matM,matMVP,floatArrayOf(0f,0f,0f),0,0)

        // スペキュラライティングシェーダでトーラスモデルをレンダリング
        (0..8).forEach {  i ->
            val t = i.toFloat() * 360f/9f
            var amb = MgColor.hsva(i*40,1f,1f,1f).toFloatArray()
            Matrix.setIdentityM(matM,0)
            Matrix.rotateM(matM,0,t,0f,1f,0f)
            Matrix.translateM(matM,0,0f,0f,30f)
            Matrix.rotateM(matM,0,t1,1f,1f,0f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            Matrix.invertM(matI,0,matM,0)
            shaderSpecular.draw(vaoTorus,matMVP,matI,vecLight,vecEye,amb)
        }

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // canvasを初期化
        GLES32.glClearColor(0.0f, 0.7f, 0.7f, 1.0f)
        GLES32.glClearDepthf(1f)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // キューブマップテクスチャで背景用キューブをレンダリング
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_CUBE_MAP,textures[0])
        Matrix.setIdentityM(matM,0)
        Matrix.scaleM(matM,0,100f,100f,100f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderCubeMap.draw(vaoCube,matM,matMVP, floatArrayOf(0f,0f,0f),0,0)

        // スペキュラライティングシェーダでトーラスモデルをレンダリング
        (0..8).forEach {  i ->
            val t = i.toFloat() * 360f/9f
            var amb = MgColor.hsva(i*40,1f,1f,1f).toFloatArray()
            Matrix.setIdentityM(matM,0)
            Matrix.rotateM(matM,0,t,0f,1f,0f)
            Matrix.translateM(matM,0,0f,0f,30f)
            Matrix.rotateM(matM,0,t1,1f,1f,0f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            Matrix.invertM(matI,0,matM,0)
            shaderSpecular.draw(vaoTorus,matMVP,matI,vecLight,vecEye,amb)
        }

        // 行列を掛け合わせる
        val matPT = FloatArray(16)
        Matrix.multiplyMM(matPT,0,matTex,0,matP,0)
        Matrix.multiplyMM(matVPT,0,matPT,0,matV,0)

        // フレームバッファテクスチャをバインド
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,frameTex[0])

        // 光学迷彩でトーラスモデルを原点位置にレンダリング
        Matrix.setIdentityM(matM,0)
        Matrix.rotateM(matM,0,t1,1f,0f,1f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderStealth.draw(vaoTorus,matM,matVPT,matMVP, k, 0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()

        renderW = width
        renderH = height

        // フレームバッファ生成
        GLES32.glGenFramebuffers(1,frameBuf)
        // 深度バッファ用レンダ―バッファ生成
        GLES32.glGenRenderbuffers(1,depthRenderBuf)
        // フレームバッファ用テクスチャ生成
        GLES32.glGenTextures(1,frameTex)
        MyGLES32Func.createFrameBuffer(renderW,renderH,0,frameBuf,depthRenderBuf,frameTex)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)

        // シェーダ(光学迷彩)
        shaderStealth.loadShader()
        // シェーダ(反射光)
        shaderSpecular.loadShader()
        // シェーダ(キューブ環境マッピング)
        shaderCubeMap.loadShader()

        // キューブマップを生成
        generateCubeMap()

        // モデル生成(立方体)
        modelCube.createPath(mapOf(
                "pattern" to 2f,
                "scale"   to 2f,
                "colorR"  to 1f,
                "colorG"  to 1f,
                "colorB"  to 1f,
                "colorA"  to 1f
        ))

        // モデル生成(トーラス)
        modelTorus.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 2.5f,
                "oradius" to 5f,
                "colorR"  to 1f,
                "colorG"  to 1f,
                "colorB"  to 1f,
                "colorA"  to 1f
        ))

        // VAO(立方体)
        vaoCube.makeVIBO(modelCube)

        // VAO(トーラス)
        vaoTorus.makeVIBO(modelTorus)

        // 光源位置(反射光によるライティングで利用)
        vecLight[0] = -0.577f
        vecLight[1] =  0.577f
        vecLight[2] =  0.577f
    }

    private fun generateCubeMap() {
        // テクスチャ作成し、idをtexturesに保存
        GLES32.glGenTextures(1,textures,0)
        MyGLES32Func.checkGlError("glGenTextures")

        // テクスチャをキューブマップにバインド
        GLES32.glBindTexture(GLES32.GL_TEXTURE_CUBE_MAP,textures[0])

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
        vaoTorus.deleteVIBO()
        shaderStealth.deleteShader()
        shaderSpecular.deleteShader()
        shaderCubeMap.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}