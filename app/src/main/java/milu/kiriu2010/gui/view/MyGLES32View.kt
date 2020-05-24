package milu.kiriu2010.gui.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

// ------------------------------------
// GLSL ES 3.2用ビュー
// ------------------------------------
// 2019.06.17
// 2020.05.24 以下のエラーを取り除くため、performClickを実装
// customview has setOnTouchListener called on it but does not override performClick
// ------------------------------------
class MyGLES32View: GLSurfaceView {
    constructor(ctx: Context): super(ctx) {

    }

    /* @JvmOverloads */
    constructor(ctx: Context, attrs: AttributeSet? = null) : super(ctx, attrs) {

    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    init {
        // OpenGL ES 3.2 contextを生成
        setEGLContextClientVersion(3)
    }
}
