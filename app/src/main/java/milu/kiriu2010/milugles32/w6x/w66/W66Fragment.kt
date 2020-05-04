package milu.kiriu2010.milugles32.w6x.w66

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

class W66Fragment : androidx.fragment.app.Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    private lateinit var radioGroupW66: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w66, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW66)
        val renderer = W66Renderer(context!!)
        myGLES32View.setRenderer(renderer)
        myGLES32View.setOnTouchListener { _, event ->
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

        radioGroupW66 = view.findViewById(R.id.radioGroupW66)
        val radioButtonW66Render = view.findViewById<RadioButton>(R.id.radioButtonW66Render)
        val radioButtonW66Texture1 = view.findViewById<RadioButton>(R.id.radioButtonW66Texture1)
        val radioButtonW66Texture2 = view.findViewById<RadioButton>(R.id.radioButtonW66Texture2)

        radioGroupW66.setOnCheckedChangeListener { _, checkedId ->
            renderer.textureType = when (checkedId) {
                radioButtonW66Render.id -> 0
                radioButtonW66Texture1.id -> 1
                radioButtonW66Texture2.id -> 2
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
                W66Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
