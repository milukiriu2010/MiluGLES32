package milu.kiriu2010.milugles32.w1x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
        changeFragment("w15")

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
                changeFragment("w19")
                true
            }
            // w18_IBO
            R.id.w18 -> {
                changeFragment("w18")
                true
            }
            // w17_移動・回転・拡大縮小
            R.id.w17 -> {
                changeFragment("w17")
                true
            }
            // w16_複数モデル
            R.id.w16 -> {
                changeFragment("w16")
                true
            }
            // w15_頂点色
            R.id.w15 -> {
                changeFragment("w15")
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // 表示するフラグメントを切り替える
    private fun changeFragment(tag: String) {
        val fragment = when (tag) {
            // w19_カリング・深度テスト
            "w19" -> W19Fragment.newInstance()
            // w18_IBO
            "w18" -> W18Fragment.newInstance()
            // w17_移動・回転・拡大縮小
            "w17" -> W17Fragment.newInstance()
            // w16_複数モデル
            "w16" -> W16Fragment.newInstance()
            // w15_頂点色
            "w15" -> W15Fragment.newInstance()
            // w15_頂点色
            else -> W15Fragment.newInstance()
        }

        // 現在表示しているフラグメントをスタックから外す
        supportFragmentManager.popBackStack()
        // 選択したフラグメントを表示する
        if ( supportFragmentManager.findFragmentByTag(tag) == null ) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment, tag)
                .commit()
        }
    }
}
