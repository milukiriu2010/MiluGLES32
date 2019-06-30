package milu.kiriu2010.milugles32.w5x.w57

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

class W57Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w57, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW57)
        val renderer = W57Renderer(context!!)
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

        val radioGroupW57 = view.findViewById<RadioGroup>(R.id.radioGroupW57)
        val radioButtonW57Render = view.findViewById<RadioButton>(R.id.radioButtonW57Render)
        val radioButtonW57Texture1 = view.findViewById<RadioButton>(R.id.radioButtonW57Texture1)
        val radioButtonW57Texture2 = view.findViewById<RadioButton>(R.id.radioButtonW57Texture2)
        radioGroupW57.setOnCheckedChangeListener { group, checkedId ->
            renderer.textureType = when (checkedId) {
                radioButtonW57Render.id -> 0
                radioButtonW57Texture1.id -> 1
                radioButtonW57Texture2.id -> 2
                else -> 0
            }
        }

        val checkBoxW57Gaussian = view.findViewById<CheckBox>(R.id.checkBoxW57Gaussian)
        checkBoxW57Gaussian.isChecked = when(renderer.u_gaussian) {
            1 -> true
            else -> false
        }
        checkBoxW57Gaussian.setOnCheckedChangeListener { buttonView, isChecked ->
            renderer.u_gaussian = when (isChecked) {
                true -> 1
                else -> 0
            }
        }

        val seekBarW57Dispersion = view.findViewById<SeekBar>(R.id.seekBarW57Dispersion)
        seekBarW57Dispersion.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
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
                W57Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
