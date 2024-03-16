package com.epilogs.game_trail_tracker.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.epilogs.game_trail_tracker.database.entities.Animal

class TrailView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val trailPath = Path()
    private var animals: List<Animal> = emptyList()

    fun setAnimals(animals: List<Animal>) {
        this.animals = animals
        // You might want to sort or process animals here
        invalidate() // This tells the view to redraw with the new data
    }

    // Paint objects for drawing
    private val trailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY // Trail color
        strokeWidth = 5f // Trail thickness
        style = Paint.Style.STROKE
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED // Dot color
        style = Paint.Style.FILL
    }

    // Example data - replace with your database data
    private val dots = mutableListOf<Dot>()

    init {
        // Example initialization - you should load this from your database
        for (i in 1..5) {
            dots.add(Dot("Animal $i", "Date $i", PointF(i * 200f, 100f)))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Define the trail path
        trailPath.reset() // Clear any previous path
        trailPath.moveTo(width * 0.5f, 0f) // Start in the middle of the top
        // Create a curvy path to the bottom
        // Example: Quadratic Bezier curve
        trailPath.quadTo(width * 0.3f, height * 0.3f, width * 0.5f, height * 0.5f)
        trailPath.quadTo(width * 0.7f, height * 0.7f, width * 0.5f, height.toFloat())

        // Draw the trail path
        canvas.drawPath(trailPath, trailPaint)

        // Draw dots along the path
        val pathMeasure = PathMeasure(trailPath, false)
        val pathLength = pathMeasure.length
        val distanceBetweenDots = pathLength / (dots.size + 1) // Equal spacing

        animals.forEach { animal ->
            // Calculate the dot position. This is just an example:
            val position = calculateDotPosition(animal)
            canvas.drawCircle(position.x, position.y, 20f, dotPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val touchedX = event.x
            val touchedY = event.y

            // Check if any dot is touched
            dots.firstOrNull { dot ->
                val dx = dot.position.x - touchedX
                val dy = dot.position.y - touchedY
                Math.sqrt((dx * dx + dy * dy).toDouble()) < 20 // Assuming dot radius is 20
            }?.let { dot ->
                // Show popup for the dot
                showPopup(dot)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun showPopup(dot: Dot) {
        // Implement showing the popup here
        // This could involve a PopupWindow or a custom dialog
        // Display dot.name, dot.date, and load the image into the popup
    }

    private fun calculateDotPosition(animal: Animal): PointF {
        // Implement your logic to calculate each dot's position based on animal data
        return PointF() // Return the calculated position
    }

    data class Dot(val name: String, val date: String, val position: PointF)
}
