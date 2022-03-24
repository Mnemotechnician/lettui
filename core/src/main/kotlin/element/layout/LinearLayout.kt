package com.github.mnemotechnician.lettui.element.layout

import kotlin.math.*
import com.github.mnemotechnician.lettui.element.*
import com.github.mnemotechnician.lettui.element.border.*
import com.github.mnemotechnician.lettui.canvas.*

/**
 * A group that arranges the child elements in a straight line
 * This group honours it's own preferred size and will try to limit the size of the children if they don't fit,
 * even if their preferred width / height is greater than that.
 *
 * Elements with negative size may cause undefined behavior.
 *
 * @param vertical if true, arranges elements from top to bottom. Otherwise elements are arranged from left to right.
 * @param fill if true, the layout will try to distribute any excess space between it's children
 */
open class LinearLayout(
	var vertical: Boolean = false,
	var fill: Boolean = false
) : Group() {
	/** Whether to display debug info */
	var debug = false

	override open fun draw(canvas: TextCanvas) {
		super.draw(canvas)

		if (debug) {
			border = Border.Light
			canvas.render("w:$width,h:$height", x + 1, y)
		}
	}

	override open fun layout() {
		//so, our goal there is to fit into (width, height)
		var maxW = 0
		var maxH = 0

		val ownWidth = width - border.minWidth
		val ownHeight = height - border.minHeight

		//first, we calculate the total preferred size of our children
		var childMaxW = 0
		var childMaxH = 0

		elements.forEach {
			it.layout()
			if (vertical) {
				childMaxW = max(it.prefWidth, childMaxW)
				childMaxH += it.prefHeight
			} else {
				childMaxW += it.prefWidth
				childMaxH = max(it.prefHeight, childMaxH)
			}
		}

		//now we have three variants: our size matches the children size, our size is bigger or our size is smaller
		var occupiedW: Double
		var occupiedH: Double
		
		//ifs here are to prevent some weird values
		occupiedW = if (childMaxW > 0) ownWidth.toDouble() / childMaxW else 0.0
		occupiedH = if (childMaxH > 0) ownHeight.toDouble() / childMaxH else 0.0
		
		if (!fill) {
			occupiedW = min(1.0, occupiedW)
			occupiedH = min(1.0, occupiedH)
		}
		
		//... and now the fun begins, we have to distribute the space.
		elements.forEach {
			var w: Int
			var h: Int

			if (vertical) {
				w = if (fill) ownWidth else min(ownWidth, it.prefWidth)
				h = (it.prefHeight * occupiedH).toInt()

				it.setPosition(x + border.offX, y + maxH + border.offY)
				it.setSize(w, h)

				maxW = max(maxW, w)
				maxH += h
			} else {
				w = (it.prefWidth * occupiedW).toInt()
				h = if (fill) ownHeight else min(ownHeight, it.prefHeight)

				it.setPosition(x + maxW + border.offX, y + border.offY)
				it.setSize(w, h)

				maxW += w
				maxH = max(maxH, h)
			}

			//it.layout()
		}
	
		//if the width and height were explicitly set by the user, use them. Otherwise, use the size of the children
		minWidth = if (rawPrefWidth > 0) rawPrefWidth else max(childMaxW, 0) + border.minWidth
		minHeight = if (rawPrefHeight > 0) rawPrefHeight else max(childMaxH, 0) + border.minHeight
		
		super.layout()
	}
}
