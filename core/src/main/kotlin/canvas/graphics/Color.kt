package com.github.mnemotechnician.lettui.canvas.graphics

import kotlin.math.*
import com.github.mnemotechnician.lettui.canvas.chars.*

/**
 * Represents a 32-bit ARGB color.
 *
 * Note: lettui supports colors with transparency, but terminals, on the other hand, don't.
 * As a workaround, non-opaque colors are dimmed at the stage of flushing the output to terminal.
 */
open class ImmutableColor(red: Int, green: Int, blue: Int, alpha: Int = 255) {
	open val red = clampComponent(red)
	open val green = clampComponent(green)
	open val blue = clampComponent(blue)
	open val alpha = clampComponent(alpha)

	/** Constructs a color from r, g, b and a components in the range of 0..1 (e.g. [0.32, 0.45, 0.98]) */
	constructor(red: Double, green: Double, blue: Double, alpha: Double = 1.0) :
		this((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(), (alpha * 255).toInt())
	
	/** Copies this color as a mutable color */
	fun copy() = Color(red, green, blue, alpha)

	/** Copies this color as an immutable color */
	fun copyImmutable() = ImmutableColor(red, green, blue, alpha)

	/** Creates a new color that's a sum of two colors, with it's alpha matching that of the first color */
	operator fun plus(other: Color) = copy().let { it += other }

	/** Creates a new colow which is a differnce between two colors, with it's alpha matching that of the first color  */
	operator fun minus(other: Color) = copy().let { it -= other }
	
	/** Simmilar to [blendWith], but creates a nww color */
	fun blend(red: Int, green: Int, blue: Int, alpha: Int) = copy().also { it.blendWith(red, green, blue, alpha) }

	/** Simmilar to [blendWith], but creates a nww color */
	fun blend(other: Color) = copy().also { it.blendWith(other) }
	
	/** Returns an ansi escape sequence that sets the foreground color to this color */
	fun toForegroundAnsi() = CharColor.Foreground.of(red * alpha / 255, green * alpha / 255, blue * alpha / 255)

	/** Returns an ansi escape sequence that sets the background color to this color */
	fun toBackgroundAnsi() = CharColor.Background.of(red * alpha / 255, green * alpha / 255, blue * alpha / 255)

	companion object {
		/** The range of color components, 0~255 */
		val colorRange = 0..255
		val colorRangeDouble = 0.0..1.0

		val BLACK = grayscale(0)
		val WHITE = grayscale(255)
		val RED = of("#f00")
		val GREEN = of("#0f0")
		val BLUE = of("#00f")
		val YELLOW = of("#ff0")
		val CYAN = of("#0ff")
		val PINK = of("#f0f")
		val ORANGE = of("#FFA500")
		val PURPLE = of("800080")
		/** Fully transparent black */
		val EMPTY = of("#0000")

		/** Constructs a grayscale color, the value must be in 0..255 */
		fun grayscale(gray: Int) = ImmutableColor(gray, gray, gray)

		/** Constructs a grayscale color, the value must be in 0..1 */
		fun grayscale(gray: Double) = ImmutableColor(gray, gray, gray)

		fun clampComponent(component: Int) = component.coerceIn(colorRange)
		fun clampComponent(component: Double) = component.coerceIn(colorRangeDouble)

		/**
		 * Decodes a string representing a color, such as "#rrggbbaa", "#rgb", etc
		 * @throws IllegalArgumentException if the color string is invalid
		 */
		fun of(color: String) = Color.of(color).copyImmutable()
	}
}


/**
 * Represents a mutable 32-bit ARGB color.
 *
 * Note: lettui supports colors with transparency, but terminals, on the other hand, don't.
 * As a workaround, non-opaque colors are dimmed at the stage of flushing the output to terminal.
 */
open class Color(
	override var red: Int,
	override var green: Int,
	override var blue: Int,
	override var alpha: Int
) : ImmutableColor(red, green, blue, alpha) {
	/** Constructs a color from r, g, b and a components in the range of 0..1 (e.g. [0.32, 0.45, 0.98]) */
	constructor(red: Double, green: Double, blue: Double, alpha: Double = 1.0) :
		this((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(), (alpha * 255).toInt())
	
	/** Clamps the r, g and b components of this color */
	fun clamp() {
		red = red.coerceIn(colorRange)
		green = green.coerceIn(colorRange)
		blue = blue.coerceIn(colorRange)
		alpha = alpha.coerceIn(colorRange)
	}
	
	/** Sets this color to match the other color */
	fun set(red: Int, green: Int, blue: Int, alpha: Int = this.alpha) {
		this.red = red
		this.green = green
		this.blue = blue
		this.alpha = alpha
	}

	/** Sets this color to match the other color */
	fun set(other: ImmutableColor) = set(other.red, other.green, other.blue, other.alpha)

	/** Adds the other color to this color */
	operator fun plusAssign(other: ImmutableColor) {
		red += other.red
		green += other.green
		blue += other.blue
		alpha += other.alpha
		clamp()
	}

	
	/** Substracts the other color from this color. Does not affect their alpha. */
	operator fun minusAssign(other: ImmutableColor) {
		red -= other.red
		green -= other.green
		blue -= other.blue
		clamp()
	}

	/** Blends this color with another color, with the other color being the foreground and this color being the background */
	fun blendWith(foregroundRed: Int = 0, foregroundGreen: Int = 0, foregroundBlue: Int = 0, foregroundAlpha: Int = 0) {
		if (foregroundAlpha == 0) return; //no need to blend, the foreground color is fully transparent
		if (alpha == 0) {
			set(foregroundRed, foregroundGreen, foregroundBlue, foregroundAlpha)
			return; //no need to blend, only the foreground is visible
		}

		//todo: this can be optimized
		val newAlpha = 255 - (255 - foregroundAlpha) * (255 - alpha) / 255;
		val cf1 = foregroundAlpha.toFloat() / newAlpha
		val cf2 = (alpha / 255f) * (255 - foregroundAlpha) / newAlpha

		red = (foregroundRed * cf1 + red * cf2).toInt()
		green = (foregroundGreen * cf1 + green * cf2).toInt()
		blue = (foregroundBlue * cf1 + blue * cf2).toInt()
		alpha = newAlpha
	}

	/** Blends this color with another color, with the other color being the foreground and this color being the background */
	fun blendWith(other: ImmutableColor) = blendWith(other.red, other.green, other.blue, other.alpha)

	companion object {
		/**
		 * Decodes a string representing a color, such as "#rrggbbaa", "#rgb", etc
		 * @throws IllegalArgumentException if the color string is invalid
		 */
		fun of(color: String): Color {
			var begin = if (color.startsWith('#')) 1 else 0
			val realLength = color.length - begin

			require(realLength.let { it != 3 || it != 4 || it != 6 || it != 8 }) { "This string is not a valid color string" }

			return if (realLength == 3 || realLength == 4) {
				Color(
					hexCharCode(color[begin]) * 17,
					hexCharCode(color[begin + 1]) * 17,
					hexCharCode(color[begin + 2]) * 17,
					if (realLength >= 4) hexCharCode(color[begin + 3]) * 17 else 255
				)
			} else {
				Color(
					doubleHexCode(color[begin], color[begin + 1]),
					doubleHexCode(color[begin + 2], color[begin + 3]),
					doubleHexCode(color[begin + 4], color[begin + 5]),
					if (realLength >= 8) doubleHexCode(color[begin + 6], color[begin + 7]) else 256
				)
			}
		}
	}
}

/** Tries to convert this string to a color, returns null if it's invalid */
fun String.toColorOrNull() = try {
	Color.of(this)
} catch (ignored: IllegalArgumentException) {
	null
}

/** Tries to convert this string to a color */
fun String.toColor() = Color.of(this)

private fun hexCharCode(char: Char) = when {
	char <= '9' && char >= '0' -> char - '0'
	char <= 'f' && char >= 'a' -> char - 'a' + 10
	char <= 'F' && char >= 'A' -> char - 'A' + 10
	else -> throw IllegalArgumentException("Invalid color code point: '$char'")
}

private fun doubleHexCode(first: Char, second: Char) = hexCharCode(first) * 16 + hexCharCode(second)
