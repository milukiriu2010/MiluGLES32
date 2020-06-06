package milu.kiriu2010.milugles32.es32x01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import milu.kiriu2010.milugles32.es32x01.a01.A01Fragment
import milu.kiriu2010.milugles32.es32x01.a06.A06Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.es32x01.a03.A03Fragment
import milu.kiriu2010.milugles32.es32x01.a04.A04Fragment
import milu.kiriu2010.milugles32.es32x01.a05.A05Fragment
import milu.kiriu2010.milugles32.es32x01.a07.A07Fragment
import milu.kiriu2010.milugles32.es32x01.a08.A08Fragment
import milu.kiriu2010.milugles32.es32x01.a09.A09Fragment

class ES32x01Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        // 初期表示のフラグメントを設定
        changeFragment(A07Fragment.newInstance())

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
                changeFragment(A09Fragment.newInstance())
                true
            }
            // gl_VertexID
            R.id.es32_a08 -> {
                changeFragment(A08Fragment.newInstance())
                true
            }
            // インスタンシング
            R.id.es32_a07 -> {
                changeFragment(A07Fragment.newInstance())
                true
            }
            // VAO
            R.id.es32_a06 -> {
                changeFragment(A06Fragment.newInstance())
                true
            }
            // flat補間
            R.id.es32_a05 -> {
                changeFragment(A05Fragment.newInstance())
                true
            }
            // layout
            R.id.es32_a04 -> {
                changeFragment(A04Fragment.newInstance())
                true
            }
            // GLSL ES3.2
            R.id.es32_a03 -> {
                changeFragment(A03Fragment.newInstance())
                true
            }
            // 回転(立方体)01_ES32
            R.id.es32_a01 -> {
                changeFragment(A01Fragment.newInstance())
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
