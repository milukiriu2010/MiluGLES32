package milu.kiriu2010.milugles32.w7x

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import milu.kiriu2010.milugles32.R
import milu.kiriu2010.milugles32.w7x.w70.W70Fragment
import milu.kiriu2010.milugles32.w7x.w71.W71Fragment
import milu.kiriu2010.milugles32.w7x.w72.W72Fragment
import milu.kiriu2010.milugles32.w7x.w74.W74Fragment
import milu.kiriu2010.milugles32.w7x.w75.W75Fragment
import milu.kiriu2010.milugles32.w7x.w76.W76Fragment
import milu.kiriu2010.milugles32.w7x.w77.W77Fragment
import milu.kiriu2010.milugles32.w7x.w78.W78Fragment

class W7xActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_es32x01)

        // 初期表示のフラグメントを設定
        changeFragment(W77Fragment.newInstance())

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            //setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_w7x, menu)
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
            // ビデオ
            R.id.w78 -> {
                changeFragment(W78Fragment.newInstance())
                true
            }
            // ラインシェード
            R.id.w77 -> {
                changeFragment(W77Fragment.newInstance())
                true
            }
            // ハーフトーン
            R.id.w76 -> {
                changeFragment(W76Fragment.newInstance())
                true
            }
            // インスタンシング
            R.id.w75 -> {
                changeFragment(W75Fragment.newInstance())
                true
            }
            // 異方性フィルタリング
            R.id.w74 -> {
                changeFragment(W74Fragment.newInstance())
                true
            }
            // 浮動小数点数VTF
            R.id.w72 -> {
                changeFragment(W72Fragment.newInstance())
                true
            }
            // 頂点テクスチャフェッチ
            R.id.w71 -> {
                changeFragment(W71Fragment.newInstance())
                true
            }
            // 浮動小数点数テクスチャ
            R.id.w70 -> {
                changeFragment(W70Fragment.newInstance())
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
