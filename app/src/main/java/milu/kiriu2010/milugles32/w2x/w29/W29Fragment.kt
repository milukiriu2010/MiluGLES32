package milu.kiriu2010.milugles32.w2x.w29

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar

import milu.kiriu2010.gui.view.MyGLES32View
import milu.kiriu2010.milugles32.R

// ----------------------------------------------------------------------------------
// アルファブレンディング
//   色を混ぜ合わせることを可能にする
// ----------------------------------------------------------------------------------
// 描画元(これから描画されようとする色)と描画先(既に描画されている色)を混ぜ合わせる
// ----------------------------------------------------------------------------------
// 描画色 = 描画元の色 * sourceFactor + 描画先の色 * destinationFactor
// ----------------------------------------------------------------------------------
// https://wgld.org/d/webgl/w029.html
// ----------------------------------------------------------------------------------
class W29Fragment : Fragment() {

    private lateinit var myGLES32View: MyGLES32View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_w29, container, false)

        myGLES32View = view.findViewById(R.id.myGLES32ViewW29)
        val renderer = W29Renderer(context!!)
        myGLES32View.setRenderer(renderer)
        myGLES32View.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                }
                MotionEvent.ACTION_DOWN -> {
                    renderer.isRunning = !renderer.isRunning
                    myGLES32View.performClick()
                }
                MotionEvent.ACTION_MOVE -> {
                }
                else -> {
                }
            }
            true
        }

        // ブレンドタイプ
        val radioGroupW29 = view.findViewById<RadioGroup>(R.id.radioGroupW29)
        val btnW29Normal= view.findViewById<RadioButton>(R.id.btnW29Normal)
        val btnW29Transparency = view.findViewById<RadioButton>(R.id.btnW29TransParency)
        val btnW29Add = view.findViewById<RadioButton>(R.id.btnW29Add)
        val btnW29Reverse = view.findViewById<RadioButton>(R.id.btnW29Reverse)
        val btnW29PhotoShop = view.findViewById<RadioButton>(R.id.btnW29PhotoShop)
        val btnW29Mult = view.findViewById<RadioButton>(R.id.btnW29Mult)
        radioGroupW29.check(btnW29Transparency.id)
        radioGroupW29.setOnCheckedChangeListener { _, checkedId ->
            renderer.blendType = when (checkedId) {
                // 通常
                btnW29Normal.id -> 0
                // 透過
                btnW29Transparency.id -> 1
                // 加算
                btnW29Add.id -> 2
                // 反転
                btnW29Reverse.id -> 3
                // 加算+アルファ(PhotoShop的スクリーン)
                btnW29PhotoShop.id -> 4
                // 乗算
                btnW29Mult.id -> 5
                else -> 1
            }
        }

        // ブレンディングに使うアルファ成分の割合
        val seekBarW29BlendAlpha = view.findViewById<SeekBar>(R.id.seekBarW29BlendAlpha)
        seekBarW29BlendAlpha.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.vertexAplha = seekBar.progress.toFloat()/seekBar.max.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.vertexAplha = seekBar.progress.toFloat()/seekBar.max.toFloat()
            }
        })

        // 背景に使う色(赤)
        val seekBarW29BackRed = view.findViewById<SeekBar>(R.id.seekBarW29BackRed)
        seekBarW29BackRed.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.colorBack[0] = seekBar.progress.toFloat()/seekBar.max.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.colorBack[0] = seekBar.progress.toFloat()/seekBar.max.toFloat()
            }
        })

        // 背景に使う色(緑)
        val seekBarW29BackGreen = view.findViewById<SeekBar>(R.id.seekBarW29BackGreen)
        seekBarW29BackGreen.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.colorBack[1] = seekBar.progress.toFloat()/seekBar.max.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.colorBack[1] = seekBar.progress.toFloat()/seekBar.max.toFloat()
            }
        })

        // 背景に使う色(青)
        val seekBarW29BackBlue = view.findViewById<SeekBar>(R.id.seekBarW29BackBlue)
        seekBarW29BackBlue.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.colorBack[2] = seekBar.progress.toFloat()/seekBar.max.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.colorBack[2] = seekBar.progress.toFloat()/seekBar.max.toFloat()
            }
        })

        // 背景に使う色(α)
        val seekBarW29BackAlpha = view.findViewById<SeekBar>(R.id.seekBarW29BackAlpha)
        seekBarW29BackAlpha.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                renderer.colorBack[3] = seekBar.progress.toFloat()/seekBar.max.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                renderer.colorBack[3] = seekBar.progress.toFloat()/seekBar.max.toFloat()
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
                W29Fragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
