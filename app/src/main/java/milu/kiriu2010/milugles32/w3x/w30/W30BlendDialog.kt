package milu.kiriu2010.milugles32.w3x.w30

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import milu.kiriu2010.milugles32.R

// ---------------------------------------------------
// ブレンドファクター
// ---------------------------------------------------
// https://wgld.org/d/webgl/w030.html
// ---------------------------------------------------
class W30BlendDialog: DialogFragment() {

    // 0.0 - 1.0
    var red = 0f
    var green = 0f
    var blue = 0f
    var alpha = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            red   = it.getFloat("RED")
            green = it.getFloat("GREEN")
            blue  = it.getFloat("BLUE")
            alpha = it.getFloat("ALPHA")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_w30_blend, container, false)

        //val ctx = context ?: return view

        val seekBarW30BlendRed = view.findViewById<SeekBar>(R.id.seekBarW30BlendRed)
        seekBarW30BlendRed.progress = (red * 10f).toInt()
        seekBarW30BlendRed.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                red = seekBar.progress.toFloat()/10f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                red = seekBar.progress.toFloat()/10f
            }
        })

        val seekBarW30BlendGreen = view.findViewById<SeekBar>(R.id.seekBarW30BlendGreen)
        seekBarW30BlendGreen.progress = (green * 10f).toInt()
        seekBarW30BlendGreen.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                green = seekBar.progress.toFloat()/10f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                green = seekBar.progress.toFloat()/10f
            }
        })

        val seekBarW30BlendBlue = view.findViewById<SeekBar>(R.id.seekBarW30BlendBlue)
        seekBarW30BlendBlue.progress = (blue * 10f).toInt()
        seekBarW30BlendBlue.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                blue = seekBar.progress.toFloat()/10f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                blue = seekBar.progress.toFloat()/10f
            }
        })

        val seekBarW30BlendAlpha = view.findViewById<SeekBar>(R.id.seekBarW30BlendAlpha)
        seekBarW30BlendAlpha.progress = (alpha * 10f).toInt()
        seekBarW30BlendAlpha.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                alpha = seekBar.progress.toFloat()/10f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                alpha = seekBar.progress.toFloat()/10f
            }
        })

        val btnW30CloseBlend = view.findViewById<Button>(R.id.btnW30CloseBlend)
        btnW30CloseBlend.setOnClickListener {
            val intent = Intent().also {
                it.putExtra("RED"  , red)
                it.putExtra("GREEN", green)
                it.putExtra("BLUE" , blue)
                it.putExtra("ALPHA", alpha)
            }
            targetFragment?.onActivityResult(2,0,intent)
            dismiss()
        }


        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle) =
                W30BlendDialog().apply {
                    arguments = Bundle().also {
                        it.putFloat("RED"  , bundle.getFloat("RED"))
                        it.putFloat("GREEN", bundle.getFloat("GREEN"))
                        it.putFloat("BLUE" , bundle.getFloat("BLUE"))
                        it.putFloat("ALPHA", bundle.getFloat("ALPHA"))
                    }
                }
    }
}