package milu.kiriu2010.milugles32.g1x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.g1x.g10.G10Fragment
import milu.kiriu2010.milugles32.g1x.g11.G11Fragment
import milu.kiriu2010.milugles32.g1x.g12.G12Fragment
import milu.kiriu2010.milugles32.g1x.g13.G13Fragment
import milu.kiriu2010.milugles32.g1x.g14.G14Fragment
import milu.kiriu2010.milugles32.g1x.g15.G15Fragment
import milu.kiriu2010.milugles32.g1x.g16.G16Fragment
import milu.kiriu2010.milugles32.g1x.g17.G17Fragment
import milu.kiriu2010.milugles32.g1x.g18.G18Fragment
import milu.kiriu2010.milugles32.g1x.g19.G19Fragment
import milu.kiriu2010.milugles32.g1x.g20.G20Fragment

class G1xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        // 初期表示のフラグメントを設定
        changeFragment(G10Fragment.newInstance())

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_g1x, menu)
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
            // レイマーチング(シャドウ)
            R.id.g20 -> {
                changeFragment(G20Fragment.newInstance())
                true
            }
            // レイマーチング(テクスチャ)
            R.id.g19 -> {
                changeFragment(G19Fragment.newInstance())
                true
            }
            // レイマーチング(ツイスト)
            R.id.g18 -> {
                changeFragment(G18Fragment.newInstance())
                true
            }
            // レイマーチング(回転)
            R.id.g17 -> {
                changeFragment(G17Fragment.newInstance())
                true
            }
            // レイマーチング(スムース補間)
            R.id.g16 -> {
                changeFragment(G16Fragment.newInstance())
                true
            }
            // レイマーチング(リング+板)
            R.id.g15 -> {
                changeFragment(G15Fragment.newInstance())
                true
            }
            // レイマーチング(トーラス+床)
            R.id.g14 -> {
                changeFragment(G14Fragment.newInstance())
                true
            }
            // レイマーチング(ボックス)
            R.id.g13 -> {
                changeFragment(G13Fragment.newInstance())
                true
            }
            // レイマーチング(複製)
            R.id.g12 -> {
                changeFragment(G12Fragment.newInstance())
                true
            }
            // レイマーチング(視野角)
            R.id.g11 -> {
                changeFragment(G11Fragment.newInstance())
                true
            }
            // レイマーチング(球体+ライティング)
            R.id.g10 -> {
                changeFragment(G10Fragment.newInstance())
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
