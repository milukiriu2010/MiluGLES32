package milu.kiriu2010.milugles32.w2x

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w2x.w20.W20Fragment
import milu.kiriu2010.milugles32.w2x.w21.W21Fragment
import milu.kiriu2010.milugles32.w2x.w22.W22Fragment
import milu.kiriu2010.milugles32.w2x.w23.W23Fragment
import milu.kiriu2010.milugles32.w2x.w24.W24Fragment
import milu.kiriu2010.milugles32.w2x.w25.W25Fragment
import milu.kiriu2010.milugles32.w2x.w26.W26Fragment
import milu.kiriu2010.milugles32.w2x.w27.W27Fragment
import milu.kiriu2010.milugles32.w2x.w28.W28Fragment
import milu.kiriu2010.milugles32.w2x.w29.W29Fragment

class W2xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, W29Fragment.newInstance(), "xyz")
                .commit()
        }

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w2x, menu)
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
            // アルファブレンド
            R.id.w29 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w29") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W29Fragment.newInstance(), "w29")
                        .commit()
                }
                true
            }
            // テクスチャパラメータ
            R.id.w28 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w28") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W28Fragment.newInstance(), "w28")
                        .commit()
                }
                true
            }
            // マルチテクスチャ
            R.id.w27 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w27") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W27Fragment.newInstance(), "w27")
                        .commit()
                }
                true
            }
            // テクスチャ
            R.id.w26 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w26") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W26Fragment.newInstance(), "w26")
                        .commit()
                }
                true
            }
            // 点光源
            R.id.w25 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w25") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W25Fragment.newInstance(), "w25")
                        .commit()
                }
                true
            }
            // フォンシェーディング
            R.id.w24 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w24") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W24Fragment.newInstance(), "w24")
                        .commit()
                }
                true
            }
            // 反射光
            R.id.w23 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w23") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W23Fragment.newInstance(), "w23")
                        .commit()
                }
                true
            }
            // 環境光
            R.id.w22 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w22") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W22Fragment.newInstance(), "w22")
                        .commit()
                }
                true
            }
            // 平行光源
            R.id.w21 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w21") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W21Fragment.newInstance(), "w21")
                        .commit()
                }
                true
            }
            // トーラス
            R.id.w20 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w20") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W20Fragment.newInstance(), "w20")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
