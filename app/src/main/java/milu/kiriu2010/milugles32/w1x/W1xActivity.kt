package milu.kiriu2010.milugles32.w1x

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w1x.w15.W15Fragment
import milu.kiriu2010.milugles32.w1x.w16.W16Fragment
import milu.kiriu2010.milugles32.w1x.w17.W17Fragment
import milu.kiriu2010.milugles32.w1x.w18.W18Fragment
import milu.kiriu2010.milugles32.w1x.w19.W19Fragment

class W1xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, W19Fragment.newInstance(), "xyz")
                .commit()
        }

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w1x, menu)
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
            // カリング・深度テスト
            R.id.w19 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w19") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W19Fragment.newInstance(), "w19")
                        .commit()
                }
                true
            }
            // IBO
            R.id.w18 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w18") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W18Fragment.newInstance(), "w18")
                        .commit()
                }
                true
            }
            // 異動・回転・拡大縮小
            R.id.w17 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w17") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W17Fragment.newInstance(), "w17")
                        .commit()
                }
                true
            }
            // 複数モデル
            R.id.w16 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w16") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W16Fragment.newInstance(), "w16")
                        .commit()
                }
                true
            }
            // 頂点色
            R.id.w15 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w15") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W15Fragment.newInstance(), "w15")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
