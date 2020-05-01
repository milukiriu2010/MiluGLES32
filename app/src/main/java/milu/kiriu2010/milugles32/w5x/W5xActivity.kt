package milu.kiriu2010.milugles32.w5x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w5x.w50.W50Fragment
import milu.kiriu2010.milugles32.w5x.w51.W51Fragment
import milu.kiriu2010.milugles32.w5x.w53.W53Fragment
import milu.kiriu2010.milugles32.w5x.w54.W54Fragment
import milu.kiriu2010.milugles32.w5x.w55.W55Fragment
import milu.kiriu2010.milugles32.w5x.w56.W56Fragment
import milu.kiriu2010.milugles32.w5x.w57.W57Fragment
import milu.kiriu2010.milugles32.w5x.w58.W58Fragment
import milu.kiriu2010.milugles32.w5x.w59.W59Fragment

class W5xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, W59Fragment.newInstance(), "xyz")
                .commit()
        }

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w5x, menu)
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
            // 被写界深度
            R.id.w59 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w59") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W59Fragment.newInstance(), "w59")
                        .commit()
                }
                true
            }
            // グレアフィルタ
            R.id.w58 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w58") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W58Fragment.newInstance(), "w58")
                        .commit()
                }
                true
            }
            // gaussianフィルタ
            R.id.w57 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w57") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W57Fragment.newInstance(), "w57")
                        .commit()
                }
                true
            }
            // laplacianフィルタ
            R.id.w56 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w56") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W56Fragment.newInstance(), "w56")
                        .commit()
                }
                true
            }
            // sobelフィルタ
            R.id.w55 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w55") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W55Fragment.newInstance(), "w55")
                        .commit()
                }
                true
            }
            // セピア調変換
            R.id.w54 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w54") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W54Fragment.newInstance(), "w54")
                        .commit()
                }
                true
            }
            // グレイスケール
            R.id.w53 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w53") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W53Fragment.newInstance(), "w53")
                        .commit()
                }
                true
            }
            // シャドウマッピング
            R.id.w51 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w51") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W51Fragment.newInstance(), "w51")
                        .commit()
                }
                true
            }
            // 光学迷彩
            R.id.w50 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w50") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W50Fragment.newInstance(), "w50")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
