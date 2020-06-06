package milu.kiriu2010.milugles32.g0x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.g0x.g01.G01Fragment
import milu.kiriu2010.milugles32.g0x.g02.G02Fragment
import milu.kiriu2010.milugles32.g0x.g03.G03Fragment
import milu.kiriu2010.milugles32.g0x.g04.G04Fragment
import milu.kiriu2010.milugles32.g0x.g05.G05Fragment
import milu.kiriu2010.milugles32.g0x.g06.G06Fragment
import milu.kiriu2010.milugles32.g0x.g07.G07Fragment
import milu.kiriu2010.milugles32.g0x.g08.G08Fragment
import milu.kiriu2010.milugles32.g0x.g09.G09Fragment

class G0xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        // 初期表示のフラグメントを設定
        changeFragment(G09Fragment.newInstance())

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_g0x, menu)
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
            // レイマーチング(球体)
            R.id.g09 -> {
                changeFragment(G09Fragment.newInstance())
                true
            }
            // レイマーチング(RGB)
            R.id.g08 -> {
                changeFragment(G08Fragment.newInstance())
                true
            }
            // ノイズ
            R.id.g07 -> {
                changeFragment(G07Fragment.newInstance())
                true
            }
            // ジュリア集合
            R.id.g06 -> {
                changeFragment(G06Fragment.newInstance())
                true
            }
            // マンデルブロ集合
            R.id.g05 -> {
                changeFragment(G05Fragment.newInstance())
                true
            }
            // 様々な図形
            R.id.g04 -> {
                changeFragment(G04Fragment.newInstance())
                true
            }
            // オーブ
            R.id.g03 -> {
                changeFragment(G03Fragment.newInstance())
                true
            }
            // 同心円
            R.id.g02 -> {
                changeFragment(G02Fragment.newInstance())
                true
            }
            // GLSL
            R.id.g01 -> {
                changeFragment(G01Fragment.newInstance())
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
