package com.lukakordzaia.streamflow.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.abs

class SquircleImageView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val squirclePath: Path = getSquirclePaath(20, 20, 400)
        canvas.drawPath(squirclePath, Paint())
    }

    private fun getSquirclePaath(left: Int, top: Int, radius: Int): Path {
        //Formula: (|x|)^3 + (|y|)^3 = radius^3
        val radiusToPow = (radius * radius * radius).toDouble()
        val path = Path()
        path.moveTo((-radius).toFloat(), 0F)
        for (x in -radius..radius) path.lineTo(x.toFloat(), Math.cbrt(radiusToPow - abs(x * x * x)).toFloat())
        for (x in radius downTo -radius) path.lineTo(x.toFloat(), (-Math.cbrt(radiusToPow - abs(x * x * x))).toFloat())
        path.close()
        val matrix = Matrix()
        matrix.postTranslate((left + radius).toFloat(), (top + radius).toFloat())
        path.transform(matrix)
        return path
    }
}