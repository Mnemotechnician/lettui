package com.github.mnemotechnician.lettui.util

import kotlin.properties.*
import kotlin.reflect.*

/** Calls [onChange] whenever a value gets assigned to the property */
inline fun <reified T> listened(initial: T, crossinline onChange: (T) -> Unit): PropertyListener<T> {
	return object : PropertyListener<T>(initial) {
		override open operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
			this.value = value
			onChange(value)
		}
	}
}

abstract class PropertyListener<T>(var value: T) {
	open operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
		return value
	}

	open operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		this.value = value
	}
}
