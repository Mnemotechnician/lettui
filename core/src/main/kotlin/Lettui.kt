package com.github.mnemotechnician.lettui

import java.io.*
import java.util.*
import kotlin.concurrent.*
import kotlinx.coroutines.*
import com.github.mnemotechnician.lettui.util.*
import com.github.mnemotechnician.lettui.canvas.*
import com.github.mnemotechnician.lettui.element.*
import com.github.mnemotechnician.lettui.element.layout.*
import com.github.mnemotechnician.lettui.element.border.*

/** The entry point of this library. Acts as a core of the TUI. */
open class Lettui(
	val canvas: TextCanvas = RootCanvas(50, 20),
	var sizeProvider: SizeProvider = FixedSizeProvider(50, 20),
	val root: Group = LinearLayout(true, true).also { it.border = Border.Rounded },
	val targetFps: Int = 60
) {
	private var hasStopped = false
	var timer: Timer? = null

	private var lastFrame: Long = 0
	val timeScale: Float get() = fps / targetFps
	var fps: Float = 60f
		protected set(value: Float) { field = if (value.isNaN() || value < 0f || value == Float.POSITIVE_INFINITY) 60f else value }
	
	init {	
		require(targetFps > 0) { "targetFps must be >= 0" }
	}

	/** 
	 * Sets this instance of lettui up, assuming there's no other instances currently running.
	 * This method should be called in order to launch the application.
	 **/
	open fun setup() {
		lastFrame = System.currentTimeMillis()
		
		timer = fixedRateTimer(daemon = false, period = 1000L / targetFps) {
			tick()
		}

		print("${CSI}?1049h${CSI}2J") //enable alternative buffer and clear it just in casw

		Runtime.getRuntime().addShutdownHook(thread(start = false) {
			if (!hasStopped) stop() //this is mainly to disable the alternative buffer
		})
	}
	
	/** Stops this instance of lettui. It will not be usable after that.  */
	open fun stop() {
		print("${CSI}?1049l") //disable alternative buffer
		timer?.cancel()
		timer = null

		if (sizeProvider is Closeable) (sizeProvider as Closeable).close()
		hasStopped = true
	}

	open fun tick() {
		val current = System.currentTimeMillis()
		fps = fps * 0.95f + (1000 / (current - lastFrame + 1)) * 0.05f
		lastFrame = current
		
		sizeProvider.update()
		if (canvas.width != sizeProvider.width || canvas.height != sizeProvider.height) {
			canvas.resize(sizeProvider.width, sizeProvider.height)
		}
		
		root.setPrefSize(sizeProvider.width, sizeProvider.height)
		root.update()
		root.draw(canvas)
		
		canvas.flush()
	}
}

///** Same as [Lettui], but also acts as a Coroutine Scope. Not recommended; requires you to include kotlinx.coroutines as a dependency */
//open class LettuiCoroutine(
//	canvas: TextCanvas = TextCanvas(50, 20),
//	sizeProvider: SizeProvider = ScreenSizeProvider(),
//	root: Group = LinearLayout(true).also { it.border = Border.Rounded },
//	targetFps: Int = 60
//) : Lettui(canvas, sizeProvider, root, targetFps), CoroutineScope {
//	override val coroutineContext = SupervisorJob() + Dispatchers.Default
//
//	override fun stop() {
//		super.stop()
//		coroutineContext.cancel()
//	}
//}
