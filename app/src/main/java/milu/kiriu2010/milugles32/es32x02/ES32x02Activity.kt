package milu.kiriu2010.milugles32.es32x02

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.es32x02.a10.A10Fragment
import milu.kiriu2010.milugles32.es32x02.a11.A11Fragment
import milu.kiriu2010.milugles32.es32x02.a12.A12Fragment
import milu.kiriu2010.milugles32.es32x02.a13.A13Fragment
import milu.kiriu2010.milugles32.es32x02.a14.A14Fragment
import milu.kiriu2010.milugles32.es32x02.a15.A15Fragment

class ES32x02Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        // 初期表示のフラグメントを設定
        changeFragment(A15Fragment.newInstance())

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_es32x02, menu)
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
            // Transform Feedback(GPGPU)
            R.id.es32_a15 -> {
                changeFragment(A15Fragment.newInstance())
                true
            }
            // Transform Feedback
            R.id.es32_a14 -> {
                changeFragment(A14Fragment.newInstance())
                true
            }
            // centroid
            R.id.es32_a13 -> {
                changeFragment(A13Fragment.newInstance())
                true
            }
            // derivative関数
            R.id.es32_a12 -> {
                changeFragment(A12Fragment.newInstance())
                true
            }
            // MRT
            R.id.es32_a11 -> {
                changeFragment(A11Fragment.newInstance())
                true
            }
            // Sampler Object
            R.id.es32_a10 -> {
                changeFragment(A10Fragment.newInstance())
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // 表示するフラグメントを切り替える
    private fun changeFragment(fragment: Fragment) {
        // 現在表示しているフラグメントをスタックから外す
        supportFragmentManager.popBackStack()
        // 選択したフラグメントを表示する
        if ( supportFragmentManager.findFragmentByTag(fragment.javaClass.simpleName) == null ) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment, fragment.javaClass.simpleName)
                .commit()
        }
    }
}
