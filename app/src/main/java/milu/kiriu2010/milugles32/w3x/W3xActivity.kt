package milu.kiriu2010.milugles32.w3x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w3x.w30.W30Fragment
import milu.kiriu2010.milugles32.w3x.w32.W32Fragment
import milu.kiriu2010.milugles32.w3x.w33.W33Fragment
import milu.kiriu2010.milugles32.w3x.w34.W34Fragment
import milu.kiriu2010.milugles32.w3x.w35.W35Fragment
import milu.kiriu2010.milugles32.w3x.w36.W36Fragment
import milu.kiriu2010.milugles32.w3x.w37.W37Fragment
import milu.kiriu2010.milugles32.w3x.w38.W38Fragment
import milu.kiriu2010.milugles32.w3x.w39.W39Fragment
import milu.kiriu2010.milugles32.w8x.w81.W81Fragment

class W3xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        // 初期表示のフラグメントを設定
        changeFragment(W39Fragment.newInstance())

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w3x, menu)
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
            // アウトライン
            R.id.w39 -> {
                changeFragment(W39Fragment.newInstance())
                true
            }
            // ステンシルバッファ
            R.id.w38 -> {
                changeFragment(W38Fragment.newInstance())
                true
            }
            // ポイントスプライト
            R.id.w37 -> {
                changeFragment(W37Fragment.newInstance())
                true
            }
            // 点・線
            R.id.w36 -> {
                changeFragment(W36Fragment.newInstance())
                true
            }
            // ビルボード
            R.id.w35 -> {
                changeFragment(W35Fragment.newInstance())
                true
            }
            // 球面線形補間
            R.id.w34 -> {
                changeFragment(W34Fragment.newInstance())
                true
            }
            // マウスによる回転
            R.id.w33 -> {
                changeFragment(W33Fragment.newInstance())
                true
            }
            // クォータニオン
            R.id.w32 -> {
                changeFragment(W32Fragment.newInstance())
                true
            }
            // ブレンドファクター
            R.id.w30 -> {
                changeFragment(W30Fragment.newInstance())
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
