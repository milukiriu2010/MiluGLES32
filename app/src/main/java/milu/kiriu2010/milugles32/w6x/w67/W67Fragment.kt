package milu.kiriu2010.milugles32.w6x.w67

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

class W67Fragment : androidx.fragment.app.Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w67, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW67)
        val renderer = W67Renderer(context!!)
        myGLES32View.setRenderer(renderer)
        myGLES32View.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                }
                MotionEvent.ACTION_DOWN -> {
                    Log.d(javaClass.simpleName,"ex[${event.x}]ey[${event.y}]")
                    Log.d(javaClass.simpleName,"vw[${myGLES32View.width}]vh[${myGLES32View.height}]")
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

        val radioGroupW67 = view.findViewById<RadioGroup>(R.id.radioGroupW67)
        val radioButtonW67Render = view.findViewById<RadioButton>(R.id.radioButtonW67Render)
        val radioButtonW67Texture1 = view.findViewById<RadioButton>(R.id.radioButtonW67Texture1)
        val radioButtonW67Texture2 = view.findViewById<RadioButton>(R.id.radioButtonW67Texture2)

        radioGroupW67.setOnCheckedChangeListener { group, checkedId ->
            renderer.textureType = when (checkedId) {
                radioButtonW67Render.id -> 0
                radioButtonW67Texture1.id -> 1
                radioButtonW67Texture2.id -> 2
                else -> 0
            }
        }

        val seekBarW67 = view.findViewById<SeekBar>(R.id.seekBarW67)
        seekBarW67.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.u_strength = seekBar.progress.toFloat() * 3f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.u_strength = seekBar.progress.toFloat() * 3f
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
                W67Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
