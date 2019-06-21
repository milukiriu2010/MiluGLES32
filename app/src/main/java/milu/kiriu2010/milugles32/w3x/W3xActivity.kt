package milu.kiriu2010.milugles32.w3x

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w3x.w32.W32Fragment
import milu.kiriu2010.milugles32.w8x.w81.W81Fragment

class W3xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, W32Fragment.newInstance(), "xyz")
                .commit()
        }

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w8x, menu)
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
            // クォータニオン
            R.id.w32 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w32") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W32Fragment.newInstance(), "w32")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
