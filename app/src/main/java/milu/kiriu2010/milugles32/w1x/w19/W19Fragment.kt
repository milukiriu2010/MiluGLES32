package milu.kiriu2010.milugles32.w1x.w19

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

// ---------------------------------------------
// カリングと深度テスト
// OpenGL ES 3.2
// ---------------------------------------------
// https://wgld.org/d/webgl/w019.html
// ---------------------------------------------
class W19Fragment : androidx.fragment.app.Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    private lateinit var switchCulling: Switch

    private lateinit var switchFront: Switch

    private lateinit var switchDepth: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w19, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW19)
        var renderer = W19Renderer(context!!)
        myGLES32View.setRenderer(renderer)

        switchCulling = view.findViewById(R.id.switchCullingW19)
        switchCulling.setOnCheckedChangeListener { _, isChecked ->
            renderer.culling = isChecked
        }

        switchFront = view.findViewById(R.id.switchFrontW19)
        switchFront.setOnCheckedChangeListener { _, isChecked ->
            renderer.frontFace = isChecked
        }

        switchDepth = view.findViewById(R.id.switchDepthW19)
        switchDepth.setOnCheckedChangeListener { _, isChecked ->
            renderer.depthTest = isChecked
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
                W19Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
