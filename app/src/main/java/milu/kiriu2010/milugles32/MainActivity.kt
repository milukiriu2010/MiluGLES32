package milu.kiriu2010.milugles32

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import milu.kiriu2010.milugles32.es32x01.ES32x01Activity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 描画GLSL ES32x01ページへ遷移
        btnES32x01.transformationMethod = null
        btnES32x01.setOnClickListener {
            val intent = Intent(this, ES32x01Activity::class.java)
            startActivity(intent)
        }
    }
}
