package com.github.mnemotechnician.lettui.examples.simple

import java.util.Timer
import kotlin.random.*
import kotlin.concurrent.*
import com.github.mnemotechnician.lettui.*
import com.github.mnemotechnician.lettui.element.*
import com.github.mnemotechnician.lettui.element.impl.*
import com.github.mnemotechnician.lettui.element.layout.*
import com.github.mnemotechnician.lettui.element.border.*
import com.github.mnemotechnician.lettui.canvas.*

fun main() {
	val ui = Lettui(targetFps = 30, canvas = TextCanvas(90, 20))

	ui.root.apply {
		+ Label("hello world")
		+ Label("uwu")

		+ LinearLayout(false).apply {
			border = Border.Double

			+ Label("what if")
			+ Label("").also {
				onUpdate {
					it.text = Random.nextInt(-50, 10000).toString()
				}
			}
			+ Label(" |yay uwu now i can arrange them horizontally too| ")
			+ LinearLayout(true).apply {
				+ Label("amogus")
				+ Label("sus")
				+ Label("susogus")
				+ Label().also { onUpdate {
					it.text = "fps: ${ui.fps}"
				} }
			}
		}

		+ Label("").also {
			onUpdate {
				it.text = "fps: ${ui.fps}"
			}
		}
	}

	ui.setup()

	Timer().schedule(3500L) {
		ui.root.add(Label("aaaaa"))
	}
}
