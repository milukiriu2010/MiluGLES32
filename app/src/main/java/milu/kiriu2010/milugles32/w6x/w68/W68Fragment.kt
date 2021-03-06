package milu.kiriu2010.milugles32.w6x.w68

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

// -----------------------------------------
// emuglGLESv2_enc: device/generic/goldfish-opengl/system/GLESv2_enc/GL2Encoder.cpp:s_glVertexAttribPointer:599 GL error 0x501
//    Info: Invalid vertex attribute index. Wanted index: 4294967295. Max index: 16
// WV068ShaderZoomBlur:a_TextureCoord:Board00Model:1281
// -----------------------------------------
// ゴッドレイフィルタ
// -----------------------------------------
// // https://wgld.org/d/webgl/w068.html
// -----------------------------------------
class W68Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w68, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW68)
        val renderer = W68Renderer(context!!)
        myGLES32View.setRenderer(renderer)
        myGLES32View.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                }
                MotionEvent.ACTION_DOWN -> {
                    Log.d(javaClass.simpleName,"ex[${event.x}]ey[${event.y}]")
                    Log.d(javaClass.simpleName,"vw[${myGLES32View.width}]vh[${myGLES32View.height}]")
                    //render.receiveTouch(event,myGLES32View.width,myGLES32View.height)
                    renderer.mouseP[0] = event.x
                    renderer.mouseP[1] = event.y
                    myGLES32View.performClick()
                }
                MotionEvent.ACTION_MOVE -> {
                    //render.receiveTouch(event,myGLES32View.width,myGLES32View.height)
                    renderer.mouseP[0] = event.x
                    renderer.mouseP[1] = event.y
                }
                else -> {
                }
            }
            true
        }

        val seekBarW68 = view.findViewById<SeekBar>(R.id.seekBarW68)
        seekBarW68.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.u_strength = seekBar.progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.u_strength = seekBar.progress.toFloat()
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
                W68Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
