package com.github.mnemotechnician.lettui.element.border

/** Represents a set of box drawing characters */
open class BoxDrawing(
	val hline: Char,
	val vline: Char,
	val cornerTopRight: Char,
	val cornerRightBotton: Char,
	val cornerBottomLeft: Char, 
	val cornerLeftTop: Char,
	val tripleLeft: Char,
	val tripleRight: Char,
	val tripleTop: Char,
	val tripleBottom: Char,
	val cross: Char,
	val diagonalCross: Char,
	val diagonalLeftRight: Char,
	val diagonalRightLeft: Char
) {
	val box = '█'
	val vhalfBox = '▀'
	val hhalfBox = '▐'
	val horizontalBoxes = arrayOf('▁', '▂', '▃', '▄', '▅', '▆', '▇', '█')
	val verticalBoxes = arrayOf('▏', '▎', '▍', '▌', '▋', '▊', '▉', '█')
	
	companion object {
		val Light = BoxDrawing('─', '│', '└', '┌', '┐', '┘', '├', '┤', '┴', '┬', '┼', '╳', '╲', '╱')
		val Rounded = BoxDrawing('─', '│', '╰', '╭', '╮', '╯', '├', '┤', '┴', '┬', '┼', '╳', '╲', '╱')
		val Double = BoxDrawing('═', '║', '╚', '╔', '╗', '╝', '╠', '╣', '╩', '╦', '╬', '╳', '╲', '╱')
		val Bold = BoxDrawing('━', '┃', '┗', '┏', '┓', '┛', '┣', '┫', '┻', '┳', '╋', '╳', '╲', '╱')
	}
}
