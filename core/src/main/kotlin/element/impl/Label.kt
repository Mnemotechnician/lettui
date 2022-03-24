package com.github.mnemotechnician.lettui.element.impl

import kotlin.math.*
import com.github.mnemotechnician.lettui.canvas.*
import com.github.mnemotechnician.lettui.element.*
import com.github.mnemotechnician.lettui.util.*

/**
 * Displays arbitrary text.
 *
 * @param text The text to display
 * @param wrap Whether to enable text wrap. If true and the length of the text exceeds the label's preferred width, it'gets wraped.
 */
open class Label(
	text: String = "",
	wrap: Boolean = false
) : Element() {
	var text by listened(text) { invalidate() }
	var wrap by listened(wrap) { invalidate() }

	val maxWidth get() = if (rawPrefWidth > 0) rawPrefWidth else Int.MAX_VALUE

	override open fun layout() {
		super.layout()

		minWidth = border.minWidth
		minHeight = border.minHeight + 1

		var currentW = minWidth
		text.forEach {
			if (it == '\n') {
				currentW = border.minWidth
				minHeight++
				return@forEach
			} else if (wrap && currentW >= maxWidth) {
				currentW = border.minWidth
				minHeight++
			}

			if (currentW < maxWidth && (it != ' ' || currentW != 0)) minWidth = max(minWidth, ++currentW)
		}
	}

	override open fun draw(canvas: TextCanvas) {
		super.draw(canvas)
		
		border.drawBordered(canvas, x, y, width, height) { baseX, baseY, width, height ->
			var x = baseX
			var y = baseY

			text.forEach {
				if (it == '\n') {
					x = baseX
					y++
					return@forEach
				} else if (wrap && x >= baseX + width) {
					x = baseX
					y++
				}
				
				if (x < baseX + width && y < baseY + height && (it != ' ' || x != baseX)) canvas.render(it, x++, y)
			}
		}
	}
}
