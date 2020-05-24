package milu.kiriu2010.milugles32.w1x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w1x.w15.W15Fragment
import milu.kiriu2010.milugles32.w1x.w16.W16Fragment
import milu.kiriu2010.milugles32.w1x.w17.W17Fragment
import milu.kiriu2010.milugles32.w1x.w18.W18Fragment
import milu.kiriu2010.milugles32.w1x.w19.W19Fragment

class W1xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        // 初期表示のフラグメントを設定
        changeFragment(W15Fragment.newInstance())

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w1x, menu)
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
            // w19_カリング・深度テスト
            R.id.w19 -> {
                changeFragment(W19Fragment.newInstance())
                true
            }
            // w18_IBO
            R.id.w18 -> {
                changeFragment(W18Fragment.newInstance())
                true
            }
            // w17_移動・回転・拡大縮小
            R.id.w17 -> {
                changeFragment(W17Fragment.newInstance())
                true
            }
            // w16_複数モデル
            R.id.w16 -> {
                changeFragment(W16Fragment.newInstance())
                true
            }
            // w15_頂点色
            R.id.w15 -> {
                changeFragment(W15Fragment.newInstance())
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
