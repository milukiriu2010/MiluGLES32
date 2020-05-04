package milu.kiriu2010.milugles32.w3x.w35

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.*
import android.widget.Switch

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R


class W35Fragment : androidx.fragment.app.Fragment() {

    private lateinit var myGLES32View: MyGLES32View
    private lateinit var switch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w35, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW35)
        val renderer = W35Renderer(context!!)
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

        switch = view.findViewById(R.id.switchW35)
        switch.setOnCheckedChangeListener { _, isChecked ->
            renderer.isBillBoard = isChecked
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
                W35Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
