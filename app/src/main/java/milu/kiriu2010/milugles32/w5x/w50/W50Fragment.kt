package milu.kiriu2010.milugles32.w5x.w50

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
class W50Fragment : androidx.fragment.app.Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w50, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW50)
        val renderer = W50Renderer(context!!)
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

        val seekBarW50 = view.findViewById<SeekBar>(R.id.seekBarW50)
        seekBarW50.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.k = (seekBar.progress-5).toFloat()/5f
                Log.d(javaClass.simpleName,"k[${renderer.k}]")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.k = (seekBar.progress-5).toFloat()/5f
                Log.d(javaClass.simpleName,"k[${renderer.k}]")
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
                W50Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
