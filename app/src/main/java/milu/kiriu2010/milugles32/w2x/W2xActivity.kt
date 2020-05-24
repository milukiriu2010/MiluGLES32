package milu.kiriu2010.milugles32.w2x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w2x.w20.W20Fragment
import milu.kiriu2010.milugles32.w2x.w21.W21Fragment
import milu.kiriu2010.milugles32.w2x.w22.W22Fragment
import milu.kiriu2010.milugles32.w2x.w23.W23Fragment
import milu.kiriu2010.milugles32.w2x.w24.W24Fragment
import milu.kiriu2010.milugles32.w2x.w25.W25Fragment
import milu.kiriu2010.milugles32.w2x.w26.W26Fragment
import milu.kiriu2010.milugles32.w2x.w27.W27Fragment
import milu.kiriu2010.milugles32.w2x.w28.W28Fragment
import milu.kiriu2010.milugles32.w2x.w29.W29Fragment

class W2xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        // 初期表示のフラグメントを設定
        changeFragment(W29Fragment.newInstance())

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w2x, menu)
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
            // アルファブレンド
            R.id.w29 -> {
                changeFragment(W29Fragment.newInstance())
                true
            }
            // テクスチャパラメータ
            R.id.w28 -> {
                changeFragment(W28Fragment.newInstance())
                true
            }
            // マルチテクスチャ
            R.id.w27 -> {
                changeFragment(W27Fragment.newInstance())
                true
            }
            // テクスチャ
            R.id.w26 -> {
                changeFragment(W26Fragment.newInstance())
                true
            }
            // 点光源
            R.id.w25 -> {
                changeFragment(W25Fragment.newInstance())
                true
            }
            // フォンシェーディング
            R.id.w24 -> {
                changeFragment(W24Fragment.newInstance())
                true
            }
            // 反射光
            R.id.w23 -> {
                changeFragment(W23Fragment.newInstance())
                true
            }
            // 環境光
            R.id.w22 -> {
                changeFragment(W22Fragment.newInstance())
                true
            }
            // 平行光源
            R.id.w21 -> {
                changeFragment(W21Fragment.newInstance())
                true
            }
            // トーラス
            R.id.w20 -> {
                changeFragment(W20Fragment.newInstance())
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
