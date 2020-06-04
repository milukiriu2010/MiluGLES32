package milu.kiriu2010.milugles32.w5x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w5x.w50.W50Fragment
import milu.kiriu2010.milugles32.w5x.w51.W51Fragment
import milu.kiriu2010.milugles32.w5x.w53.W53Fragment
import milu.kiriu2010.milugles32.w5x.w54.W54Fragment
import milu.kiriu2010.milugles32.w5x.w55.W55Fragment
import milu.kiriu2010.milugles32.w5x.w56.W56Fragment
import milu.kiriu2010.milugles32.w5x.w57.W57Fragment
import milu.kiriu2010.milugles32.w5x.w58.W58Fragment
import milu.kiriu2010.milugles32.w5x.w59.W59Fragment

class W5xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        // 初期表示のフラグメントを設定
        changeFragment(W59Fragment.newInstance())

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w5x, menu)
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
            // 被写界深度
            R.id.w59 -> {
                changeFragment(W59Fragment.newInstance())
                true
            }
            // グレアフィルタ
            R.id.w58 -> {
                changeFragment(W58Fragment.newInstance())
                true
            }
            // gaussianフィルタ
            R.id.w57 -> {
                changeFragment(W57Fragment.newInstance())
                true
            }
            // laplacianフィルタ
            R.id.w56 -> {
                changeFragment(W56Fragment.newInstance())
                true
            }
            // sobelフィルタ
            R.id.w55 -> {
                changeFragment(W55Fragment.newInstance())
                true
            }
            // セピア調変換
            R.id.w54 -> {
                changeFragment(W54Fragment.newInstance())
                true
            }
            // グレイスケール
            R.id.w53 -> {
                changeFragment(W53Fragment.newInstance())
                true
            }
            // シャドウマッピング
            R.id.w51 -> {
                changeFragment(W51Fragment.newInstance())
                true
            }
            // 光学迷彩
            R.id.w50 -> {
                changeFragment(W50Fragment.newInstance())
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
