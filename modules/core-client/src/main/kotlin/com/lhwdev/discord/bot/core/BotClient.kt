package com.lhwdev.discord.bot.core

import kotlinx.coroutines.withTimeout
import java.io.File


class BotClient(val session: BotSocketSession) {
	suspend fun invoke(command: Packet.Command): Packet.Result {
		session.write(command)
		
		// guard!
		return withTimeout(5000) { session.read() } as Packet.Result
	}
	
	suspend fun loadBot(path: String, botClass: String, autoReload: Boolean = true) {
		invoke(
			Packet.Command.LoadBot(
				path = File(path).absolutePath,
				botClass = botClass,
				autoReload = autoReload
			)
		) as Packet.Result.LoadBotResult
	}
}
