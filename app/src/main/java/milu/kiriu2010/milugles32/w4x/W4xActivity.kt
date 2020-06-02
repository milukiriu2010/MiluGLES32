package milu.kiriu2010.milugles32.w4x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w4x.w40.W40Fragment
import milu.kiriu2010.milugles32.w4x.w41.W41Fragment
import milu.kiriu2010.milugles32.w4x.w42.W42Fragment
import milu.kiriu2010.milugles32.w4x.w43.W43Fragment
import milu.kiriu2010.milugles32.w4x.w44.W44Fragment
import milu.kiriu2010.milugles32.w4x.w45.W45Fragment
import milu.kiriu2010.milugles32.w4x.w46.W46Fragment
import milu.kiriu2010.milugles32.w4x.w47.W47Fragment
import milu.kiriu2010.milugles32.w4x.w48.W48Fragment
import milu.kiriu2010.milugles32.w4x.w49.W49Fragment

class W4xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        // 初期表示のフラグメントを設定
        changeFragment(W49Fragment.newInstance())

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w4x, menu)
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
            // 射影テクスチャマッピング
            R.id.w49 -> {
                changeFragment(W49Fragment.newInstance())
                true
            }
            // トゥーンレンダリング
            R.id.w48 -> {
                changeFragment(W48Fragment.newInstance())
                true
            }
            // 動的キューブマッピング
            R.id.w47 -> {
                changeFragment(W47Fragment.newInstance())
                true
            }
            // 屈折マッピング
            R.id.w46 -> {
                changeFragment(W46Fragment.newInstance())
                true
            }
            // キューブ環境バンプマッピング
            R.id.w45 -> {
                changeFragment(W45Fragment.newInstance())
                true
            }
            // キューブ環境マッピング
            R.id.w44 -> {
                changeFragment(W44Fragment.newInstance())
                true
            }
            // 視差マッピング
            R.id.w43 -> {
                changeFragment(W43Fragment.newInstance())
                true
            }
            // バンプマッピング
            R.id.w42 -> {
                changeFragment(W42Fragment.newInstance())
                true
            }
            // ブラーフィルタ
            R.id.w41 -> {
                changeFragment(W41Fragment.newInstance())
                true
            }
            // フレームバッファ
            R.id.w40 -> {
                changeFragment(W40Fragment.newInstance())
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
