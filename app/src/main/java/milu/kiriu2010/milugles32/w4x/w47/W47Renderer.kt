package milu.kiriu2010.milugles32.w4x.w47

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
import milu.kiriu2010.gui.vbo.es32.ES32VAOIpnc
import milu.kiriu2010.milugles32.R
import java.nio.ByteBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// --------------------------------------
// 動的キューブマッピング
// --------------------------------------
// https://wgld.org/d/webgl/w047.html
// --------------------------------------
// キューブマップのシェーダで
// UseProgramで0x502が発生する
// トーラスが写らない
// --------------------------------------
class W47Renderer(ctx: Context): MgRenderer(ctx) {

    // 描画オブジェクト(立方体)
    private val modelCube = Cube01Model()
    // 描画オブジェクト(球体)
    private val modelSphere = Sphere01Model()
    // 描画オブジェクト(トーラス)
    private val modelTorus = Torus01Model()

    // VAO(立方体)
    private val vaoCube = ES32VAOIpnc()
    // VAO(球体)
    private val vaoSphere = ES32VAOIpnc()
    // VAO(トーラス)
    private val vaoTorus = ES32VAOIpnc()

    // シェーダ(反射光ライティング)
    private val shaderSpecular = W47ShaderSpecular(ctx)
    // シェーダ(キューブマッピング)
    private val shaderCubeMap = W47ShaderCubeMap(ctx)

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

    // カメラが見る方向を表すベクトル
    val camDir = FloatArray(3*6)
    // カメラの上方向を表すベクトル
    val camUp = FloatArray(3*6)
    // トーラスの位置
    val torusPos = FloatArray(3*6)
    // トーラスの色
    val torusCol = FloatArray(4*6)

    init {
        // テクスチャ配列
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

        // GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_X
        // 右:赤
        camDir[0] = 1f;  camDir[1] =  0f;  camDir[2] = 0f;
        camUp[0]  = 0f;  camUp[1]  = -1f;  camUp[2]  = 0f;
        torusPos[0] = 6f; torusPos[1] = 0f; torusPos[2]  = 0f;
        torusCol[0] = 1f; torusCol[1] = 0.5f; torusCol[2] = 0.5f; torusCol[3] = 1f;

        // GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_Y
        // 上:緑
        camDir[3] = 0f;  camDir[4] = 1f;   camDir[5] = 0f;
        camUp[3]  = 0f;  camUp[4]  = 0f;   camUp[5]  = 1f;
        torusPos[3] = 0f;   torusPos[4] = 6f; torusPos[5]   = 0f;
        torusCol[4] = 0.5f; torusCol[5] = 1f; torusCol[6] = 0.5f; torusCol[7] = 1f;

        // GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_Z
        // 前:青
        camDir[6] = 0f; camDir[7] = 0f;  camDir[8] = 1f;
        camUp[6]  = 0f; camUp[7]  = -1f; camUp[8]  = 0f;
        torusPos[6] = 0f;   torusPos[7] = 0f;   torusPos[8]  = 6f;
        torusCol[8] = 0.5f; torusCol[9] = 0.5f; torusCol[10] = 1f; torusCol[11] = 1f;

        // GLES32.GL_TEXTURE_CUBE_MAP_NEGATIVE_X
        // 左:濃い赤
        camDir[9] = -1f; camDir[10] = 0f; camDir[11] = 0f;
        camUp[9]  = 0f;  camUp[10] = -1f; camUp[11] = 0f;
        torusPos[9] = -6f; torusPos[10] = 0f; torusPos[11] = 0f;
        torusCol[12] = 0.5f; torusCol[13] = 0f; torusCol[14] = 0f; torusCol[15] = 1f;

        // GLES32.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y
        // 下:濃い緑
        camDir[12] = 0f; camDir[13] = -1f; camDir[14] = 0f;
        camUp[12]  = 0f; camUp[13] = 0f;   camUp[14] = -1f;
        torusPos[12] = 0f; torusPos[13] = -6f;  torusPos[14] = 0f;
        torusCol[16] = 0f; torusCol[17] = 0.5f; torusCol[18] = 0f; torusCol[19] = 1f;

        // GLES32.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
        // 奥:濃い青
        camDir[15] = 0f; camDir[16] = 0f; camDir[17] = -1f;
        camUp[15] = 0f;  camUp[16] = -1f; camUp[17] = 0f;
        torusPos[15] = 0f; torusPos[16] = 0f; torusPos[17] = -6f;
        torusCol[20] = 0f; torusCol[21] = 0f; torusCol[22] = 0.5f; torusCol[23] = 1f;
    }

    override fun onDrawFrame(gl: GL10?) {
        // 回転角度
        angle[0] =(angle[0]+1)%360
        val t0 = angle[0].toFloat()

        // フレームバッファをバインド
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,frameBuf[0])

        // フレームバッファへの６方向レンダリング
        render2FrameBuffer(t0)

