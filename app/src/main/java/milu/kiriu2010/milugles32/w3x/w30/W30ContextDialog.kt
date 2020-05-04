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

class W30ContextDialog: androidx.fragment.app.DialogFragment() {

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
        val view = inflater.inflate(R.layout.fragment_w30_context, container, false)

        //val ctx = context ?: return view

        val seekBarW30ContextRed = view.findViewById<SeekBar>(R.id.seekBarW30ContextRed)
        seekBarW30ContextRed.progress = (red * 10f).toInt()
        seekBarW30ContextRed.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                red = seekBar.progress.toFloat()/10f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                red = seekBar.progress.toFloat()/10f
            }
        })

        val seekBarW30ContextGreen = view.findViewById<SeekBar>(R.id.seekBarW30ContextGreen)
        seekBarW30ContextGreen.progress = (green * 10f).toInt()
        seekBarW30ContextGreen.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                green = seekBar.progress.toFloat()/10f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                green = seekBar.progress.toFloat()/10f
            }
        })

        val seekBarW30ContextBlue = view.findViewById<SeekBar>(R.id.seekBarW30ContextBlue)
        seekBarW30ContextBlue.progress = (blue * 10f).toInt()
        seekBarW30ContextBlue.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                blue = seekBar.progress.toFloat()/10f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                blue = seekBar.progress.toFloat()/10f
            }
        })

        val seekBarW30ContextAlpha = view.findViewById<SeekBar>(R.id.seekBarW30ContextAlpha)
        seekBarW30ContextAlpha.progress = (alpha * 10f).toInt()
        seekBarW30ContextAlpha.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                alpha = seekBar.progress.toFloat()/10f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                alpha = seekBar.progress.toFloat()/10f
            }
        })

        val btnW30CloseContext = view.findViewById<Button>(R.id.btnW30CloseContext)
        btnW30CloseContext.setOnClickListener {
            val intent = Intent().also {
                it.putExtra("RED"  , red)
                it.putExtra("GREEN", green)
                it.putExtra("BLUE" , blue)
                it.putExtra("ALPHA", alpha)
            }
            targetFragment?.onActivityResult(1,0,intent)
            dismiss()
        }


        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle) =
                W30ContextDialog().apply {
                    arguments = Bundle().also {
                        it.putFloat("RED"  , bundle.getFloat("RED"))
                        it.putFloat("GREEN", bundle.getFloat("GREEN"))
                        it.putFloat("BLUE" , bundle.getFloat("BLUE"))
                        it.putFloat("ALPHA", bundle.getFloat("ALPHA"))
                    }
                }
    }
}