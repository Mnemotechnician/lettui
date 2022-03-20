package com.github.mnemotechnician.lettui.element

import com.github.mnemotechnician.lettui.canvas.*
import com.github.mnemotechnician.lettui.element.listener.*
import com.github.mnemotechnician.lettui.element.border.*
import com.github.mnemotechnician.lettui.util.*

/** A root class for all elements */
abstract class Element {
	/** The parent element. Null if this element is the root */
	var parent: Element? = null

	var x: Int = 0
		protected set
	var y: Int = 0
		protected set
	var width: Int = 0
		protected set
	var height: Int = 0
		protected set
	var needsLayout = false
	
	/** Optional border. Some elements may not account for it. */
	var border by listened(Border.None) { invalidate() }
	
	/** The chain-like update listener */
	protected var updateListener: UpdateListener? = null
	
	/** Updates the state of the element */
	open fun update() {
		updateListener?.invoke()
	}
	
	/** Draws the element */
	open fun draw(canvas: TextCanvas) {
		if (needsLayout) layout()
	}

	/** Forcibly recalculates the dimensions of the element */
	open fun layout() {
		needsLayout = false
	}
	
	/** Marks this element as a subject for re-layouting */
	open fun invalidate() {
		needsLayout = true
		parent?.invalidate()
	}
	
	/** Forcibly changes the position of the element. May have unwanted consequences. */
	open fun setPosition(x: Int, y: Int) {	
		if (this.x != x || this.y != y) {
			invalidate()
			this.x = x
			this.y = y
		}
	}

	/** Forcibly changes the size of the element. May have unwanted consequences. */
	open fun setSize(width: Int, height: Int) {
		if (this.width != width || this.height != height) {
			this.width = width
			this.height = height
			invalidate()
		}
	}

	open fun addUpdateListener(listener: UpdateListener) {
		if (updateListener == null) {
			updateListener = listener
		} else {
			updateListener!!.then(listener)
		}
	}

	inline fun onUpdate(crossinline updateAction: () -> Unit) {
		addUpdateListener(object : UpdateListener() {
			override fun action() = updateAction()
		})
	}
}
