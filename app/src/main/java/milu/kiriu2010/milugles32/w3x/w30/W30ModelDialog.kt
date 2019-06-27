package milu.kiriu2010.milugles32.w3x.w30

import android.content.Intent
import android.opengl.GLES32
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import milu.kiriu2010.milugles32.R

class W30ModelDialog: DialogFragment() {

    // リクエストコード
    var reqCode = -1
    // ブレンド
    var blend = false
    // アルファ成分
    var vertexAlpha = 0f
    // 方程式(カラー)
    var equationColor = GLES32.GL_FUNC_ADD
    // 方程式(アルファ)
    var equationAlpha = GLES32.GL_FUNC_ADD
    // ブレンドファクター(カラー元)
    var blendFctSCBF = GLES32.GL_ONE
    // ブレンドファクター(カラー先)
    var blendFctDCBF = GLES32.GL_ONE
    // ブレンドファクター(アルファ元)
    var blendFctSABF = GLES32.GL_ONE
    // ブレンドファクター(アルファ先)
    var blendFctDABF = GLES32.GL_ONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // リクエストコード
            reqCode = it.getInt("REQ_CODE") ?: reqCode
            // ブレンド
            blend = it.getBoolean("BLEND") ?: false
            // アルファ成分
            vertexAlpha = it.getFloat("VERTEX_ALPHA") ?: 0f
            // 方程式(カラー)
            equationColor = it.getInt("EQUATION_COLOR") ?: GLES32.GL_FUNC_ADD
            // 方程式(アルファ)
            equationAlpha = it.getInt("EQUATION_ALPHA") ?: GLES32.GL_FUNC_ADD
            // ブレンドファクター(カラー元)
            blendFctSCBF = it.getInt("BLEND_FCT_SCBF") ?: GLES32.GL_ONE
            // ブレンドファクター(カラー先)
            blendFctDCBF = it.getInt("BLEND_FCT_DCBF") ?: GLES32.GL_ONE
            // ブレンドファクター(カラー元)
            blendFctSABF = it.getInt("BLEND_FCT_SABF") ?: GLES32.GL_ONE
            // ブレンドファクター(カラー先)
            blendFctDABF = it.getInt("BLEND_FCT_DABF") ?: GLES32.GL_ONE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_w30_model, container, false)

        val ctx = context ?: return view

        // 修正対象のモデル
        val textViewW30Model = view.findViewById<TextView>(R.id.textViewW30Model)
        when (reqCode) {
            3 -> textViewW30Model.text = "テクスチャの合成成分を変更"
            4 -> textViewW30Model.text = "板ポリゴンの合成成分を変更"
            else -> textViewW30Model.text = "不明"
        }

        // ブレンドするかどうか
        val checkBoxW30Blend = view.findViewById<CheckBox>(R.id.checkBoxW30Blend)
        checkBoxW30Blend.isChecked = blend
        checkBoxW30Blend.setOnCheckedChangeListener { buttonView, isChecked ->
            blend = isChecked
        }

        // アルファ成分
        val seekBarW30VertexAlpha = view.findViewById<SeekBar>(R.id.seekBarW30VertexAlpha)
        seekBarW30VertexAlpha.progress = (vertexAlpha * 10f).toInt()
        seekBarW30VertexAlpha.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                vertexAlpha = seekBar.progress.toFloat()/10f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                vertexAlpha = seekBar.progress.toFloat()/10f
            }
        })

        // 方程式(カラー)
        val spinnerW30CE = view.findViewById<Spinner>(R.id.spinnerW30CE)
        val adapterW30CE = spinnerW30CE.adapter
        val strW30CE = when (equationColor) {
            GLES32.GL_FUNC_ADD ->  "FUNC_ADD"
            GLES32.GL_FUNC_SUBTRACT -> "FUNC_SUBTRACT"
            GLES32.GL_FUNC_REVERSE_SUBTRACT -> "FUNC_REVERSE_SUBTRACT"
            else -> "FUNC_ADD"
        }
        var idW30CE = -1
        (0 until adapterW30CE.count).forEach { i ->
            if ( adapterW30CE.getItem(i).equals(strW30CE) ) {
                idW30CE = i
                return@forEach
            }
        }
        spinnerW30CE.setSelection(idW30CE)
        spinnerW30CE.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                equationColor = when (spinnerW30CE.selectedItem) {
                    "FUNC_ADD" -> GLES32.GL_FUNC_ADD
                    "FUNC_SUBTRACT" -> GLES32.GL_FUNC_SUBTRACT
                    "FUNC_REVERSE_SUBTRACT" -> GLES32.GL_FUNC_REVERSE_SUBTRACT
                    else -> GLES32.GL_FUNC_ADD
                }
            }
        }

        // 方程式(アルファ)
        val spinnerW30AE = view.findViewById<Spinner>(R.id.spinnerW30AE)
        val adapterW30AE = spinnerW30CE.adapter
        val strW30AE = when (equationAlpha) {
            GLES32.GL_FUNC_ADD ->  "FUNC_ADD"
            GLES32.GL_FUNC_SUBTRACT -> "FUNC_SUBTRACT"
            GLES32.GL_FUNC_REVERSE_SUBTRACT -> "FUNC_REVERSE_SUBTRACT"
            else -> "FUNC_ADD"
        }
        var idW30AE = -1
        (0 until adapterW30AE.count).forEach { i ->
            if ( adapterW30AE.getItem(i).equals(strW30AE) ) {
                idW30AE = i
                return@forEach
            }
        }
        spinnerW30AE.setSelection(idW30AE)
        spinnerW30AE.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                equationAlpha = when (spinnerW30AE.selectedItem) {
                    "FUNC_ADD" -> GLES32.GL_FUNC_ADD
                    "FUNC_SUBTRACT" -> GLES32.GL_FUNC_SUBTRACT
                    "FUNC_REVERSE_SUBTRACT" -> GLES32.GL_FUNC_REVERSE_SUBTRACT
                    else -> GLES32.GL_FUNC_ADD
                }
            }
        }

        // ブレンドファクター(カラー元)
        val spinnerW30SCBF = view.findViewById<Spinner>(R.id.spinnerW30SCBF)
        val adapterW30SCBF = spinnerW30SCBF.adapter
        val strW30SCBF = when (blendFctSCBF) {
            GLES32.GL_ZERO -> "ZERO"
            GLES32.GL_ONE -> "ONE"
            GLES32.GL_SRC_COLOR -> "SRC_COLOR"
            GLES32.GL_DST_COLOR -> "DST_COLOR"
            GLES32.GL_ONE_MINUS_SRC_COLOR -> "ONE_MINUS_SRC_COLOR"
            GLES32.GL_ONE_MINUS_DST_COLOR -> "ONE_MINUS_DST_COLOR"
            GLES32.GL_SRC_ALPHA -> "SRC_ALPHA"
            GLES32.GL_DST_ALPHA -> "DST_ALPHA"
            GLES32.GL_ONE_MINUS_SRC_ALPHA -> "ONE_MINUS_SRC_ALPHA"
            GLES32.GL_ONE_MINUS_DST_ALPHA -> "ONE_MINUS_DST_ALPHA"
            GLES32.GL_CONSTANT_COLOR -> "CONSTANT_COLOR"
            GLES32.GL_ONE_MINUS_CONSTANT_COLOR -> "ONE_MINUS_CONSTANT_COLOR"
            GLES32.GL_CONSTANT_ALPHA -> "CONSTANT_ALPHA"
            GLES32.GL_ONE_MINUS_CONSTANT_ALPHA -> "ONE_MINUS_CONSTANT_ALPHA"
            GLES32.GL_SRC_ALPHA_SATURATE -> "SRC_ALPHA_SATURATE"
            else -> "ONE"
        }
        var idW30SCBF = -1
        (0 until adapterW30SCBF.count).forEach { i ->
            if ( adapterW30SCBF.getItem(i).equals(strW30SCBF) ) {
                idW30SCBF = i
                return@forEach
            }
        }
        spinnerW30SCBF.setSelection(idW30SCBF)
        spinnerW30SCBF.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                blendFctSCBF = when (spinnerW30SCBF.selectedItem) {
                    "ZERO" -> GLES32.GL_ZERO
                    "ONE" -> GLES32.GL_ONE
                    "SRC_COLOR" -> GLES32.GL_SRC_COLOR
                    "DST_COLOR" -> GLES32.GL_DST_COLOR
                    "ONE_MINUS_SRC_COLOR" -> GLES32.GL_ONE_MINUS_SRC_COLOR
                    "ONE_MINUS_DST_COLOR" -> GLES32.GL_ONE_MINUS_DST_COLOR
                    "SRC_ALPHA" -> GLES32.GL_SRC_ALPHA
                    "DST_ALPHA" -> GLES32.GL_DST_ALPHA
                    "ONE_MINUS_SRC_ALPHA" -> GLES32.GL_ONE_MINUS_SRC_ALPHA
                    "ONE_MINUS_DST_ALPHA" -> GLES32.GL_ONE_MINUS_DST_ALPHA
                    "CONSTANT_COLOR" -> GLES32.GL_CONSTANT_COLOR
                    "ONE_MINUS_CONSTANT_COLOR" -> GLES32.GL_ONE_MINUS_CONSTANT_COLOR
                    "CONSTANT_ALPHA" -> GLES32.GL_CONSTANT_ALPHA
                    "ONE_MINUS_CONSTANT_ALPHA" -> GLES32.GL_ONE_MINUS_CONSTANT_ALPHA
                    "SRC_ALPHA_SATURATE" -> GLES32.GL_SRC_ALPHA_SATURATE
                    else -> GLES32.GL_ONE
                }
            }
        }

        // ブレンドファクター(カラー先)
        val spinnerW30DCBF = view.findViewById<Spinner>(R.id.spinnerW30DCBF)
        val adapterW30DCBF = spinnerW30DCBF.adapter
        val strW30DCBF = when (blendFctDCBF) {
            GLES32.GL_ZERO -> "ZERO"
            GLES32.GL_ONE -> "ONE"
            GLES32.GL_SRC_COLOR -> "SRC_COLOR"
            GLES32.GL_DST_COLOR -> "DST_COLOR"
            GLES32.GL_ONE_MINUS_SRC_COLOR -> "ONE_MINUS_SRC_COLOR"
            GLES32.GL_ONE_MINUS_DST_COLOR -> "ONE_MINUS_DST_COLOR"
            GLES32.GL_SRC_ALPHA -> "SRC_ALPHA"
            GLES32.GL_DST_ALPHA -> "DST_ALPHA"
            GLES32.GL_ONE_MINUS_SRC_ALPHA -> "ONE_MINUS_SRC_ALPHA"
            GLES32.GL_ONE_MINUS_DST_ALPHA -> "ONE_MINUS_DST_ALPHA"
            GLES32.GL_CONSTANT_COLOR -> "CONSTANT_COLOR"
            GLES32.GL_ONE_MINUS_CONSTANT_COLOR -> "ONE_MINUS_CONSTANT_COLOR"
            GLES32.GL_CONSTANT_ALPHA -> "CONSTANT_ALPHA"
            GLES32.GL_ONE_MINUS_CONSTANT_ALPHA -> "ONE_MINUS_CONSTANT_ALPHA"
            GLES32.GL_SRC_ALPHA_SATURATE -> "SRC_ALPHA_SATURATE"
            else -> "ONE"
        }
        var idW30DCBF = -1
        (0 until adapterW30DCBF.count).forEach { i ->
            if ( adapterW30DCBF.getItem(i).equals(strW30DCBF) ) {
                idW30DCBF = i
                return@forEach
            }
        }
        spinnerW30DCBF.setSelection(idW30DCBF)
        spinnerW30DCBF.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                blendFctDCBF = when (spinnerW30DCBF.selectedItem) {
                    "ZERO" -> GLES32.GL_ZERO
                    "ONE" -> GLES32.GL_ONE
                    "SRC_COLOR" -> GLES32.GL_SRC_COLOR
                    "DST_COLOR" -> GLES32.GL_DST_COLOR
                    "ONE_MINUS_SRC_COLOR" -> GLES32.GL_ONE_MINUS_SRC_COLOR
                    "ONE_MINUS_DST_COLOR" -> GLES32.GL_ONE_MINUS_DST_COLOR
                    "SRC_ALPHA" -> GLES32.GL_SRC_ALPHA
                    "DST_ALPHA" -> GLES32.GL_DST_ALPHA
                    "ONE_MINUS_SRC_ALPHA" -> GLES32.GL_ONE_MINUS_SRC_ALPHA
                    "ONE_MINUS_DST_ALPHA" -> GLES32.GL_ONE_MINUS_DST_ALPHA
                    "CONSTANT_COLOR" -> GLES32.GL_CONSTANT_COLOR
                    "ONE_MINUS_CONSTANT_COLOR" -> GLES32.GL_ONE_MINUS_CONSTANT_COLOR
                    "CONSTANT_ALPHA" -> GLES32.GL_CONSTANT_ALPHA
                    "ONE_MINUS_CONSTANT_ALPHA" -> GLES32.GL_ONE_MINUS_CONSTANT_ALPHA
                    "SRC_ALPHA_SATURATE" -> GLES32.GL_SRC_ALPHA_SATURATE
                    else -> GLES32.GL_ONE
                }
            }
        }

        // ブレンドファクター(アルファ元)
        val spinnerW30SABF = view.findViewById<Spinner>(R.id.spinnerW30SABF)
        val adapterW30SABF = spinnerW30SABF.adapter
        val strW30SABF = when (blendFctSABF) {
            GLES32.GL_ZERO -> "ZERO"
            GLES32.GL_ONE -> "ONE"
            GLES32.GL_SRC_COLOR -> "SRC_COLOR"
            GLES32.GL_DST_COLOR -> "DST_COLOR"
            GLES32.GL_ONE_MINUS_SRC_COLOR -> "ONE_MINUS_SRC_COLOR"
            GLES32.GL_ONE_MINUS_DST_COLOR -> "ONE_MINUS_DST_COLOR"
            GLES32.GL_SRC_ALPHA -> "SRC_ALPHA"
            GLES32.GL_DST_ALPHA -> "DST_ALPHA"
            GLES32.GL_ONE_MINUS_SRC_ALPHA -> "ONE_MINUS_SRC_ALPHA"
            GLES32.GL_ONE_MINUS_DST_ALPHA -> "ONE_MINUS_DST_ALPHA"
            GLES32.GL_CONSTANT_COLOR -> "CONSTANT_COLOR"
            GLES32.GL_ONE_MINUS_CONSTANT_COLOR -> "ONE_MINUS_CONSTANT_COLOR"
            GLES32.GL_CONSTANT_ALPHA -> "CONSTANT_ALPHA"
            GLES32.GL_ONE_MINUS_CONSTANT_ALPHA -> "ONE_MINUS_CONSTANT_ALPHA"
            GLES32.GL_SRC_ALPHA_SATURATE -> "SRC_ALPHA_SATURATE"
            else -> "ONE"
        }
        var idW30SABF = -1
        (0 until adapterW30SABF.count).forEach { i ->
            if ( adapterW30SABF.getItem(i).equals(strW30SABF) ) {
                idW30SABF = i
                return@forEach
            }
        }
        spinnerW30SABF.setSelection(idW30SABF)
        spinnerW30SABF.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                blendFctSABF = when (spinnerW30SABF.selectedItem) {
                    "ZERO" -> GLES32.GL_ZERO
                    "ONE" -> GLES32.GL_ONE
                    "SRC_COLOR" -> GLES32.GL_SRC_COLOR
                    "DST_COLOR" -> GLES32.GL_DST_COLOR
                    "ONE_MINUS_SRC_COLOR" -> GLES32.GL_ONE_MINUS_SRC_COLOR
                    "ONE_MINUS_DST_COLOR" -> GLES32.GL_ONE_MINUS_DST_COLOR
                    "SRC_ALPHA" -> GLES32.GL_SRC_ALPHA
                    "DST_ALPHA" -> GLES32.GL_DST_ALPHA
                    "ONE_MINUS_SRC_ALPHA" -> GLES32.GL_ONE_MINUS_SRC_ALPHA
                    "ONE_MINUS_DST_ALPHA" -> GLES32.GL_ONE_MINUS_DST_ALPHA
                    "CONSTANT_COLOR" -> GLES32.GL_CONSTANT_COLOR
                    "ONE_MINUS_CONSTANT_COLOR" -> GLES32.GL_ONE_MINUS_CONSTANT_COLOR
                    "CONSTANT_ALPHA" -> GLES32.GL_CONSTANT_ALPHA
                    "ONE_MINUS_CONSTANT_ALPHA" -> GLES32.GL_ONE_MINUS_CONSTANT_ALPHA
                    "SRC_ALPHA_SATURATE" -> GLES32.GL_SRC_ALPHA_SATURATE
                    else -> GLES32.GL_ONE
                }
            }
        }

        // ブレンドファクター(アルファ先)
        val spinnerW30DABF = view.findViewById<Spinner>(R.id.spinnerW30DABF)
        val adapterW30DABF = spinnerW30DABF.adapter
        val strW30DABF = when (blendFctDABF) {
            GLES32.GL_ZERO -> "ZERO"
            GLES32.GL_ONE -> "ONE"
            GLES32.GL_SRC_COLOR -> "SRC_COLOR"
            GLES32.GL_DST_COLOR -> "DST_COLOR"
            GLES32.GL_ONE_MINUS_SRC_COLOR -> "ONE_MINUS_SRC_COLOR"
            GLES32.GL_ONE_MINUS_DST_COLOR -> "ONE_MINUS_DST_COLOR"
            GLES32.GL_SRC_ALPHA -> "SRC_ALPHA"
            GLES32.GL_DST_ALPHA -> "DST_ALPHA"
            GLES32.GL_ONE_MINUS_SRC_ALPHA -> "ONE_MINUS_SRC_ALPHA"
            GLES32.GL_ONE_MINUS_DST_ALPHA -> "ONE_MINUS_DST_ALPHA"
            GLES32.GL_CONSTANT_COLOR -> "CONSTANT_COLOR"
            GLES32.GL_ONE_MINUS_CONSTANT_COLOR -> "ONE_MINUS_CONSTANT_COLOR"
            GLES32.GL_CONSTANT_ALPHA -> "CONSTANT_ALPHA"
            GLES32.GL_ONE_MINUS_CONSTANT_ALPHA -> "ONE_MINUS_CONSTANT_ALPHA"
            GLES32.GL_SRC_ALPHA_SATURATE -> "SRC_ALPHA_SATURATE"
            else -> "ONE"
        }
        var idW30DABF = -1
        (0 until adapterW30DABF.count).forEach { i ->
            if ( adapterW30DABF.getItem(i).equals(strW30DABF) ) {
                idW30DABF = i
                return@forEach
            }
        }
        spinnerW30DABF.setSelection(idW30DABF)
        spinnerW30DABF.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                blendFctDABF = when (spinnerW30DABF.selectedItem) {
                    "ZERO" -> GLES32.GL_ZERO
                    "ONE" -> GLES32.GL_ONE
                    "SRC_COLOR" -> GLES32.GL_SRC_COLOR
                    "DST_COLOR" -> GLES32.GL_DST_COLOR
                    "ONE_MINUS_SRC_COLOR" -> GLES32.GL_ONE_MINUS_SRC_COLOR
                    "ONE_MINUS_DST_COLOR" -> GLES32.GL_ONE_MINUS_DST_COLOR
                    "SRC_ALPHA" -> GLES32.GL_SRC_ALPHA
                    "DST_ALPHA" -> GLES32.GL_DST_ALPHA
                    "ONE_MINUS_SRC_ALPHA" -> GLES32.GL_ONE_MINUS_SRC_ALPHA
                    "ONE_MINUS_DST_ALPHA" -> GLES32.GL_ONE_MINUS_DST_ALPHA
                    "CONSTANT_COLOR" -> GLES32.GL_CONSTANT_COLOR
                    "ONE_MINUS_CONSTANT_COLOR" -> GLES32.GL_ONE_MINUS_CONSTANT_COLOR
                    "CONSTANT_ALPHA" -> GLES32.GL_CONSTANT_ALPHA
                    "ONE_MINUS_CONSTANT_ALPHA" -> GLES32.GL_ONE_MINUS_CONSTANT_ALPHA
                    "SRC_ALPHA_SATURATE" -> GLES32.GL_SRC_ALPHA_SATURATE
                    else -> GLES32.GL_ONE
                }
            }
        }

        // 閉じるボタン
        val buttonW30CloseModel = view.findViewById<Button>(R.id.buttonW30CloseModel)
        buttonW30CloseModel.setOnClickListener {
            val intent = Intent().also {
                // ブレンド
                it.putExtra("BLEND", blend )
                // アルファ成分
                it.putExtra("VERTEX_ALPHA", vertexAlpha )
                // 方程式(カラー)
                it.putExtra("EQUATION_COLOR", equationColor )
                // 方程式(アルファ)
                it.putExtra("EQUATION_ALPHA", equationAlpha )
                // ブレンドファクター(カラー元)
                it.putExtra("BLEND_FCT_SCBF", blendFctSCBF )
                // ブレンドファクター(カラー先)
                it.putExtra("BLEND_FCT_DCBF", blendFctDCBF )
                // ブレンドファクター(アルファ元)
                it.putExtra("BLEND_FCT_SABF", blendFctSABF )
                // ブレンドファクター(アルファ先)
                it.putExtra("BLEND_FCT_DABF", blendFctDABF )
            }
            targetFragment?.onActivityResult(reqCode,0,intent)
            dismiss()
        }


        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle) =
                W30ModelDialog().apply {
                    arguments = Bundle().also {
                        // リクエストコード
                        it.putInt("REQ_CODE", bundle.getInt("REQ_CODE") ?: -1)
                        // ブレンド
                        it.putBoolean("BLEND", bundle.getBoolean("BLEND") ?: false )
                        // アルファ成分
                        it.putFloat("VERTEX_ALPHA", bundle.getFloat("VERTEX_ALPHA") ?: 0f)
                        // 方程式(カラー)
                        it.putInt("EQUATION_COLOR", bundle.getInt("EQUATION_COLOR") ?: GLES32.GL_FUNC_ADD )
                        // 方程式(アルファ)
                        it.putInt("EQUATION_ALPHA", bundle.getInt("EQUATION_ALPHA") ?: GLES32.GL_FUNC_ADD )
                        // ブレンドファクター(カラー元)
                        it.putInt("BLEND_FCT_SCBF", bundle.getInt("BLEND_FCT_SCBF") ?: GLES32.GL_ONE )
                        // ブレンドファクター(カラー先)
                        it.putInt("BLEND_FCT_DCBF", bundle.getInt("BLEND_FCT_DCBF") ?: GLES32.GL_ONE )
                        // ブレンドファクター(アルファ元)
                        it.putInt("BLEND_FCT_SABF", bundle.getInt("BLEND_FCT_SABF") ?: GLES32.GL_ONE )
                        // ブレンドファクター(アルファ先)
                        it.putInt("BLEND_FCT_DABF", bundle.getInt("BLEND_FCT_DABF") ?: GLES32.GL_ONE )
                    }
                }
    }
}