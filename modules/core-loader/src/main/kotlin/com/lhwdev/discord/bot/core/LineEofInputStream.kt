package com.lhwdev.discord.bot.core

import java.io.InputStream


const val sNewLineByte = '\n'.code


class LineEofInputStream(val base: InputStream) : InputStream() {
	private var eol = false
	
	fun resetToNextLine() {
		require(eol) { "Not in End-of-Line state" }
		resetLineState()
	}
	
	fun skipResetToNextLine() {
		close()
		resetLineState()
	}
	
	private fun resetLineState() {
		eol = false
	}
	
	
	override fun read(): Int {
		if(eol) return -1
		
		val value = base.read()
		if(value == -1) return -1
		
		if(value == sNewLineByte) {
			eol = true
			return -1
		}
		
		return value
	}
	
	override fun close() {
		if(!eol) while(true) {
			val value = base.read()
			if(value == -1 || value == sNewLineByte) break
		}
		
		eol = true
	}
	
	fun closeGlobal() {
		base.close()
	}
}
