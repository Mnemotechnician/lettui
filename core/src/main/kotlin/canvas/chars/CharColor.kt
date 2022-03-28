package com.github.mnemotechnician.lettui.canvas.chars

import com.github.mnemotechnician.lettui.canvas.graphics.* 

sealed class CharColor(val init: Int) {
	/** Returns an ansi espace sequence corresponding to the specified 3-bit color attribute. The value should be in 0..7 */
	fun of(color: Int) = "\u001b[${init - 8 + color}m"
	/** Returns an ansi sequence corresponding to the specified 24-bit color attribute. The values should be in 0..255. */
	fun of(red: Int, green: Int, blue: Int) = "\u001b[$init;2;$red;$green;${blue}m"
	/** Returns an ansi sequence corresponding to the specified color */
	fun of(color: Color) = of(color.red, color.green, color.blue)

	object Foreground : CharColor(38)

	object Background : CharColor(42)
}
