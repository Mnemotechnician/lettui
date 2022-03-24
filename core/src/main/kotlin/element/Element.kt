package com.github.mnemotechnician.lettui.element

import kotlin.math.*
import com.github.mnemotechnician.lettui.canvas.*
import com.github.mnemotechnician.lettui.element.listener.*
import com.github.mnemotechnician.lettui.element.border.*
import com.github.mnemotechnician.lettui.util.*

/** A root class for all elements */
abstract class Element {
	/** The parent element. Null if this element is the root */
	var parent: Element? = null
	
	/** The real x position of the element. Used internally, but SHOULD ONLY be modified by the parent element. */
	var x: Int = 0
		protected set
	/** The real y position of the element. Used internally, but SHOULD ONLY be modified by the parent element. */
	var y: Int = 0
		protected set
	/** The real width of the element. Used internally, but SHOULD ONLY be modified by the parent element. */
	var width: Int = 0
		protected set
	/** The real height of the element. Used internally, but SHOULD ONLY be modified by the parent element. */
	var height: Int = 0
		protected set
	/** Whether this elements has to recalculate it's minWidth and minHeight. */
	open var needsLayout = true

	/** The minimum width of the element. Set by the element itself. */
	protected var minWidth: Int = 0
	/** The minimum width of the element. Set by the element itself. */
	protected var minHeight: Int = 0
	
	/**
	 * The preferred width of the element. This value is usually set by the user but it also accounts for the minimum width.
	 * The raw user-preferred width is stored in [rawPrefWidth]
	 * Parents should account for this value and never change it. 
	 */
	var prefWidth: Int
		get() = max(rawPrefWidth, minWidth)
		set(value: Int) { rawPrefWidth = value; invalidate() }
	var rawPrefWidth: Int = 0

	/** Simmilar to [prefWidth] */
	var prefHeight: Int 
		get() = max(rawPrefHeight, minHeight)
		set(value: Int) { rawPrefHeight = value; invalidate() }
	var rawPrefHeight: Int = 0

	/** Optional border. Some elements may not account for it. */
	var border by listened(Border.None) { invalidate() }
	
	/** The chain-like update listener */
	protected var updateListener: UpdateListener? = null
	
	/** Updates the state of the element. Should be invoked even if the element is not visible. */
	open fun update() {
		//if there's no parent, its preferred size is its actual size, nobody can set that for it.
		if (parent == null) {
			width = prefWidth
			height = prefHeight
		}

		updateListener?.invoke()
	}
	
	/** Draws the element. */
	open fun draw(canvas: TextCanvas) {
		if (needsLayout) layout()
	}

	/** Recalculates the [minWidth] and [minHeight] of the element, also repositions the children in case of groups. */
	open fun layout() {
		needsLayout = false
	}
	
	/** Marks this element and all of it's parents as subjects for re-layouting */
	open fun invalidate() {
		needsLayout = true
		if (parent != null && !parent!!.needsLayout) parent!!.invalidate()
	}
	
	/** Forcibly changes the position of the element and invalidates it. */
	open fun setPosition(x: Int, y: Int) {	
		if (this.x != x || this.y != y) {
			invalidate()
			this.x = x
			this.y = y
		}
	}

	/** Forcibly changes the size of the element and invalidates it. Should only be used by parent elements */
	open fun setSize(width: Int, height: Int) {
		if (this.width != width || this.height != height) {
			this.width = width
			this.height = height
			invalidate()
		}
	}

	/** Changes the preferred size of the element and invalidates it. */
	open fun setPrefSize(width: Int, height: Int) {
		if (this.prefWidth != width || this.prefHeight != height) {
			prefWidth = width
			prefHeight = height
			invalidate()
		}
	}

	/** If this element's parent is a Group, removes it from the said group. Does nothing otherwise. */
	open fun orphan() {
		(parent as? Group)?.remove(this)
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
