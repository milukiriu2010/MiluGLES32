package milu.kiriu2010.milugles32.w8x

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w8x.w81.W81Fragment
import milu.kiriu2010.milugles32.w8x.w82.W82Fragment
import milu.kiriu2010.milugles32.w8x.w83.W83Fragment
import milu.kiriu2010.milugles32.w8x.w84.W84Fragment
import milu.kiriu2010.milugles32.w8x.w86.W86Fragment
import milu.kiriu2010.milugles32.w8x.w87.W87Fragment
import milu.kiriu2010.milugles32.w8x.w89.W89Fragment

class W8xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, W83Fragment.newInstance(), "xyz")
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
            // スフィア環境マッピング
            R.id.w89 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w89") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W89Fragment.newInstance(), "w89")
                        .commit()
                }
                true
            }
            // フラットシェーディング
            R.id.w87 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w87") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W87Fragment.newInstance(), "w87")
                        .commit()
                }
                true
            }
            // 色取得
            R.id.w86 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w86") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W86Fragment.newInstance(), "w86")
                        .commit()
                }
                true
            }
            // MRT
            R.id.w84 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w84") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W84Fragment.newInstance(), "w84")
                        .commit()
                }
                true
            }
            // GPGPU
            R.id.w83 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w83") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W83Fragment.newInstance(), "w83")
                        .commit()
                }
                true
            }
            // VBO逐次更新:パーティクル
            R.id.w82 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w82") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W82Fragment.newInstance(), "w82")
                        .commit()
                }
                true
            }
            // VBO逐次更新
            R.id.w81 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("w81") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, W81Fragment.newInstance(), "w81")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
