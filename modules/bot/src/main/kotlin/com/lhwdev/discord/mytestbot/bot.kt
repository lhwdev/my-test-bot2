package com.lhwdev.discord.mytestbot

import com.lhwdev.discord.bot.core.Bot
import dev.kord.core.Kord
import dev.kord.core.on
import kotlinx.coroutines.launch


@Suppress("unused")
object BotMain : Bot {
	override fun Kord.initialize() {
		botMain()
	}
}


private fun Kord.botMain() = launch {
	on<> {  }
}
