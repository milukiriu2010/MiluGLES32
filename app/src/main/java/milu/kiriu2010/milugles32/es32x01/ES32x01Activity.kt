package milu.kiriu2010.milugles32.es32x01

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import milu.kiriu2010.milugles32.es32x01.a01.A01Fragment
import milu.kiriu2010.milugles32.es32x01.a06.A06Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.es32x01.a03.A03Fragment
import milu.kiriu2010.milugles32.es32x01.a09.A09Fragment

class ES32x01Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        supportFragmentManager.popBackStack()
        if (supportFragmentManager.findFragmentByTag("xyz") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, A09Fragment.newInstance(), "xyz")
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
            // UBO
            R.id.es32_a09 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("a09") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, A09Fragment.newInstance(), "a09")
                        .commit()
                }
                true
            }
            // VAO
            R.id.es32_a06 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("a06") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, A06Fragment.newInstance(), "a06")
                        .commit()
                }
                true
            }
            // GLSL ES3.2
            R.id.es32_a03 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("a03") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, A03Fragment.newInstance(), "a03")
                        .commit()
                }
                true
            }
            // 回転(立方体)01_ES32
            R.id.es32_a01 -> {
                supportFragmentManager.popBackStack()
                if (supportFragmentManager.findFragmentByTag("a01") == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, A01Fragment.newInstance(), "a01")
                        .commit()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
