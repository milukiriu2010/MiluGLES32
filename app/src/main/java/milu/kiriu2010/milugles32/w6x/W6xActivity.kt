package milu.kiriu2010.milugles32.w6x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w6x.w60.W60Fragment
import milu.kiriu2010.milugles32.w6x.w61.W61Fragment
import milu.kiriu2010.milugles32.w6x.w62.W62Fragment
import milu.kiriu2010.milugles32.w6x.w63.W63Fragment
import milu.kiriu2010.milugles32.w6x.w64.W64Fragment
import milu.kiriu2010.milugles32.w6x.w65.W65Fragment
import milu.kiriu2010.milugles32.w6x.w66.W66Fragment
import milu.kiriu2010.milugles32.w6x.w67.W67Fragment
import milu.kiriu2010.milugles32.w6x.w68.W68Fragment
import milu.kiriu2010.milugles32.w6x.w69.W69Fragment

class W6xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, W68Fragment.newInstance(), "xyz")
                .commit()
        }

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w6x, menu)
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
            // 深度値
            R.id.w69 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w69") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W69Fragment.newInstance(), "w69")
                        .commit()
                }
                true
            }
            // ゴッドレイ
            R.id.w68 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w68") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W68Fragment.newInstance(), "w68")
                        .commit()
                }
                true
            }
            // ズームブラー
            R.id.w67 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w67") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W67Fragment.newInstance(), "w67")
                        .commit()
                }
                true
            }
            // mosaicフィルタ
            R.id.w66 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w66") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W66Fragment.newInstance(), "w66")
                        .commit()
                }
                true
            }
            // 後光表面化散乱
            R.id.w65 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w65") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W65Fragment.newInstance(), "w65")
                        .commit()
                }
                true
            }
            // リムライティング
            R.id.w64 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w64") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W64Fragment.newInstance(), "w64")
                        .commit()
                }
                true
            }
            // 半球ライティング
            R.id.w63 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w63") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W63Fragment.newInstance(), "w63")
                        .commit()
                }
                true
            }
            // ステンシル鏡面
            R.id.w62 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w62") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W62Fragment.newInstance(), "w62")
                        .commit()
                }
                true
            }
            // パーティクルフォグ
            R.id.w61 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w61") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W61Fragment.newInstance(), "w61")
                        .commit()
                }
                true
            }
            // 距離フォグ
            R.id.w60 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w60") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W60Fragment.newInstance(), "w60")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
