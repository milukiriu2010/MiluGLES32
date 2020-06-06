package milu.kiriu2010.milugles32.w6x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w6x.w60.W60Fragment
import milu.kiriu2010.milugles32.w6x.w61.W61Fragment
import milu.kiriu2010.milugles32.w6x.w62.W62Fragment
import milu.kiriu2010.milugles32.w6x.w63.W63Fragment
import milu.kiriu2010.milugles32.w6x.w64.W64Fragment
import milu.kiriu2010.milugles32.w6x.w65.W65Fragment
import milu.kiriu2010.milugles32.w6x.w66.W66Fragment
import milu.kiriu2010.milugles32.w6x.w67.W67Fragment
import milu.kiriu2010.milugles32.w6x.w68.W68Fragment
import milu.kiriu2010.milugles32.w6x.w69.W69Fragment

class W6xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        // 初期表示のフラグメントを設定
        changeFragment(W68Fragment.newInstance())

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w6x, menu)
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
            // 深度値
            R.id.w69 -> {
                changeFragment(W69Fragment.newInstance())
                true
            }
            // ゴッドレイ
            R.id.w68 -> {
                changeFragment(W68Fragment.newInstance())
                true
            }
            // ズームブラー
            R.id.w67 -> {
                changeFragment(W67Fragment.newInstance())
                true
            }
            // mosaicフィルタ
            R.id.w66 -> {
                changeFragment(W66Fragment.newInstance())
                true
            }
            // 後光表面化散乱
            R.id.w65 -> {
                changeFragment(W65Fragment.newInstance())
                true
            }
            // リムライティング
            R.id.w64 -> {
                changeFragment(W64Fragment.newInstance())
                true
            }
            // 半球ライティング
            R.id.w63 -> {
                changeFragment(W63Fragment.newInstance())
                true
            }
            // ステンシル鏡面
            R.id.w62 -> {
                changeFragment(W62Fragment.newInstance())
                true
            }
            // パーティクルフォグ
            R.id.w61 -> {
                changeFragment(W61Fragment.newInstance())
                true
            }
            // 距離フォグ
            R.id.w60 -> {
                changeFragment(W60Fragment.newInstance())
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
