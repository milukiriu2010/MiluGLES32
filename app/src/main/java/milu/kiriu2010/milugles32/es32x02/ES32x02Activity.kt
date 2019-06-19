package milu.kiriu2010.milugles32.es32x02

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.es32x02.a10.A10Fragment
import milu.kiriu2010.milugles32.es32x02.a11.A11Fragment
import milu.kiriu2010.milugles32.es32x02.a12.A12Fragment
import milu.kiriu2010.milugles32.es32x02.a13.A13Fragment

class ES32x02Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x02)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, A13Fragment.newInstance(), "xyz")
                .commit()
        }

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_es32x01, menu)
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
            // centroid
            R.id.es32_a13 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("a13") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, A13Fragment.newInstance(), "a13")
                        .commit()
                }
                true
            }
            // derivative関数
            R.id.es32_a12 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("a12") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, A12Fragment.newInstance(), "a12")
                        .commit()
                }
                true
            }
            // MRT
            R.id.es32_a11 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("a11") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, A11Fragment.newInstance(), "a11")
                        .commit()
                }
                true
            }
            // Sampler Object
            R.id.es32_a10 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("a10") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, A10Fragment.newInstance(), "a10")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
