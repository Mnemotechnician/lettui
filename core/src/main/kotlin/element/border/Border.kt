package com.github.mnemotechnician.lettui.element.border

import com.github.mnemotechnician.lettui.canvas.*

/** Represents a set of border drawing characters and allows elements to draw the said border */
@Suppress("NOTHING_TO_INLINE")
open class Border(
	val topLeft: Char = EMPTY_CHAR,
	val top: Char = EMPTY_CHAR,
	val topRight: Char = EMPTY_CHAR,
	val left: Char = EMPTY_CHAR,
	val right: Char = EMPTY_CHAR,
	val bottomLeft: Char = EMPTY_CHAR,
	val bottom: Char = EMPTY_CHAR,
	val bottomRight: Char = EMPTY_CHAR
) {
	val hasTop = top != EMPTY_CHAR || topLeft != EMPTY_CHAR || topRight != EMPTY_CHAR
	val hasLeft = left != EMPTY_CHAR || topLeft != EMPTY_CHAR || bottomLeft != EMPTY_CHAR
	val hasBottom = bottom != EMPTY_CHAR || bottomLeft != EMPTY_CHAR || bottomRight != EMPTY_CHAR
	val hasRight = right != EMPTY_CHAR || topRight != EMPTY_CHAR || bottomRight != EMPTY_CHAR

	val offX = hasLeft.toInt()
	val offY = hasTop.toInt()

	val minWidth = hasLeft + hasRight
	val minHeight = hasTop + hasBottom
	
	/** Draws a border with the specified dimensions */
	open fun drawBorder(
		canvas: TextCanvas,
		baseX: Int, baseY: Int, width: Int, height: Int
	) {
		if (width > 0 && height > 0) {
			//top
			if (topLeft != EMPTY_CHAR) {
				canvas.render(topLeft, baseX, baseY)
			}

			if (topRight != EMPTY_CHAR) {
				canvas.render(topRight, baseX + width - 1, baseY)
			}

			if (top != EMPTY_CHAR) {
				val sub = (topLeft != EMPTY_CHAR) + (topRight != EMPTY_CHAR)
				repeat(width - sub) { canvas.render(top, baseX + it + offX, baseY) }
			}

			//bottom
			if (bottomLeft != EMPTY_CHAR) {
				canvas.render(bottomLeft, baseX, baseY + height - 1)
			}

			if (bottomRight != EMPTY_CHAR) {
				canvas.render(bottomRight, baseX + width - 1, baseY + height - 1)
			}

			if (bottom != EMPTY_CHAR) {
				val sub = (bottomLeft != EMPTY_CHAR) + (bottomRight != EMPTY_CHAR)
				repeat(width - sub) { canvas.render(bottom, baseX + offX + it, baseY + height - 1) }
			}
			
			//left
			if (left != EMPTY_CHAR) {
				val sub = (topLeft != EMPTY_CHAR) + (bottomLeft != EMPTY_CHAR)
				repeat(height - sub) { canvas.render(left, baseX, baseY + offY + it) }
			}
			
			//right
			if (right != EMPTY_CHAR) {
				val sub = (topRight != EMPTY_CHAR) + (bottomRight != EMPTY_CHAR)
				repeat(height - sub) { canvas.render(right, baseX + width - 1, baseY + offY + it) }
			}
		}
	}
	
	/** Draws a border and calls the drawer function with the root position and available space */
	inline fun drawBordered(
		canvas: TextCanvas,
		baseX: Int, baseY: Int, width: Int, height: Int,
		drawer: (x: Int, y: Int, width: Int, height: Int) -> Unit
	) {
		drawBorder(canvas, baseX, baseY, width, height)

		val x = baseX + offX
		val y = baseY + offY
		val w = width - (hasLeft + hasRight)
		val h = height - (hasTop + hasBottom)

		drawer(x, y, w, h)
	}

	inline fun Boolean.toInt() = if (this) 1 else 0
	
	inline operator fun Boolean.plus(other: Boolean) = toInt() + other.toInt()

	companion object {
		fun withSet(set: BoxDrawing): Border = Border(
			topLeft = set.cornerRightBotton,
			topRight = set.cornerBottomLeft,
			bottomLeft = set.cornerTopRight,
			bottomRight = set.cornerLeftTop,
			top = set.hline,
			bottom = set.hline,
			left = set.vline,
			right = set.vline
		)

		val None = Border()

		val Light = withSet(BoxDrawing.Light)

		val Rounded = withSet(BoxDrawing.Rounded)

		val Double = withSet(BoxDrawing.Double)

		val Bold = withSet(BoxDrawing.Bold)
	}
}

