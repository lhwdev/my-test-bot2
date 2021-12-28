package com.lhwdev.discord.bot.core

import kotlinx.serialization.json.Json


object BotCoreConstants {
	const val version = "1.0.0"
	
	val portRange = 8410..8410
	
	val hello = Packet.Hello(name = "BotCore", by = "lhwdev", version = version)
	
	val json = Json
}
