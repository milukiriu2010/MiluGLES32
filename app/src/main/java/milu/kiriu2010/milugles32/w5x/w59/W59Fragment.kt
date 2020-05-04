package milu.kiriu2010.milugles32.w5x.w59

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

class W59Fragment : androidx.fragment.app.Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w59, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW59)
        val renderer = W59Renderer(context!!)
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

        val spinnerW59 = view.findViewById<Spinner>(R.id.spinnerW59)
        spinnerW59.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // http://android-note.open-memo.net/sub/spinner--get-resource-id-for-selected-item.html
                val array = resources.obtainTypedArray(R.array.w59list)
                val itemId = array.getResourceId(position,R.string.w59_depth_of_field)
                renderer.u_result = when (itemId) {
                    R.string.w59_depth_of_field -> 0
                    R.string.w59_depth -> 1
                    R.string.w59_scene -> 2
                    R.string.w59_blur1 -> 3
                    R.string.w59_blur2 -> 4
                    else -> 0
                }
                // 使わなくなったら解放
                array.recycle()
            }

        }

        val seekBarW59Depth = view.findViewById<SeekBar>(R.id.seekBarW59Depth)
        seekBarW59Depth.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.u_depthOffset = (seekBar.progress-5).toFloat()/10f * 0.85f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.u_depthOffset = (seekBar.progress-5).toFloat()/10f * 0.85f
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
                W59Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