        // フレームバッファのバインドを解除
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER,0)

        // canvasを初期化
        GLES32.glClearColor(0.0f, 1.0f, 0.0f, 1.0f)
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

        // 背景用キューブをレンダリング
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_CUBE_MAP,textures[0])
        Matrix.setIdentityM(matM,0)
        Matrix.scaleM(matM,0,100f,100f,100f)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderCubeMap.draw(vaoCube,matM,matMVP,vecEye,0,0,"Default:Cube")

        // 動的キューブマップテクスチャを適用して球体をレンダリング
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_CUBE_MAP,frameTex[0])
        Matrix.setIdentityM(matM,0)
        Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
        shaderCubeMap.draw(vaoSphere,matM,matMVP,vecEye,0,1,"Default:Sphere")

        // スペキュラライティングシェーダを使って
        // トーラスをレンダリング
        targetArray.forEachIndexed { id, target ->
            Matrix.setIdentityM(matM,0)
            Matrix.translateM(matM,0,torusPos[0+id*3],torusPos[1+id*3],torusPos[2+id*3])
            Matrix.rotateM(matM,0,t0,camDir[0+id*3],camDir[1+id*3],camDir[2+id*3])
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            Matrix.invertM(matI,0,matM,0)

            val amb = FloatArray(4)
            amb[0] = torusCol[0+id*4]
            amb[1] = torusCol[1+id*4]
            amb[2] = torusCol[2+id*4]
            amb[3] = torusCol[3+id*4]
            shaderSpecular.draw(vaoTorus,matMVP,matI,vecLight,vecEye,amb,"Default:Torus")
        }
    }

    // フレームバッファへの６方向レンダリング
    private fun render2FrameBuffer(t1:Float) {
        targetArray.forEachIndexed { id, target ->
            // フレームバッファにテクスチャを関連付ける
            GLES32.glFramebufferTexture2D(GLES32.GL_FRAMEBUFFER,GLES32.GL_COLOR_ATTACHMENT0,target,frameTex[0],0)

            // フレームバッファを初期化
            GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            GLES32.glClearDepthf(1f)
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

            // ライトベクトル
            val lightDirection = floatArrayOf(-1f,1f,1f)

            // -------------------------------------------------------
            // キューブマップテクスチャは、
            // 原点から６方向を継ぎ目なくつながる形で撮影する
            // ・カメラを原点におく
            // ・カメラの上方向を撮影方向に応じて適切に設定
            // ・アスペクト比が1.0で画角を90度にする
            // -------------------------------------------------------


            // カメラからみた
            // ビュー×プロジェクション座標変換行列
            Matrix.setLookAtM(matV,0,
                    0f,0f,0f,
                    camDir[0+id*3], camDir[1+id*3], camDir[2+id*3],
                    camUp[0+id*3], camUp[1+id*3], camUp[2+id*3]
            )
            Matrix.perspectiveM(matP,0,90f,1f,0.1f,200f)
            Matrix.multiplyMM(matVP,0,matP,0,matV,0)

            // キューブマップテクスチャで背景用キューブをレンダリング
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_CUBE_MAP, textures[0])
            Matrix.setIdentityM(matM,0)
            Matrix.scaleM(matM,0,100f,100f,100f)
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            shaderCubeMap.draw(vaoCube,matM,matMVP, floatArrayOf(0f,0f,0f),0,0,"FrameBuffer:Cube")

            // 視線ベクトルの変換
            // 原点から見たときの正しい照明効果を得るために行っている
            // シェーダへ送る視線ベクトルとして使っている
            var torusInvEye = FloatArray(3)
            torusInvEye[0] = -camDir[0+id*3]
            torusInvEye[1] = -camDir[1+id*3]
            torusInvEye[2] = -camDir[2+id*3]

            // 環境色
            var amb = FloatArray(4)
            amb[0] = torusCol[0+id*4]
            amb[1] = torusCol[1+id*4]
            amb[2] = torusCol[2+id*4]
            amb[3] = torusCol[3+id*4]

            // スペキュラライティングシェーダでトーラスをレンダリング
            Matrix.setIdentityM(matM,0)
            Matrix.translateM(matM,0,torusPos[0+id*3],torusPos[1+id*3],torusPos[2+id*3])
            Matrix.rotateM(matM,0,t1,camDir[0+id*3],camDir[1+id*3],camDir[2+id*3])
            Matrix.multiplyMM(matMVP,0,matVP,0,matM,0)
            Matrix.invertM(matI,0,matM,0)
            shaderSpecular.draw(vaoTorus,matMVP,matI,lightDirection,torusInvEye,amb,"FrameBuffer:Torus")
        }
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
        // キューブマップ用のフレームバッファを生成
        MyGLES32Func.createFrameBuffer4CubeMap(renderW,renderH,0,frameBuf,depthRenderBuf,frameTex)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 深度テストを有効にする
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)

        // シェーダ(反射光ライティング)
        shaderSpecular.loadShader()

        // シェーダ(キューブ環境マッピング)
        shaderCubeMap.loadShader()

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
                "radius" to 3f,
                "colorR" to 1f,
                "colorG" to 1f,
                "colorB" to 1f,
                "colorA" to 1f
        ))

        // モデル生成(トーラス)
        modelTorus.createPath(mapOf(
                "row"     to 32f,
                "column"  to 32f,
                "iradius" to 0.5f,
                "oradius" to 1f,
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

        // キューブマップを生成
        generateCubeMap()

        // 光源位置
        vecLight[0] = -1f
        vecLight[1] =  1f
        vecLight[2] =  1f

        // 視点位置
        vecEye[0] =  0f
        vecEye[1] =  0f
        vecEye[2] = 20f
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
        vaoSphere.deleteVIBO()
        vaoTorus.deleteVIBO()
        shaderSpecular.deleteShader()
        shaderCubeMap.deleteShader()

        GLES32.glDeleteTextures(textures.size,textures,0)
        GLES32.glDeleteTextures(frameTex.capacity(),frameTex)
        GLES32.glDeleteRenderbuffers(depthRenderBuf.capacity(),depthRenderBuf)
        GLES32.glDeleteFramebuffers(frameBuf.capacity(),frameBuf)
    }
}