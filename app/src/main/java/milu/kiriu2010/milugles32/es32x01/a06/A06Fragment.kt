package milu.kiriu2010.milugles32.es32x01.a06

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

// -----------------------------------------
// GLSL ES 3.0(VAO)
// -----------------------------------------
// https://wgld.org/d/webgl2/w006.html
// https://github.com/danginsburg/opengles3-book/blob/master/Android_Java/Chapter_6/VertexArrayObjects/src/com/openglesbook/VertexArrayObjects/VAORenderer.java
// https://www.programcreek.com/java-api-examples/?code=biezhihua/Android_OpenGL_Demo/Android_OpenGL_Demo-master/Learn-OpenGLES-Tutorials/android/AndroidOpenGLESLessons/app/src/main/java/com/learnopengles/android/lesson8/LessonEightRenderer.java#
// -----------------------------------------
class A06Fragment : Fragment() {

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
        val renderer = A06Renderer(context!!)
        myGLES32View.setRenderer(renderer)
        myGLES32View.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                }
                MotionEvent.ACTION_DOWN -> {
                    Log.d(javaClass.simpleName,"ex[${event.x}]ey[${event.y}]")
                    Log.d(javaClass.simpleName,"vw[${myGLES32View.width}]vh[${myGLES32View.height}]")
                    renderer.receiveTouch(event,myGLES32View.width,myGLES32View.height)
                    myGLES32View.performClick()
                }
                MotionEvent.ACTION_MOVE -> {
                    renderer.receiveTouch(event,myGLES32View.width,myGLES32View.height)
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
                A06Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
