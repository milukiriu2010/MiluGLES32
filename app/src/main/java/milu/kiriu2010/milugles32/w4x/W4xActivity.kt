package milu.kiriu2010.milugles32.w4x

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w4x.w40.W40Fragment
import milu.kiriu2010.milugles32.w4x.w44.W44Fragment

class W4xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, W44Fragment.newInstance(), "xyz")
                .commit()
        }

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w6x, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // アクションバーのアイコンがタップされると呼ばれる
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // 前画面に戻る
            android.R.id.home -> {
                finish()
                true
            }
            // キューブ環境マッピング
            R.id.w44 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w44") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W44Fragment.newInstance(), "w44")
                        .commit()
                }
                true
            }
            // フレームバッファ
            R.id.w40 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w40") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W40Fragment.newInstance(), "w40")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
