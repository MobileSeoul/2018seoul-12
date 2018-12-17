package k_spot.jnm.k_spot.viewpager

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class MainViewPager(ctx : Context, attributeSet: AttributeSet) : ViewPager(ctx, attributeSet){
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}