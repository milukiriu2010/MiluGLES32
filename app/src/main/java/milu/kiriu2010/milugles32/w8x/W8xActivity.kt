package milu.kiriu2010.milugles32.w8x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w8x.w80.W80Fragment
import milu.kiriu2010.milugles32.w8x.w81.W81Fragment
import milu.kiriu2010.milugles32.w8x.w82.W82Fragment
import milu.kiriu2010.milugles32.w8x.w83.W83Fragment
import milu.kiriu2010.milugles32.w8x.w84.W84Fragment
import milu.kiriu2010.milugles32.w8x.w85.W85Fragment
import milu.kiriu2010.milugles32.w8x.w86.W86Fragment
import milu.kiriu2010.milugles32.w8x.w87.W87Fragment
import milu.kiriu2010.milugles32.w8x.w89.W89Fragment

class W8xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        // 初期表示のフラグメントを設定
        changeFragment(W80Fragment.newInstance())

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w8x, menu)
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
            // スフィア環境マッピング
            R.id.w89 -> {
                changeFragment(W89Fragment.newInstance())
                true
            }
            // フラットシェーディング
            R.id.w87 -> {
                changeFragment(W87Fragment.newInstance())
                true
            }
            // 色取得
            R.id.w86 -> {
                changeFragment(W86Fragment.newInstance())
                true
            }
            // MRTエッジ検出
            R.id.w85 -> {
                changeFragment(W85Fragment.newInstance())
                true
            }
            // MRT
            R.id.w84 -> {
                changeFragment(W84Fragment.newInstance())
                true
            }
            // GPGPU
            R.id.w83 -> {
                changeFragment(W83Fragment.newInstance())
                true
            }
            // VBO逐次更新:パーティクル
            R.id.w82 -> {
                changeFragment(W82Fragment.newInstance())
                true
            }
            // VBO逐次更新
            R.id.w81 -> {
                changeFragment(W81Fragment.newInstance())
                true
            }
            // クロマキー
            R.id.w80 -> {
                changeFragment(W80Fragment.newInstance())
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
