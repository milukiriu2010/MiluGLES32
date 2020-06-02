package milu.kiriu2010.milugles32.w4x.w45

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
class W45Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w43, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW43)
        val renderer = W45Renderer(context!!)
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
                W45Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
