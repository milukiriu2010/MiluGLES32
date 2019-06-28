package milu.kiriu2010.milugles32.g1x

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.g1x.g01.G01Fragment
import milu.kiriu2010.milugles32.g1x.g02.G02Fragment
import milu.kiriu2010.milugles32.g1x.g03.G03Fragment
import milu.kiriu2010.milugles32.g1x.g04.G04Fragment
import milu.kiriu2010.milugles32.g1x.g05.G05Fragment
import milu.kiriu2010.milugles32.g1x.g06.G06Fragment
import milu.kiriu2010.milugles32.g1x.g07.G07Fragment

class G1xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, G07Fragment.newInstance(), "xyz")
                .commit()
        }

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_g1x, menu)
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
            // ノイズ
            R.id.g07 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("g07") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, G07Fragment.newInstance(), "g07")
                        .commit()
                }
                true
            }
            // ジュリア集合
            R.id.g06 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("g06") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, G06Fragment.newInstance(), "g06")
                        .commit()
                }
                true
            }
            // マンデルブロ集合
            R.id.g05 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("g05") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, G05Fragment.newInstance(), "g05")
                        .commit()
                }
                true
            }
            // 様々な図形
            R.id.g04 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("g04") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, G04Fragment.newInstance(), "g04")
                        .commit()
                }
                true
            }
            // オーブ
            R.id.g03 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("g03") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, G03Fragment.newInstance(), "g03")
                        .commit()
                }
                true
            }
            // 同心円
            R.id.g02 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("g02") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, G02Fragment.newInstance(), "g02")
                        .commit()
                }
                true
            }
            // GLSL
            R.id.g01 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("g01") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, G01Fragment.newInstance(), "g01")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
