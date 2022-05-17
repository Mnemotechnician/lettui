package com.github.mnemotechnician.lettui.canvas

import com.github.mnemotechnician.lettui.canvas.graphics.*

abstract class TextCanvas {
	abstract val width: Int
	abstract val height: Int

	/** Renders a char at the specified position with the specified attributes */
	abstract fun render(
		char: Char, x: Int, y: Int,
		bold: Boolean = false, italic: Boolean = false, underline: Boolean = false, strikethrough: Boolean = false,
		foreground: ImmutableColor = ImmutableColor.WHITE,
		background: ImmutableColor = ImmutableColor.BLACK
	)

	/** Renders a char at the specified position with the default attributes */
	open fun render(char: Char, x: Int, y: Int) = render(char, x, y, false)
	
	/** Renders a string, starting at the specified position with the default attributes */
	open fun render(string: String, x: Int, y: Int) = render(string, x, y, false)

	/** Renders a string, starting at the specified position, with the specified attributes */
	open fun render(
		string: String, x: Int, y: Int,
		bold: Boolean = false, italic: Boolean = false, underline: Boolean = false, strikethrough: Boolean = false,
		foreground: ImmutableColor = ImmutableColor.WHITE,
		background: ImmutableColor = ImmutableColor.BLACK
	) {
		string.forEachIndexed { index, char ->
			render(char, x + index, y, bold, italic, underline, strikethrough, foreground, background)
		}
	}

	/** Fills a rectangle (from (x1, y1) to (x2, y2)) with the same character */
	open fun render(
		char: Char, x1: Int, y1: Int, x2: Int, y2: Int,
		bold: Boolean = true, italic: Boolean = false, underline: Boolean = false, strikethrough: Boolean = false,
		foreground: ImmutableColor = ImmutableColor.WHITE,
		background: ImmutableColor = ImmutableColor.BLACK
	) {
		for (x in x1..x2) {
			for (y in y1..y2) {
				render(char, x, y, bold, italic, underline, strikethrough, foreground, background)
			}
		}
	}

	/** Flushes the canvas, if applicable */
	abstract fun flush()

	/** Resizes the canvas, if applicable */
	abstract fun resize(newWidth: Int, newHeight: Int)
}
