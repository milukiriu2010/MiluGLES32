package milu.kiriu2010.milugles32.w6x.w69

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

// -------------------------------------------
// a vertex attribute index out of boundary is detected. Skipping corresponding vertex attribute. buf=0xe7b8ec30
// emuglGLESv2_enc: Out of bounds vertex attribute info: clientArray? 1 attribute 2 vbo 13 allocedBufferSize 64 bufferDataSpecified? 1 wantedStart 0 wantedEnd 17424
// -------------------------------------------
// 正しい深度値を適用したシャドウマッピング
// -------------------------------------------
// w51とは違うらしい
// -------------------------------------------
// https://wgld.org/d/webgl/w069.html
// -------------------------------------------
class W69Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w69, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW69)
        val renderer = W69Renderer(context!!)
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
        val seekBarW69 = view.findViewById<SeekBar>(R.id.seekBarW69)
        seekBarW69.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.k = (seekBar.progress+20).toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.k = (seekBar.progress+20).toFloat()
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
                W69Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
