package milu.kiriu2010.milugles32.w3x.w38

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import milu.kiriu2010.gui.basic.MyGLES32Func
import milu.kiriu2010.gui.model.d2.Board00Model
import milu.kiriu2010.gui.renderer.MgRenderer
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnct
import milu.kiriu2010.milugles32.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// ------------------------------------------------
// ステンシルバッファ
// ------------------------------------------------
// 基準値を保存するためのバッファとして機能する
// ------------------------------------------------
// https://wgld.org/d/webgl/w038.html
// ------------------------------------------------
class W38Renderer(ctx: Context): MgRenderer(ctx) {

    // 描画オブジェクト
    private val model = Board00Model()

    // VAO
    private val vao = ES32VAOIpnct()

    // シェーダ
    private val shader = W38Shader(ctx)

    // 画面縦横比
    var ratio: Float = 0f

    init {
        // テクスチャ
        textures = IntArray(1)

        // ビットマップをロード
        bmpArray.clear()
        val bmp0 = BitmapFactory.decodeResource(ctx.resources, R.drawable.texture_w38)
        bmpArray.add(bmp0)
    }

    override fun onDrawFrame(gl: GL10?) {
        // canvasを初期化
        GLES32.glClearColor(0.0f, 0.7f, 0.7f, 1.0f)
        GLES32.glClearDepthf(1f)
        // ステンシルバッファの基準値を0にする
        GLES32.glClearStencil(0)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_STENCIL_BUFFER_BIT)

        // 回転角度
        angle[0] =(angle[0]+1)%360
        //val t0 = angle[0].toFloat()

        // クォータニオンを行列に適用
        var matQ = qtnNow.toMatIV()

        // カメラの位置
        // ビュー座標変換行列
        Matrix.setLookAtM(matV, 0,
                vecEye[0], vecEye[1], vecEye[2],
                vecCenter[0], vecCenter[1], vecCenter[2],
                vecEyeUp[0], vecEyeUp[1], vecEyeUp[2])
        Matrix.perspectiveM(matP,0,45f,ratio,0.1f,100f)
        // ビュー座標変換行列にクォータニオンの回転を適用
        Matrix.multiplyMM(matV,0,matV,0,matQ,0)
        // ビュー×プロジェクション
        Matrix.multiplyMM(matVP,0,matP,0,matV,0)

        // テクスチャ0をバインド
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textures[0])

        // ステンシルテストを有効にする
        GLES32.glEnable(GLES32.GL_STENCIL_TEST)

        // モデル１をレンダリング
        // -------------------------------------------------------------
        // 常にステンシルテストをパスする
        // ステンシルテストをパスすると、
        // ステンシルバッファの対象ピクセルの基準値が１に書き換えられる
        // -------------------------------------------------------------
        // 0の補数(~0)を0.inv()でなく0xffにしてみる
        // -------------------------------------------------------------
        GLES32.glStencilFunc(GLES32.GL_ALWAYS,1, 0.inv() )
        GLES32.glStencilOp(GLES32.GL_KEEP, GLES32.GL_REPLACE, GLES32.GL_REPLACE)
        render(floatArrayOf(-0.25f,0.25f,-0.5f))

        // モデル２をレンダリング
        // -------------------------------------------------------------
        // 常にステンシルテストをパスする
        // ステンシルテストをパスすると、
        // ステンシルバッファの対象ピクセルの基準値がインクリメントされる
        // すなわち、２枚目のポリゴンがレンダリングされた時点で、
        // 双方のポリゴンが重なっている領域の基準値は２
        // 重なっていない部分の基準値は最大でも１にしかなりえない
        // -------------------------------------------------------------
        // 0の補数(~0)を0.inv()でなく0xffにしてみる
        // -------------------------------------------------------------
        GLES32.glStencilFunc(GLES32.GL_ALWAYS,0, 0.inv() )
        GLES32.glStencilOp(GLES32.GL_KEEP, GLES32.GL_INCR, GLES32.GL_INCR)
        render(floatArrayOf(0f,0f,0f))

        // モデル３をレンダリング
        // -------------------------------------------------------------
        // ステンシルバッファの基準値２と同じものしか
        // ステンシルテストをパスしない
        // １枚目と２枚目が重なり合っている領域が基準値２の領域なので、
        // ３枚目のポリゴンは２つのポリゴンが重なっている
        // 領域しかレンダリングされない
        // -------------------------------------------------------------
        // 0の補数(~0)を0.inv()でなく0xffにしてみる
        // -------------------------------------------------------------
        GLES32.glStencilFunc(GLES32.GL_EQUAL,2, 0.inv() )
        GLES32.glStencilOp(GLES32.GL_KEEP, GLES32.GL_KEEP, GLES32.GL_KEEP)
        render(floatArrayOf(0.25f,-0.25f,0.5f))

        // ステンシルテストを無効にする
        GLES32.glDisable(GLES32.GL_STENCIL_TEST)
    }

    // モデルをレンダリング
    private fun render(tr: FloatArray) {
        Matrix.setIdentityM(matM,0)
        Matrix.translateM(matM,0,tr[0],tr[1],tr[2])
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        Matrix.invertM(matI,0,matM,0)
        shader.draw(vao,matMVP,matI,vecLight,0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)

        ratio = width.toFloat()/height.toFloat()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // ステンシルバッファに設定できる最大値をコンソールに出力
        // Huawei P20Lite: 8
        val stencilBufSize = IntArray(1)
        GLES32.glGetIntegerv(GLES32.GL_STENCIL_BITS,stencilBufSize,0)
        Log.d(javaClass.simpleName,"stencilBufSize:${stencilBufSize[0]}")

        // カリングと深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)

        // シェーダ
        shader.loadShader()

        // モデル生成
        model.createPath(mapOf("pattern" to 29f))

        // VAO
        vao.makeVIBO(model)

        // テクスチャ作成し、idをtexturesに保存
        GLES32.glGenTextures(1,textures,0)
        MyGLES32Func.checkGlError("glGenTextures")

        // テクスチャ0をバインド
        MyGLES32Func.createTexture(0,textures,bmpArray[0])

        // 光源位置
        vecLight[0] = 1f
        vecLight[1] = 1f
        vecLight[2] = 1f

        // 視点位置
        vecEye[0] = 0f
        vecEye[1] = 0f
        vecEye[2] = 5f
    }

    override fun setMotionParam(motionParam: MutableMap<String, Float>) {
    }

    override fun closeShader() {
        vao.deleteVIBO()
        shader.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
    }
}