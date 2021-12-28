package com.lhwdev.discord.mytestbot

import com.lhwdev.discord.bot.core.initializeBot
import kotlin.system.exitProcess


suspend fun main() {
	val client = initializeBot()
	client.loadBot(path = "modules/bot/build/libs/bot-all.jar", botClass = "com.lhwdev.discord.mytestbot.BotMain")
	exitProcess(0)
}
