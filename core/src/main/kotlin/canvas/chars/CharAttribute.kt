package com.github.mnemotechnician.lettui.canvas.chars

enum class CharAttribute(val codeEnabled: Int, val codeDisabled: Int) {
	RESET(0, -1),
	BOLD(1, 22),
	FAINT(2, 22),
	ITALIC(3, 23),
	UNDERLINE(4, 24),
	REVERSE_COLOR(7, 27),
	STRIKE(9, 29);

	val enabledAnsi = "\u001b[${codeEnabled}m"
	val disabledAnsi = "\u001b[${codeDisabled}m"
	
	/** Get the ANSI escape code of the specified state of this attribute */
	fun ansi(enabled: Boolean) = if (enabled) enabledAnsi else disabledAnsi
}
