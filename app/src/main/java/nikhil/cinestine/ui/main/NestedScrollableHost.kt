package nikhil.cinestine.ui.main

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.absoluteValue
import kotlin.math.sign

class NestedScrollableHost @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var touchSlop = 0
    private var initialX = 0f
    private var initialY = 0f

    private val parentViewPager: ViewPager2?
        get() {
            var current = parent as? View
            while (current != null && current !is ViewPager2) {
                current = current.parent as? View
            }
            return current as? ViewPager2
        }

    private val child: View? get() = if (childCount > 0) getChildAt(0) else null

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        handleInterceptTouchEvent(event)
        return super.onInterceptTouchEvent(event)
    }

    private fun canChildScroll(orientation: Int, delta: Float): Boolean {
        val direction = -delta.sign.toInt()
        return when (orientation) {
            ViewPager2.ORIENTATION_HORIZONTAL -> child?.canScrollHorizontally(direction) ?: false
            ViewPager2.ORIENTATION_VERTICAL -> child?.canScrollVertically(direction) ?: false
            else -> false
        }
    }

    private fun handleInterceptTouchEvent(event: MotionEvent) {
        val orientation = parentViewPager?.orientation ?: return
        if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
            return
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = event.x
                initialY = event.y
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - initialX
                val dy = event.y - initialY
                val horizontalPager = orientation == ViewPager2.ORIENTATION_HORIZONTAL
                val scaledDx = dx.absoluteValue * if (horizontalPager) 0.5f else 1f
                val scaledDy = dy.absoluteValue * if (horizontalPager) 1f else 0.5f
                if (scaledDx > touchSlop || scaledDy > touchSlop) {
                    if (horizontalPager == (scaledDy > scaledDx)) {
                        parent.requestDisallowInterceptTouchEvent(false)
                    } else if (canChildScroll(orientation, if (horizontalPager) dx else dy)) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }
        }
    }
}
