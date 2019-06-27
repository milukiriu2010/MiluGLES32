package milu.kiriu2010.milugles32.w3x

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w3x.w30.W30Fragment
import milu.kiriu2010.milugles32.w3x.w32.W32Fragment
import milu.kiriu2010.milugles32.w3x.w33.W33Fragment
import milu.kiriu2010.milugles32.w3x.w34.W34Fragment
import milu.kiriu2010.milugles32.w3x.w35.W35Fragment
import milu.kiriu2010.milugles32.w3x.w36.W36Fragment
import milu.kiriu2010.milugles32.w3x.w37.W37Fragment
import milu.kiriu2010.milugles32.w3x.w38.W38Fragment
import milu.kiriu2010.milugles32.w3x.w39.W39Fragment
import milu.kiriu2010.milugles32.w8x.w81.W81Fragment

class W3xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, W39Fragment.newInstance(), "xyz")
                .commit()
        }

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w3x, menu)
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
            // アウトライン
            R.id.w39 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w39") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W39Fragment.newInstance(), "w39")
                        .commit()
                }
                true
            }
            // ステンシルバッファ
            R.id.w38 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w38") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W38Fragment.newInstance(), "w38")
                        .commit()
                }
                true
            }
            // ポイントスプライト
            R.id.w37 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w37") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W37Fragment.newInstance(), "w37")
                        .commit()
                }
                true
            }
            // 点・線
            R.id.w36 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w36") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W36Fragment.newInstance(), "w36")
                        .commit()
                }
                true
            }
            // ビルボード
            R.id.w35 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w35") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W35Fragment.newInstance(), "w35")
                        .commit()
                }
                true
            }
            // 球面線形補間
            R.id.w34 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w34") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W34Fragment.newInstance(), "w34")
                        .commit()
                }
                true
            }
            // マウスによる回転
            R.id.w33 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w33") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W33Fragment.newInstance(), "w33")
                        .commit()
                }
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
            // ブレンドファクター
            R.id.w30 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w30") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W30Fragment.newInstance(), "w30")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
