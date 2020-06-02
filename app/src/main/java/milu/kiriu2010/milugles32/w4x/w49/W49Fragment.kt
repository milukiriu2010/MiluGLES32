package milu.kiriu2010.milugles32.w4x.w49

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

// ------------------------------------------------------------------------------
// 射影テクスチャマッピング
// ------------------------------------------------------------------------------
// テクスチャをまるでスクリーンに投影するかのようにマッピングする
// モデルの影を投影したり、モデルに光学迷彩がかかったように処理できる
// ------------------------------------------------------------------------------
// テクスチャという２次元データを射影変換することで３次元空間上に投影する
// 画像データの原点が左上になるのに対し、テクスチャの原点が左下となることに注意

// またテクスチャ空間は0～1の範囲で座標を表し、原点は左下。
// プロジェクション変換を行う射影空間では、座標の範囲は-1～1で、原点は空間の中心
//
// ・イメージが上下反転してしまう問題
// ・テクスチャ空間と射影空間で座標系が異なる問題
// を対処するために,テクスチャ座標系への変換行列を作成する
//   http://asura.iaigiri.com/OpenGL/gl45.html
// ------------------------------------------------------------------------------
// 物体のローカル座標系からテクスチャ座標系への変換
//
//   [テクスチャ座標変換行列]
//   ×[ライトから見たときの射影行列]
//   ×[ライトから見たときのビュー行列]
//   ×[ワールド行列]
//   ×[ローカル座標系の頂点座標]
// ------------------------------------------------------------------------------
// https://wgld.org/d/webgl/w049.html
// ------------------------------------------------------------------------------
class W49Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w49, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW49)
        val renderer = W49Renderer(context!!)
        myGLES32View.setRenderer(renderer)
        myGLES32View.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    renderer.isRunning = false
                }
                MotionEvent.ACTION_DOWN -> {
                    Log.d(javaClass.simpleName,"ex[${event.x}]ey[${event.y}]")
                    Log.d(javaClass.simpleName,"vw[${myGLES32View.width}]vh[${myGLES32View.height}]")
                    renderer.isRunning = true
                    renderer.receiveTouch(event,myGLES32View.width,myGLES32View.height)
                    myGLES32View.performClick()
                }
                MotionEvent.ACTION_MOVE -> {
                    renderer.receiveTouch(event,myGLES32View.width,myGLES32View.height)
                }
                else -> {
                }
            }
            true
        }
        val seekBarW49 = view.findViewById<SeekBar>(R.id.seekBarW49)
        seekBarW49.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.k = seekBar.progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.k = seekBar.progress.toFloat()
            }

        })

        return view
    }

    override fun onResume() {
        super.onResume()
        myGLES32View.onResume()
    }

    override fun onPause() {
        super.onPause()
        myGLES32View.onPause()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                W49Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
