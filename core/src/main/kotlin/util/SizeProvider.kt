package com.github.mnemotechnician.lettui.util

import java.lang.ProcessBuilder.Redirect.*
import java.io.*
import com.github.mnemotechnician.lettui.canvas.*

interface SizeProvider {
	val width: Int
	val height: Int

	fun update()
}

class FixedSizeProvider(
	override var width: Int,
	override var height: Int
) : SizeProvider {
	override fun update() {}
}

///** 
// * Provides the size of the screen, which is updated up to 2 times a second
// * Every instance of this class starts a new subprocess, which should be finished by calling ScreenSizeProvider.close()
// */
//open class ScreenSizeProvider : SizeProvider, AutoCloseable {
//	override var width = 0
//		protected set
//	
//	override var height = 0
//		protected set
//	
//	protected val inStream = FileInputStream(FileDescriptor.`in`)
//	protected val process = ProcessBuilder("tput", "-S").start()
//	protected val processInReader = process.inputStream.bufferedReader()
//	protected val processOutWriter = process.outputStream.bufferedWriter()
//	protected var lastCheck = 0L
//	protected var closed = false
//	
//	override open fun update() {
//		if (closed) throw IllegalStateException("this resource was closed")
//
//		if (System.currentTimeMillis() - lastCheck > 500L) {
//			lastCheck = System.currentTimeMillis()
//			
//			//first we write `cols` and `lines` into it's stdin, then we read the outputs of both commands from its stdout
//			//this may cause errors.
//			process.outputStream.bufferedWriter().let {
//				it.write("cols\n")
//				it.write("lines\n")
//				it.flush()
//			}
//
//			processOutWriter.write("cols\n")
//			processOutWriter.write("lines\n")
//			processOutWriter.flush()
//
//			Thread.sleep(100L) //TODO: THIS IS FOR DEBUG PURPOSES ONLY
//
//			//while (process.inputStream.available() < 1) print('w')
//			width = processInReader.readLine().toInt()
//
//			//while (process.inputStream.available() < 1) print('h');
//			height = processInReader.readLine().toInt()
//		}
//	}
//
//	override fun close() {
//		process.destroyForcibly()
//		processInReader.close()
//		processOutWriter.close()
//		closed = true
//	}
//
//	protected fun finalize() {
//		if (!closed) close() //just in case. keeping a useless process in the background is not a good idea.
//	}
//}

