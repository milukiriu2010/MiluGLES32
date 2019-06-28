package milu.kiriu2010.milugles32

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import milu.kiriu2010.milugles32.es32x01.ES32x01Activity
import milu.kiriu2010.milugles32.es32x02.ES32x02Activity
import milu.kiriu2010.milugles32.g0x.G0xActivity
import milu.kiriu2010.milugles32.g1x.G1xActivity
import milu.kiriu2010.milugles32.w1x.W1xActivity
import milu.kiriu2010.milugles32.w2x.W2xActivity
import milu.kiriu2010.milugles32.w3x.W3xActivity
import milu.kiriu2010.milugles32.w4x.W4xActivity
import milu.kiriu2010.milugles32.w5x.W5xActivity
import milu.kiriu2010.milugles32.w6x.W6xActivity
import milu.kiriu2010.milugles32.w8x.W8xActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 描画 ES32x01ページへ遷移
        btnES32x01.transformationMethod = null
        btnES32x01.setOnClickListener {
            val intent = Intent(this, ES32x01Activity::class.java)
            startActivity(intent)
        }

        // 描画 ES32x02ページへ遷移
        btnES32x02.transformationMethod = null
        btnES32x02.setOnClickListener {
            val intent = Intent(this, ES32x02Activity::class.java)
            startActivity(intent)
        }

        // 描画 G0xページへ遷移
        btnG0X.transformationMethod = null
        btnG0X.setOnClickListener {
            val intent = Intent(this, G0xActivity::class.java)
            startActivity(intent)
        }

        // 描画 G1xページへ遷移
        btnG1X.transformationMethod = null
        btnG1X.setOnClickListener {
            val intent = Intent(this, G1xActivity::class.java)
            startActivity(intent)
        }

        // 描画 W1xページへ遷移
        btnW1X.transformationMethod = null
        btnW1X.setOnClickListener {
            val intent = Intent(this, W1xActivity::class.java)
            startActivity(intent)
        }

        // 描画 W2xページへ遷移
        btnW2X.transformationMethod = null
        btnW2X.setOnClickListener {
            val intent = Intent(this, W2xActivity::class.java)
            startActivity(intent)
        }

        // 描画 W3xページへ遷移
        btnW3X.transformationMethod = null
        btnW3X.setOnClickListener {
            val intent = Intent(this, W3xActivity::class.java)
            startActivity(intent)
        }

        // 描画 W4xページへ遷移
        btnW4X.transformationMethod = null
        btnW4X.setOnClickListener {
            val intent = Intent(this, W4xActivity::class.java)
            startActivity(intent)
        }

        // 描画 W5xページへ遷移
        btnW5X.transformationMethod = null
        btnW5X.setOnClickListener {
            val intent = Intent(this, W5xActivity::class.java)
            startActivity(intent)
        }

        // 描画 W6xページへ遷移
        btnW6X.transformationMethod = null
        btnW6X.setOnClickListener {
            val intent = Intent(this, W6xActivity::class.java)
            startActivity(intent)
        }

        // 描画 W8xページへ遷移
        btnW8X.transformationMethod = null
        btnW8X.setOnClickListener {
            val intent = Intent(this, W8xActivity::class.java)
            startActivity(intent)
        }
    }
}
