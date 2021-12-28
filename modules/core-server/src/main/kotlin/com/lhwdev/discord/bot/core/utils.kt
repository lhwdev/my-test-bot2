package com.lhwdev.discord.bot.core


inline fun <T, R> ThreadLocal<T>.withValue(value: T, block: () -> R): R {
	val previous = get()
	set(value)
	
	return try {
		block()
	} finally {
		set(previous)
	}
}
