package com.github.mnemotechnician.lettui.canvas

import kotlin.math.*
import com.github.mnemotechnician.lettui.canvas.chars.* 
import com.github.mnemotechnician.lettui.canvas.graphics.*

val CSI = "\u001b["
val EMPTY_CHAR = ' ' //note for myself: YES, IT SHOULD BE A SPACE

/** A canvaa that flushes directly to STDOUT. */
open class RootCanvas(initialWidth: Int, initialHeight: Int) : TextCanvas() {
	override var width: Int = 0
		protected set
	override var height: Int = 0
		protected set

	/** The canvas. (y, x). */
	lateinit var output: Array<CharRow>
		protected set
	
	var cx: Int = 999
	var cy: Int = 999

	init {
		resize(initialWidth, initialHeight)
	}
	
	/** Flushes the contents of this canvas to stdout and resets the state of the canvas */
	override open fun flush() {	
		val string = buildString {
			append("\u001b[1;1H") //put cursor at (0, 0)

			var lastChar: CharPosition? = null

			output.forEach {
				append("\u001b[2K") //clear this line
				it.forEach {
					//only new attributes are appended, attributes that are already present are ignored
					if (it.underline != lastChar?.underline) append(CharAttribute.UNDERLINE.ansi(it.underline))
					if (it.strikethrough != lastChar?.strikethrough) append(CharAttribute.STRIKE.ansi(it.strikethrough))
					if (it.background != lastChar?.background) append(it.background.toBackgroundAnsi())

					//the following attributes have no effect on empty chars
					if (it.char != EMPTY_CHAR) {
						if (it.bold != lastChar?.bold) append(CharAttribute.BOLD.ansi(it.bold))
						if (it.italic != lastChar?.italic) append(CharAttribute.ITALIC.ansi(it.italic))

						if (it.foreground != lastChar?.foreground) append(it.foreground.toForegroundAnsi())
					}

					append(it.char)
					lastChar = it
				}
				append("\u001b[1E") //move cursor to the beginning of the next line
			}

			append("\u001b[$cy;${cx}H") //put the cursor at the current cursor position
		}
		System.out.writeBytes(string.toByteArray()) //write manually to avoid buffering
		setCursor(width - 5, height)
		
		//resst every char
		output.forEach {
			it.forEach {
				it.char = EMPTY_CHAR
				it.resetAttributes()
			}
		}
	}

	operator fun get(x: Int, y: Int) = output.getOrNull(y)?.getOrNull(x)
	
	override open fun render(
		char: Char, x: Int, y: Int,
		bold: Boolean, italic: Boolean, underline: Boolean, strikethrough: Boolean,
		foreground: Color, background: Color
	) {
		get(x, y)?.let {
			it.bold = bold
			it.italic = italic
			it.underline = underline
			it.strikethrough = strikethrough

			it.foreground.blendWith(foreground)
			it.background.blendWith(background)

			it.char = char
		}
	}

	/** Sets the cursor position. */
	fun setCursor(x: Int, y: Int) {
		cx = x
		cy = y
	}


	override fun resize(newWidth: Int, newHeight: Int) {
		if (newWidth == width && newHeight == height) return
		output = Array(newHeight) { CharRow(newWidth) }
		width = newWidth
		height = newHeight
	}
}

@Suppress("NOTHING_TO_INLINE")
class CharRow(var width: Int) {
	var chars = Array(width) { CharPosition() }

	inline operator fun get(index: Int) = chars[index]
	
	inline fun getOrNull(index: Int) = chars.getOrNull(index)
	
	inline fun forEach(crossinline action: (CharPosition) -> Unit) = chars.forEach(action)

	fun resize(newWidth: Int) {
		if (newWidth != width) chars = Array(newWidth) { CharPosition() }
		width = newWidth
	}
}

class CharPosition {
	/** character at this position. May be 0 if not present. */
	var char = EMPTY_CHAR
	var bold = false
	var italic = false
	var underline = false
	var strikethrough = false

	val foreground: Color = Color.WHITE
	val background: Color = Color.BLACK

	fun resetAttributes() {
		bold = false
		italic = false
		underline = false
		strikethrough = false
		foreground.set(Color.WHITE)
		background.set(Color.BLACK)
	}
}
