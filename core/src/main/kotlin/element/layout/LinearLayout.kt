package com.github.mnemotechnician.lettui.element.layout

import kotlin.math.*
import com.github.mnemotechnician.lettui.element.*
import com.github.mnemotechnician.lettui.canvas.*

/**
 * A group that arranges the child elements in a line
 *
 * @param vertical if true, arranges elements from top to bottom. Otherwise elements are arranged from left to right.
 */
open class LinearLayout(val vertical: Boolean = false) : Group() {
	override open fun layout() {
		super.layout()
		
		var maxW = 0
		var maxH = 0

		elements.forEach {
			if (vertical) {
				it.setPosition(x + border.offX, y + maxH + border.offY)
			} else {
				it.setPosition(x + maxW + border.offX, y + border.offY)
			}
			it.layout()
			
			if (vertical) {
				maxW = max(maxW, it.width + border.minWidth)
				maxH += it.height
			} else {
				maxW += it.width
				maxH = max(maxH, it.height + border.minHeight)
			}
		}

		width = maxW + border.minWidth
		height = maxH + border.minHeight
	}
}
