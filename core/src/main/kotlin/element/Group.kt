package com.github.mnemotechnician.lettui.element

import com.github.mnemotechnician.lettui.element.*
import com.github.mnemotechnician.lettui.canvas.*

/** Represents a group of elements */
abstract class Group : Element() {
	/** A list of child elements */
	val elements = ArrayList<Element>(10)
	
	/** Updates all children of this group */
	override open fun update() {
		super.update()
		elements.forEach { it.update() }
	}
	
	/** Draws all elements of this group */
	override open fun draw(canvas: TextCanvas) {
		super.draw(canvas)

		border.drawBorder(canvas, x, y, width, height)
		elements.forEach { it.draw(canvas) }
	}
	
	/** Adds a child element */
	open fun add(element: Element) {
		elements.add(element)
		element.parent = this
		invalidate()
	}
	
	operator fun Element.unaryPlus() = add(this)
	
	/** Removes a child element */
	open fun remove(element: Element): Boolean {
		return elements.remove(element).also { success ->
			if (success) element.parent = null
		}
	}
	
	/** Removes a child with the specified index */
	open fun remove(index: Int): Boolean {
		return (index >= 0 && index < elements.size).also { success ->
			if (success) elements.removeAt(index)
		}
	}
}
