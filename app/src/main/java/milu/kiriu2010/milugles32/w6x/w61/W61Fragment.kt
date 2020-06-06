package milu.kiriu2010.milugles32.w6x.w61

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

// ------------------------------------------------------------
// emuglGLESv2_enc: device/generic/goldfish-opengl/system/GLESv2_enc/GL2Encoder.cpp:s_glVertexAttribPointer:599 GL error 0x501
// WV061ShaderFog:a_Normal:Particle01Model:1281
// ------------------------------------------------------------
// パーティクルフォグ
// ------------------------------------------------------------
//   板状の四角形ポリゴンを３次元空間にたくさん配置し、
//   これら板状のポリゴンに霧のようなテクスチャを適用して、
//   ブレンドを有効にして半透明描画することにより、
//   なんとなく霧っぽく見えるようにしている
// ------------------------------------------------------------
// https://wgld.org/d/webgl/w061.html
// ------------------------------------------------------------
class W61Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w61, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW61)
        val renderer = W61Renderer(context!!)
        myGLES32View.setRenderer(renderer)
        myGLES32View.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                }
                MotionEvent.ACTION_DOWN -> {
                    Log.d(javaClass.simpleName,"ex[${event.x}]ey[${event.y}]")
                    Log.d(javaClass.simpleName,"vw[${myGLES32View.width}]vh[${myGLES32View.height}]")
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

        val checkBoxW61Fog = view.findViewById<CheckBox>(R.id.checkBoxW61Fog)
        checkBoxW61Fog.setOnCheckedChangeListener { _, isChecked ->
            renderer.u_softParticle = when(isChecked) {
                true -> 1
                else -> 0
            }
        }

        val seekBarW61DepthCoef = view.findViewById<SeekBar>(R.id.seekBarW61DepthCoef)
        seekBarW61DepthCoef.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.u_depthCoef = seekBar.progress.toFloat()*0.01f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.u_depthCoef = seekBar.progress.toFloat()*0.01f
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
                W61Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
