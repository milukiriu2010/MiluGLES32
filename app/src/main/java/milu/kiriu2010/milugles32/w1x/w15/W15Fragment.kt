package milu.kiriu2010.milugles32.w1x.w15

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

// ----------------------------------------------
// ポリゴンに色を塗る(頂点色の指定)
// OpenGL ES 3.2
// ----------------------------------------------
// https://wgld.org/d/webgl/w015.html
// https://android.googlesource.com/platform/development/+/master/samples/OpenGL/HelloOpenGLES32/src/com/example/android/opengl/MyGLRenderer.java
// https://android.keicode.com/basics/opengl-drawing-basic-shapes.php
// https://developer.android.com/training/graphics/opengl/draw
// ----------------------------------------------
class W15Fragment : androidx.fragment.app.Fragment() {

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
        myGLES32View.setRenderer(W15Renderer(context!!))

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
                W15Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
