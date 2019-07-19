package milu.kiriu2010.milugles32.w7x

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w7x.w70.W70Fragment
import milu.kiriu2010.milugles32.w7x.w71.W71Fragment
import milu.kiriu2010.milugles32.w7x.w72.W72Fragment
import milu.kiriu2010.milugles32.w7x.w74.W74Fragment
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
                .replace(R.id.frameLayout, W72Fragment.newInstance(), "xyz")
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
            // 異方性フィルタリング
            R.id.w74 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w74") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W74Fragment.newInstance(), "w74")
                        .commit()
                }
                true
            }
            // 浮動小数点数VTF
            R.id.w72 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w72") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W72Fragment.newInstance(), "w72")
                        .commit()
                }
                true
            }
            // 頂点テクスチャフェッチ
            R.id.w71 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w71") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W71Fragment.newInstance(), "w71")
                        .commit()
                }
                true
            }
            // 浮動小数点数テクスチャ
            R.id.w70 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w70") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W70Fragment.newInstance(), "w70")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
