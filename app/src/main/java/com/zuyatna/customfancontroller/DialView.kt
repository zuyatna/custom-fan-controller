package com.zuyatna.customfancontroller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class DialView : View, View.OnClickListener {

    companion object {
        const val SELECTION_COUNT = 4 // Total number of selections
    }

    private var width: Float = 0F // Custom view width
    private var height: Float = 0F // Custom view height
    private lateinit var textPaint: Paint // For text in the view
    private lateinit var dialPaint: Paint // For dial circle in the view
    private var radius: Float = 0F // Radius of the circle
    private var activeSelection: Int = 0 // The active selection

    // String buffer for dial labels and float for ComputeXY result.
    private var tempLabel: StringBuffer = StringBuffer(8)
    private var tempResult: Array<Float> = arrayOf(0F, 1F)

    constructor(context: Context?) : super(context) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.color = Color.BLACK
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 40f
        dialPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        dialPaint.color = Color.GRAY
        activeSelection = 0

        setOnClickListener(this)
    }



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        width = w.toFloat() // 550.0
        height = h.toFloat() // 550.0
        radius = ((min(width, height) / 2 * 0.8).toFloat()) /// (550.0) / 2 * 0.8 = 220.0
    }

    private fun computeXYForPosition(pos: Int, radius: Float): Array<Float> {
        val result: Array<Float> = tempResult
        val startAngle: Double = PI * (9 / 8.0) // 3.53
        val angle: Double = startAngle + (pos * (PI / 4)) /// 3.53 + (0 * (PI / 4) = 3.53
        result[0] = ((radius * cos(angle)) + (width / 2)).toFloat() /// (220.0 * -0.92) + (550.0 / 2) = -53.2
        result[1] = ((radius * sin(angle)) + (height / 2)).toFloat() /// (220.0 * -0.28) + (550.0 / 2) = 183.1

        return result
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw the dial
        canvas?.drawCircle(width / 2, height / 2, radius, dialPaint)
        // Draw the text labels
        val labelRadius: Float = radius + 20 /// (220.0 + 20) = 240.0
        val label: StringBuffer = tempLabel // 3
        for (i in 0 until SELECTION_COUNT) {
            val xyData: Array<Float> = computeXYForPosition(i, labelRadius)
            val x = xyData[0]
            val y = xyData[1]
            label.setLength(0)
            label.append(i)
            canvas?.drawText(label, 0, label.length, x, y, textPaint)
        }

        // Draw the indicator mark
        val markerRadius: Float = radius - 35 /// 220.0 - 35 = 185.0
        val xyData: Array<Float> = computeXYForPosition(activeSelection, markerRadius)

        val x = xyData[0]
        val y = xyData[1]
        canvas?.drawCircle(x, y, 20F, textPaint)
    }

    override fun onClick(p0: View?) {
        // Rotate selection to the next valid choice
        activeSelection = (activeSelection + 1) % SELECTION_COUNT

        // Set dial background color to green if selection is >= 1
        if (activeSelection >= 1) {
            dialPaint.color = Color.GREEN
        } else {
            dialPaint.color = Color.GRAY
        }

        // Redraw the view
        invalidate()
    }
}