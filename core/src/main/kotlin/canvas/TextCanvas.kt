package com.github.mnemotechnician.lettui.canvas

import kotlin.math.*

val CSI = "\u001b["
val EMPTY_CHAR = ' ' //note for myself: YES, IT SHOULD BE A SPACE

open class TextCanvas(width: Int, height: Int) {
	var width: Int = width
		private set
	var height: Int = height
		private set

	/** The canvas. (y, x). */
	val output = Array(height) { Array(width) { CharPosition() } }
	
	var cx: Int = 999
	var cy: Int = 999
	
	/** Flushes the contents of this canvas to stdout and resets the state of the canvas */
	open fun flush() {	
		val string = buildString {
			append("\u001b[2J\u001b[1;1H") //clear the screen and put cursor at (0, 0)

			output.forEach {
				it.forEach {
					append(it.char)
					it.char = EMPTY_CHAR
				}
				append("\u001b[1E") //move cursor to the beginning of the next line
			}

			append("\u001b[$cy;${cx}H") //put the cursor at the current cursor position
		}
		System.out.write(string.toByteArray()) //write manually to avoid buffering
		setCursor(width - 5, height)
	}

	/** Renders a char at the current cursor position */
	open fun render(char: Char) = render(char, cx, cy)

	/** Renders a string at the current cursor position */
	open fun render(string: String) = render(string, cx, cy)
	
	/** Renders a character at the specific position if it's in canvas bounds. Does not move the pointer. */
	open fun render(char: Char, x: Int, y: Int) {
		output.getOrNull(y)?.getOrNull(x)?.let { it.char = char }
	}
	
	/** Renders a string, starting at the specified position. */
	open fun render(string: String, x: Int, y: Int) {
		for (i in 0 until string.length) {
			render(string[i], x + i, y)
		}
	}
	
	/** Sets the cursor position. */
	fun setCursor(x: Int, y: Int) {
		cx = x
		cy = y
	}


	fun resize(newWidth: Int, newHeight: Int) {
		TODO("canvas resizing is not implemented")
	}
}

class CharPosition {
	/** character at this position. May be 0 if not present. */
	var char: Char = EMPTY_CHAR
}
