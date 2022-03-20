package com.github.mnemotechnician.lettui.element.impl

import kotlin.math.*
import com.github.mnemotechnician.lettui.canvas.*
import com.github.mnemotechnician.lettui.element.*
import com.github.mnemotechnician.lettui.util.*

/**
 * Displays arbitrary text.
 *
 * @param text The text to display
 * @param wrap Whether to enable text wrap. When true, if the length of the text exceeds [maxWidth], it'gets wraped
 * @param maxWidth Maximum width of the label
 */
open class Label(
	text: String = "",
	wrap: Boolean = false,
	maxWidth: Int = Int.MAX_VALUE
) : Element() {
	var text by listened(text) { invalidate() }
	var wrap by listened(wrap) { invalidate() }
	var maxWidth by listened(maxWidth) { invalidate() }

	override open fun layout() {
		super.layout()

		width = border.minWidth
		height = 1 + border.minHeight

		var currentW = 0
		text.forEach {
			if (it == '\n') {
				currentW = 0
				height++
				return@forEach
			} else if (wrap && currentW >= maxWidth) {
				currentW = 0
				height++
			}
			
			if (currentW < maxWidth) width = max(width, ++currentW)
		}

	}

	override open fun draw(canvas: TextCanvas) {
		//todo: duplicate code?
		super.draw(canvas)
		
		border.drawBordered(canvas, x, y, width, height) { baseX, baseY, width, height ->
			var x = baseX
			var y = baseY

			text.forEach {
				if (it == '\n') {
					x = this.x
					y++
					return@forEach
				} else if (wrap && x >= width) {
					x = this.x
					y++
				}

				if (x < maxWidth) canvas.render(it, x++, y)
			}
		}
	}
}
