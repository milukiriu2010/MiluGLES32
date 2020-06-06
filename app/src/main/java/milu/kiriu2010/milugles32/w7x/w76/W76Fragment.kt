package milu.kiriu2010.milugles32.w7x.w76

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*


import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

// --------------------------------------
// ハーフトーンシェーディング
// --------------------------------------
// どっとを用いて陰影を表現する
// --------------------------------------
// https://wgld.org/d/webgl/w076.html
// --------------------------------------
class W76Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w76, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW76)
        val renderer = W76Renderer(context!!)
        myGLES32View.setRenderer(renderer)
        myGLES32View.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                }
                MotionEvent.ACTION_DOWN -> {
                    //Log.d(javaClass.simpleName,"ex[${event.x}]ey[${event.y}]")
                    //Log.d(javaClass.simpleName,"vw[${myGLES32View.width}]vh[${myGLES32View.height}]")
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

        val seekBarW76 = view.findViewById<SeekBar>(R.id.seekBarW76)
        seekBarW76.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.u_dotScale = seekBar.progress.toFloat()*0.1f+0.1f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.u_dotScale = seekBar.progress.toFloat()*0.1f+0.1f
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
                W76Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
