package milu.kiriu2010.milugles32.w5x.w58

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

class W58Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w58, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW58)
        val renderer = W58Renderer(context!!)
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

        val checkBoxW58Glare = view.findViewById<CheckBox>(R.id.checkBoxW58Glare)
        checkBoxW58Glare.isChecked = when(renderer.u_glare) {
            1 -> true
            else -> false
        }
        checkBoxW58Glare.setOnCheckedChangeListener { buttonView, isChecked ->
            renderer.u_glare = when (isChecked) {
                true -> 1
                else -> 0
            }
        }

        val seekBarW58Dispersion = view.findViewById<SeekBar>(R.id.seekBarW58Dispersion)
        seekBarW58Dispersion.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.k_gaussian = if ( seekBar.progress > 0 ) {
                    (seekBar.progress+1).toFloat()
                }
                else {
                    1f
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.k_gaussian = if ( seekBar.progress > 0 ) {
                    (seekBar.progress+1).toFloat()
                }
                else {
                    1f
                }
            }

        })

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
                W58Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
