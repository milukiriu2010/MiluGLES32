package milu.kiriu2010.milugles32.w8x.w86

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

class W86Fragment : androidx.fragment.app.Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w86, container, false)

        val textViewW86 = view.findViewById<TextView>(R.id.textViewW86)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW86)
        val renderer = W86Renderer(context!!)
        myGLES32View.setRenderer(renderer)
        myGLES32View.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                }
                MotionEvent.ACTION_DOWN -> {
                    renderer.touchP.x = event.x
                    renderer.touchP.y = event.y

                    textViewW86.text = "R[${renderer.colorBuf.get(0)}]G[${renderer.colorBuf.get(1)}]B[${renderer.colorBuf.get(2)}]A[${renderer.colorBuf.get(3)}]"
                }
                MotionEvent.ACTION_MOVE -> {
                }
                else -> {
                }
            }
            true
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
                W86Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
