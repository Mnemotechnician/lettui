/*
 * A simple example demonstrating a small silly playerless game create with Lettui.
 */

package com.github.mnemotechnician.lettui.examples.deathbattle

import java.util.Timer
import kotlin.random.*
import kotlin.concurrent.*
import com.github.mnemotechnician.lettui.*
import com.github.mnemotechnician.lettui.util.*
import com.github.mnemotechnician.lettui.element.*
import com.github.mnemotechnician.lettui.element.impl.*
import com.github.mnemotechnician.lettui.element.layout.*
import com.github.mnemotechnician.lettui.element.border.*
import com.github.mnemotechnician.lettui.canvas.*
import com.github.mnemotechnician.lettui.canvas.graphics.* 

val UNIT_AMOUNT = 20

val participants = arrayOf(
	"mono", "poly", "mega", "quad", "oct",
	"flare", "horizon", "zenith", "antumbra", "eclipse",
	"dagger", "mace", "fortress", "scepter", "reign",
	"nova", "pulsar", "quasar", "vela", "corvus",
	"crawled", "spiroct", "atrax", "arkyd", "toxopid"
)

val aliveUnits = ArrayList<Participant>()
val deadUnits = ArrayList<Participant>()

var status: String = ""
lateinit var fighting: LinearLayout
lateinit var dead: LinearLayout

fun main() {
	val ui = Lettui(targetFps = 10, sizeProvider = FixedSizeProvider(150, 35))

	ui.root.apply {
		background.set(Color.GREEN)

		+ Label("The Epic Death Battle").apply {
			border = Border.Rounded
		}
		+ LinearLayout(false, true).apply {	
			+ LinearLayout(true, true).apply {
				+ Label("").apply {
					onUpdate { text = "fighters (${aliveUnits.size})" }
				}

				+ LinearLayout(true).apply {
					prefHeight = UNIT_AMOUNT + 2
					border = Border.Double
					fighting = this
				}
			}
			+ LinearLayout(true, true).apply {
				+ Label("").apply {
					onUpdate { text = "dead (${deadUnits.size})" }
				}

				+ LinearLayout(true).apply {
					prefHeight = UNIT_AMOUNT + 2
					border = Border.Double
					dead = this
				}
			}
		}
		+ Label("").apply {
			border = Border.Rounded
			onUpdate { text = status }
		}
	}

	ui.setup()
	
	participants.shuffle()
	for (i in 0 until UNIT_AMOUNT) {
		Participant(participants[i]).spawn()
		Thread.sleep(200L)
	}

	fixedRateTimer(period = 750L) {
		if (aliveUnits.size >= 2) {
			aliveUnits.shuffle()

			val attacker = aliveUnits[0]
			val victim = aliveUnits[1]

			attacker.kill(victim)
		} else {
			//it's very unlikely that there will ever be 0 alive units
			val winner = aliveUnits.first()
			status = "$winner has won the battle with ${winner.kills} kills!"
		}
	}
}

class Participant(val name: String) {
	var alive: Boolean = true
		private set
	val label = Label(name)

	var kills = 0

	fun spawn() {
		label.orphan()
		deadUnits.remove(this)
		aliveUnits.add(this)
		fighting.add(label)
		
		status = "$this joins the battle!"
		kills = 0
	}

	fun kill(other: Participant) {
		other.apply {
			label.orphan()
			aliveUnits.remove(this)
			deadUnits.add(this)
			dead.add(label)
		}
		status = "$this kills $other!"
		kills++
	}

	override fun toString() = name
}
