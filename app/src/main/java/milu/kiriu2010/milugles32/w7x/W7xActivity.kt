package milu.kiriu2010.milugles32.w7x

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w7x.w75.W75Fragment
import milu.kiriu2010.milugles32.w7x.w76.W76Fragment
import milu.kiriu2010.milugles32.w7x.w77.W77Fragment

class W7xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, W75Fragment.newInstance(), "xyz")
                .commit()
        }

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w7x, menu)
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
            // ラインシェード
            R.id.w77 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w77") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W77Fragment.newInstance(), "w77")
                        .commit()
                }
                true
            }
            // ハーフトーン
            R.id.w76 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w76") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W76Fragment.newInstance(), "w76")
                        .commit()
                }
                true
            }
            // インスタンシング
            R.id.w75 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w75") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W75Fragment.newInstance(), "w75")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
