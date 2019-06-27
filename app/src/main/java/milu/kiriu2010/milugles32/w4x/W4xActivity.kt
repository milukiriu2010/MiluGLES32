package milu.kiriu2010.milugles32.w4x

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w4x.w40.W40Fragment
import milu.kiriu2010.milugles32.w4x.w41.W41Fragment
import milu.kiriu2010.milugles32.w4x.w42.W42Fragment
import milu.kiriu2010.milugles32.w4x.w43.W43Fragment
import milu.kiriu2010.milugles32.w4x.w44.W44Fragment
import milu.kiriu2010.milugles32.w4x.w45.W45Fragment
import milu.kiriu2010.milugles32.w4x.w46.W46Fragment
import milu.kiriu2010.milugles32.w4x.w47.W47Fragment
import milu.kiriu2010.milugles32.w4x.w48.W48Fragment
import milu.kiriu2010.milugles32.w4x.w49.W49Fragment

class W4xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, W49Fragment.newInstance(), "xyz")
                .commit()
        }

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w4x, menu)
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
            // 射影テクスチャマッピング
            R.id.w49 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w49") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W49Fragment.newInstance(), "w49")
                        .commit()
                }
                true
            }
            // トゥーンレンダリング
            R.id.w48 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w48") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W48Fragment.newInstance(), "w48")
                        .commit()
                }
                true
            }
            // 動的キューブマッピング
            R.id.w47 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w47") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W47Fragment.newInstance(), "w47")
                        .commit()
                }
                true
            }
            // 屈折マッピング
            R.id.w46 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w46") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W46Fragment.newInstance(), "w46")
                        .commit()
                }
                true
            }
            // キューブ環境バンプマッピング
            R.id.w45 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w45") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W45Fragment.newInstance(), "w45")
                        .commit()
                }
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
            // 視差マッピング
            R.id.w43 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w43") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W43Fragment.newInstance(), "w43")
                        .commit()
                }
                true
            }
            // バンプマッピング
            R.id.w42 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w42") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W42Fragment.newInstance(), "w42")
                        .commit()
                }
                true
            }
            // ブラーフィルタ
            R.id.w41 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w41") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W41Fragment.newInstance(), "w41")
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
