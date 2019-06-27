package milu.kiriu2010.milugles32.w5x.w55

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

class W55Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w55, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW55)
        val renderer = W55Renderer(context!!)
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

        val radioGroupW55 = view.findViewById<RadioGroup>(R.id.radioGroupW55)
        val radioButtonW55Render = view.findViewById<RadioButton>(R.id.radioButtonW55Render)
        val radioButtonW55Texture1 = view.findViewById<RadioButton>(R.id.radioButtonW55Texture1)
        val radioButtonW55Texture2 = view.findViewById<RadioButton>(R.id.radioButtonW55Texture2)

        radioGroupW55.setOnCheckedChangeListener { group, checkedId ->
            renderer.textureType = when (checkedId) {
                radioButtonW55Render.id -> 0
                radioButtonW55Texture1.id -> 1
                radioButtonW55Texture2.id -> 2
                else -> 0
            }
        }

        val checkBoxW55Sobel = view.findViewById<CheckBox>(R.id.checkBoxW55Sobel)
        checkBoxW55Sobel.setOnCheckedChangeListener { buttonView, isChecked ->
            renderer.u_sobel = when (isChecked) {
                true -> 1
                else -> 0
            }
        }

        val checkBoxW55Gray = view.findViewById<CheckBox>(R.id.checkBoxW55Gray)
        checkBoxW55Gray.setOnCheckedChangeListener { buttonView, isChecked ->
            renderer.u_sobelGray = when (isChecked) {
                true -> 1
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
                W55Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
