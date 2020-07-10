package com.example.segmentedprogressbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import java.util.*

class SegmentedProgressBar : View {
    private val progressPaint = Paint()
    private val dividerPaint = Paint()
    private var gradientColors = IntArray(3)
    private var lastDividerPosition = 0f
    private var percentCompleted = 0f
    private var progressBarWidth = 0
    private var maxTimeInMillis: Long = 0
    private var dividerCount = 0
    private var dividerWidth = 1f
    private var isDividerEnabled = false
    private var dividerPositions: MutableList<Float>? = null
    private var countDownTimerWithPause: CountDownTimerWithPause? = null
    private var cornerRadius = 0f
    var listener: ProgressBarListener? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(
            RectF(0f, 0f, percentCompleted, height.toFloat()),
            cornerRadius,
            cornerRadius,
            progressPaint
        )
        if (dividerCount > 0 && isDividerEnabled) {
            for (i in 0 until dividerCount) {
                val leftPosition = dividerPositions!![i]
                canvas.drawRect(
                    leftPosition,
                    0f,
                    leftPosition + dividerWidth,
                    height.toFloat(),
                    dividerPaint
                )
            }
        }
    }

    private fun init() {
        dividerPositions = ArrayList()
        cornerRadius = 0f
        val viewTreeObserver = viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this)
                    progressBarWidth = width
                    Log.d(
                        TAG,
                        "setShader: progressBarWidth : $progressBarWidth"
                    )
                    if (gradientColors.size > 0) {
                        val shader: Shader = LinearGradient(
                            0f,
                            0f,
                            progressBarWidth.toFloat(),
                            height.toFloat(),
                            gradientColors,
                            null,
                            Shader.TileMode.MIRROR
                        )
                        progressPaint.shader = shader
                    }
                }
            })
        }
    }

    fun SetListener(listeter: ProgressBarListener?) {
        listener = listeter
    }

    /**
     * Updates the progress bar based on time passed
     *
     * @param millisPassed
     */
    private fun updateProgress(millisPassed: Long) {
        listener!!.TimeinMill(millisPassed)
        percentCompleted = progressBarWidth * millisPassed.toFloat() / maxTimeInMillis
        invalidate()
    }

    /**
     * Updates the progress bar based on manually passed percent value.
     *
     * @param percentValue
     */
    private fun updateProgress(percentValue: Float) {
        percentCompleted = progressBarWidth * percentValue
        invalidate()
    }

    fun GetPercentComplete(): Float {
        return percentCompleted
    }

    fun pause() {
        if (countDownTimerWithPause == null) {
            Log.e(
                TAG,
                "pause: Auto progress is not initialized. Use \"enableAutoProgressView\" to initialize the progress bar."
            )
            return
        }
        countDownTimerWithPause!!.pause()
    }

    /**
     * Resume the progress bar
     */
    fun resume() {
        if (countDownTimerWithPause == null) {
            Log.e(
                TAG,
                "resume: Auto progress is not initialized. Use \"enableAutoProgressView\" to initialize the progress bar."
            )
            return
        }
        countDownTimerWithPause!!.resume()
    }

    fun reset() {
        countDownTimerWithPause!!.cancel()
        enableAutoProgressView(maxTimeInMillis)
        dividerPositions!!.removeAll(dividerPositions!!)
        percentCompleted = 0f
        lastDividerPosition = 0f
        dividerCount = 0
        invalidate()
    }

    fun cancel() {
        if (countDownTimerWithPause == null) {
            Log.e(
                TAG,
                "cancel: Auto progress is not initialized. Use \"enableAutoProgressView\" to initialize the progress bar."
            )
            return
        }
        countDownTimerWithPause!!.cancel()
    }

    /**
     * Apply the shader for the for the progress bar.
     *
     * @param colors
     */
    fun setShader(colors: IntArray) {
        gradientColors = colors
        if (progressBarWidth > 0) {
            val shader: Shader = LinearGradient(
                0f,
                0f,
                progressBarWidth.toFloat(),
                height.toFloat(),
                colors,
                null,
                Shader.TileMode.MIRROR
            )
            progressPaint.shader = shader
        }
    }

    /**
     * Set the color for the progress bar
     *
     * @param color
     */
    fun setProgressColor(color: Int) {
        progressPaint.color = color
    }

    /**
     * Set the color for the divider bar
     *
     * @param color
     */
    fun setDividerColor(color: Int) {
        dividerPaint.color = color
    }

    /**
     * set the width of the divider
     *
     * @param width
     */
    fun setDividerWidth(width: Float) {
        if (width < 0) {
            Log.w(
                TAG,
                "setDividerWidth: Divider width can not be negative"
            )
            return
        }
        dividerWidth = width
    }

    /**
     * The progress bar will be auto progressing within the given time limit towards completion
     * where the it can be paused and resumed.
     *
     * @param timeInMillis
     */
    fun enableAutoProgressView(timeInMillis: Long) {
        if (timeInMillis < 0) {
            Log.w(
                TAG,
                "enableAutoProgressView: Time can not be in negative"
            )
            return
        }
        maxTimeInMillis = timeInMillis
        countDownTimerWithPause = object : CountDownTimerWithPause(
            maxTimeInMillis,
            FPS_IN_MILLI.toLong(),
            false
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val timePassed =
                    maxTimeInMillis - millisUntilFinished
                updateProgress(timePassed)
            }

            override fun onFinish() {
                updateProgress(maxTimeInMillis)
            }
        }.create()
    }

    fun setDividerEnabled(value: Boolean) {
        isDividerEnabled = value
    }

    /**
     * Manually update the progress bar completion status
     *
     * @param value can only be in between 0 and 1 inclusive.
     */
    fun publishProgress(value: Float) {
        if (value < 0 || value > 1) {
            Log.w(
                TAG,
                "publishProgress: Progress value can only be in between 0 and 1"
            )
            return
        }
        updateProgress(value)
    }

    /**
     * Add Divider to current position
     */
    fun addDivider() {
        if (lastDividerPosition != percentCompleted) {
            lastDividerPosition = percentCompleted
            dividerCount += 1
            dividerPositions!!.add(percentCompleted)
            invalidate()
        } else {
            Log.w(
                TAG,
                "addDivider: Divider already added to current position"
            )
        }
    }

    fun setCornerRadius(cornerRadius: Float) {
        this.cornerRadius = cornerRadius
    }

    companion object {
        private const val TAG = "SegmentedProgressBar"
        private const val FPS_IN_MILLI = 16 // 16.66 ~ 60fps
    }
}