package milu.kiriu2010.milugles32.g1x

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.g1x.g10.G10Fragment
import milu.kiriu2010.milugles32.g1x.g11.G11Fragment
import milu.kiriu2010.milugles32.g1x.g12.G12Fragment
import milu.kiriu2010.milugles32.g1x.g13.G13Fragment
import milu.kiriu2010.milugles32.g1x.g14.G14Fragment

class G1xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, G14Fragment.newInstance(), "xyz")
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
            // レイマーチング(トーラス+床)
            R.id.g13 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("g14") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, G14Fragment.newInstance(), "g14")
                        .commit()
                }
                true
            }
            // レイマーチング(ボックス)
            R.id.g13 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("g13") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, G13Fragment.newInstance(), "g13")
                        .commit()
                }
                true
            }
            // レイマーチング(複製)
            R.id.g12 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("g12") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, G12Fragment.newInstance(), "g12")
                        .commit()
                }
                true
            }
            // レイマーチング(視野角)
            R.id.g11 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("g11") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, G11Fragment.newInstance(), "g11")
                        .commit()
                }
                true
            }
            // レイマーチング(球体+ライティング)
            R.id.g10 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("g10") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, G10Fragment.newInstance(), "g10")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
