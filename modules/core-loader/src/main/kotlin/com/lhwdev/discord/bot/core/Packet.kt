package com.lhwdev.discord.bot.core

import kotlinx.serialization.Serializable


@Serializable
sealed class Packet {
	@Serializable
	data class Hello(val name: String, val by: String, val version: String) : Packet()
	
	@Serializable
	sealed class Command : Packet() {
		@Serializable
		class LoadBot(val path: String, val botClass: String, val autoReload: Boolean) : Command()
	}
	
	@Serializable
	sealed class Result : Packet() {
		@Serializable
		class LoadBotResult(val success: Boolean, val loadedAlready: Boolean): Result()
		
	}
	
	object Disconnected : Packet()
}
