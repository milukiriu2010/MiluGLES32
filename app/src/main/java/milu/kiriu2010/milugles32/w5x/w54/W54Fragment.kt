package milu.kiriu2010.milugles32.w5x.w54

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

class W54Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w54, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW54)
        val renderer = W54Renderer(context!!)
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

        val radioGroupW54 = view.findViewById<RadioGroup>(R.id.radioGroupW54)
        val radioButtonW54Color = view.findViewById<RadioButton>(R.id.radioButtonW54Color)
        val radioButtonW54Gray = view.findViewById<RadioButton>(R.id.radioButtonW54Gray)
        val radioButtonW54Sepia = view.findViewById<RadioButton>(R.id.radioButtonW54Sepia)

        radioGroupW54.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                radioButtonW54Color.id -> {
                    renderer.u_grayScale = 0
                    renderer.u_sepiaScale = 0
                }
                radioButtonW54Gray.id -> {
                    renderer.u_grayScale = 1
                    renderer.u_sepiaScale = 0
                }
                radioButtonW54Sepia.id -> {
                    renderer.u_grayScale = 0
                    renderer.u_sepiaScale = 1
                }
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
                W54Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
