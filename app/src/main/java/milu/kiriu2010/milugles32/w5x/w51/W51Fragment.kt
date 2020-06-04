package milu.kiriu2010.milugles32.w5x.w51

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

// -----------------------------------------------------------
// シャドウマッピング
// -----------------------------------------------------------
// フレームバッファにライトから見たときの各頂点の深度値を描く
// 本番のレンダリングを行う際に、
// フレームバッファから読みだした深度値と
// 実際にレンダリングしようとしている頂点の深度を比較すれば
// その頂点が影にあるのかどうかを判別できる
// -----------------------------------------------------------
// デプスバッファの値は0.0～1.0の範囲
// カメラに最も近いところ  ⇒0.0
// カメラから最も遠いところ⇒1.0
// -----------------------------------------------------------
// emuglGLESv2_enc: Out of bounds vertex attribute info: clientArray? 1 attribute 1 vbo 10 allocedBufferSize 48 bufferDataSpecified? 1 wantedStart 0 wantedEnd 13068
// -----------------------------------------------------------
// https://wgld.org/d/webgl/w051.html
// -----------------------------------------------------------
class W51Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    private lateinit var seekBarW51: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w51, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW51)
        val renderer = W51Renderer(context!!)
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
        seekBarW51 = view.findViewById(R.id.seekBarW51)
        seekBarW51.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.k = (seekBar.progress+20).toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.k = (seekBar.progress+20).toFloat()
            }

        })
        val radioGroupW51 = view.findViewById<RadioGroup>(R.id.radioGroupW51)
        val radioButtonW51Frag = view.findViewById<RadioButton>(R.id.radioButtonW51Frag)
        val radioButtonW51Vertex = view.findViewById<RadioButton>(R.id.radioButtonW51Vertex)
        radioGroupW51.setOnCheckedChangeListener { _, checkedId ->
            renderer.u_depthBuffer = when (checkedId) {
                radioButtonW51Frag.id   -> 1
                radioButtonW51Vertex.id -> 0
                else -> 0
            }
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
                W51Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
