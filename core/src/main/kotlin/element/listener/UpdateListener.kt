package com.github.mnemotechnician.lettui.element.listener

abstract class UpdateListener {
	var followup: UpdateListener? = null

	operator fun invoke() {
		action()

		if (followup != null) {
			try {
				followup!!()
			} catch (e: StackOverflowError) {
				followup = null //recursive invocation?
			}
		}
	}
	
	/** Adds a follow action at the end of the chain */
	open fun then(other: UpdateListener) {
		var listener = this
		while (listener.followup != null) {
			if (listener == other) throw RuntimeException("Elements in an update chain must be unique!")
			listener = listener.followup!!
		}

		if (listener != other) listener.followup = other
	}

	abstract fun action()
}
