package milu.kiriu2010.milugles32.w3x.w36

import android.opengl.GLES32
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
import android.widget.TextView

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

// --------------------------------------
// 点や線のレンダリング
// --------------------------------------
// https://wgld.org/d/webgl/w036.html
// --------------------------------------
class W36Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    private lateinit var textViewW36MinSizeVal: TextView

    private lateinit var textViewW36MaxSizeVal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w36, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW36)
        val renderer = W36Renderer(context!!)
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
                }
                MotionEvent.ACTION_MOVE -> {
                    renderer.receiveTouch(event,myGLES32View.width,myGLES32View.height)
                }
                else -> {
                }
            }
            true
        }
        
        // 点のサイズ
        val seekBarW36 = view.findViewById<SeekBar>(R.id.seekBarW36)
        seekBarW36.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.u_pointSize = (seekBar.progress+20).toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.u_pointSize = (seekBar.progress+20).toFloat()
            }
        })

        // 線のプリミティブタイプを選択
        val radioGroupW36 = view.findViewById<RadioGroup>(R.id.radioGroupW36)
        val rbnW36Lines = view.findViewById<RadioButton>(R.id.rbnW36Lines)
        val rbnW36LineStrip = view.findViewById<RadioButton>(R.id.rbnW36LineStrip)
        val rbnW36LineLoop = view.findViewById<RadioButton>(R.id.rbnW36LineLoop)
        radioGroupW36.setOnCheckedChangeListener { _, checkedId ->
            renderer.lineType = when (checkedId) {
                rbnW36Lines.id     -> GLES32.GL_LINES
                rbnW36LineStrip.id -> GLES32.GL_LINE_STRIP
                rbnW36LineLoop.id  -> GLES32.GL_LINE_LOOP
                else               -> GLES32.GL_LINES
            }
        }

        // 点のサイズ(最小)
        textViewW36MinSizeVal = view.findViewById(R.id.textViewW36MinSizeVal)
        textViewW36MinSizeVal.text = renderer.pointSizeRange[0].toString()

        // 点のサイズ(最大)
        textViewW36MaxSizeVal = view.findViewById(R.id.textViewW36MaxSizeVal)
        textViewW36MaxSizeVal.text = renderer.pointSizeRange[1].toString()

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
                W36Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
