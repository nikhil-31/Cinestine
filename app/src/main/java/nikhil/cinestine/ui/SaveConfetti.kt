package nikhil.cinestine.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import nikhil.cinestine.R
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

object SaveConfetti {
    fun burstFrom(anchor: View) {
        if (!anchor.isShown) return
        val root = anchor.context.findActivity()?.window?.decorView as? ViewGroup
            ?: anchor.rootView as? ViewGroup
            ?: return
        val loc = IntArray(2)
        val rootLoc = IntArray(2)
        anchor.getLocationInWindow(loc)
        root.getLocationInWindow(rootLoc)
        val originX = loc[0] - rootLoc[0] + anchor.width / 2f
        val originY = loc[1] - rootLoc[1] + anchor.height / 2f
        val overlay = Overlay(anchor.context, originX, originY)
        overlay.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.addView(overlay)
        overlay.play { root.removeView(overlay) }
    }

    private fun Context.findActivity(): Activity? {
        var current: Context? = this
        while (current is ContextWrapper) {
            if (current is Activity) return current
            current = current.baseContext
        }
        return null
    }

    private class Overlay(
        context: Context,
        private val originX: Float,
        private val originY: Float
    ) : View(context) {
        private val sparkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }
        private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val sparks: List<Spark>
        private var progress = 0f

        init {
            importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
            isClickable = false
            isFocusable = false
            val density = resources.displayMetrics.density
            val colors = intArrayOf(
                ContextCompat.getColor(context, R.color.gold),
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.colorSecondary),
                0xFFFF8A4C.toInt()
            )
            sparks = buildList {
                repeat(2) { wave ->
                    val delay = wave * 0.16f
                    repeat(28) { index ->
                        val angle = (index / 28f) * (Math.PI * 2).toFloat() +
                            Random.nextFloat() * 0.2f
                        add(
                            Spark(
                                angle = angle,
                                speed = (90f + Random.nextFloat() * 140f) * density,
                                color = colors[(index + wave) % colors.size],
                                delay = delay,
                                stroke = (2.2f + Random.nextFloat() * 1.6f) * density
                            )
                        )
                    }
                }
            }
        }

        fun play(onEnd: () -> Unit) {
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 900
                interpolator = LinearInterpolator()
                addUpdateListener { animator ->
                    progress = animator.animatedValue as Float
                    invalidate()
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        onEnd()
                    }
                })
                start()
            }
        }

        override fun onDraw(canvas: Canvas) {
            drawFlash(canvas)
            sparks.forEach { spark ->
                val local = ((progress - spark.delay) / (1f - spark.delay)).coerceIn(0f, 1f)
                if (local <= 0f) return@forEach
                val travel = 1f - exp(-3.4f * local)
                val fade = (1f - local).coerceIn(0f, 1f)
                val x = originX + cos(spark.angle) * spark.speed * travel
                val y = originY + sin(spark.angle) * spark.speed * travel + 70f * local * local
                val tail = 0.18f
                val back = (travel - tail).coerceAtLeast(0f)
                val tx = originX + cos(spark.angle) * spark.speed * back
                val ty = originY + sin(spark.angle) * spark.speed * back + 70f * (local - 0.12f).coerceAtLeast(0f).let { it * it }
                sparkPaint.color = spark.color
                sparkPaint.strokeWidth = spark.stroke
                sparkPaint.alpha = (255 * fade).toInt().coerceIn(0, 255)
                canvas.drawLine(tx, ty, x, y, sparkPaint)
                glowPaint.color = spark.color
                glowPaint.alpha = (200 * fade).toInt().coerceIn(0, 255)
                canvas.drawCircle(x, y, spark.stroke * 0.9f, glowPaint)
            }
        }

        private fun drawFlash(canvas: Canvas) {
            val flash = (1f - progress / 0.28f).coerceIn(0f, 1f)
            if (flash <= 0f) return
            val density = resources.displayMetrics.density
            glowPaint.color = ContextCompat.getColor(context, R.color.gold)
            glowPaint.alpha = (180 * flash).toInt()
            canvas.drawCircle(originX, originY, (10f + 26f * (1f - flash)) * density, glowPaint)
            glowPaint.color = ContextCompat.getColor(context, R.color.white)
            glowPaint.alpha = (220 * flash).toInt()
            canvas.drawCircle(originX, originY, 6f * density * flash, glowPaint)
        }

        override fun onTouchEvent(event: MotionEvent) = false
    }

    private class Spark(
        val angle: Float,
        val speed: Float,
        val color: Int,
        val delay: Float,
        val stroke: Float
    )
}
