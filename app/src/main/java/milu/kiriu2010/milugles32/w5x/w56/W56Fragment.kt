package milu.kiriu2010.milugles32.w5x.w56

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

class W56Fragment : androidx.fragment.app.Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w56, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW56)
        val renderer = W56Renderer(context!!)
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

        val radioGroupW56 = view.findViewById<RadioGroup>(R.id.radioGroupW56)
        val radioButtonW56Render = view.findViewById<RadioButton>(R.id.radioButtonW56Render)
        val radioButtonW56Texture1 = view.findViewById<RadioButton>(R.id.radioButtonW56Texture1)
        val radioButtonW56Texture2 = view.findViewById<RadioButton>(R.id.radioButtonW56Texture2)

        radioGroupW56.setOnCheckedChangeListener { group, checkedId ->
            renderer.textureType = when (checkedId) {
                radioButtonW56Render.id -> 0
                radioButtonW56Texture1.id -> 1
                radioButtonW56Texture2.id -> 2
                else -> 0
            }
        }

        val checkBoxW56Laplacian = view.findViewById<CheckBox>(R.id.checkBoxW56Laplacian)
        checkBoxW56Laplacian.setOnCheckedChangeListener { buttonView, isChecked ->
            renderer.u_laplacian = when (isChecked) {
                true -> 1
                else -> 0
            }
        }

        val checkBoxW56Gray = view.findViewById<CheckBox>(R.id.checkBoxW56Gray)
        checkBoxW56Gray.setOnCheckedChangeListener { buttonView, isChecked ->
            renderer.u_laplacianGray = when (isChecked) {
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
                W56Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
