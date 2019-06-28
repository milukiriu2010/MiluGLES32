package milu.kiriu2010.milugles32.g0x.g08

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

class G08Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_a01, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewA01)
        val renderer = G08Renderer(context!!)
        myGLES32View.setRenderer(renderer)

        myGLES32View.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    renderer.touchP.x = event.x.toFloat()/renderer.renderW.toFloat()
                    renderer.touchP.y = event.y.toFloat()/renderer.renderH.toFloat()
                }
                MotionEvent.ACTION_DOWN -> {
                    renderer.touchP.x = event.x.toFloat()/renderer.renderW.toFloat()
                    renderer.touchP.y = event.y.toFloat()/renderer.renderH.toFloat()
                }
                MotionEvent.ACTION_MOVE -> {
                    renderer.touchP.x = event.x.toFloat()/renderer.renderW.toFloat()
                    renderer.touchP.y = event.y.toFloat()/renderer.renderH.toFloat()
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
                G08Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
