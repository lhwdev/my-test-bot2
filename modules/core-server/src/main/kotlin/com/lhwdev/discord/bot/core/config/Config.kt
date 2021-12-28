package com.lhwdev.discord.bot.core.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import java.io.File


private val config = Yaml.default

fun <T> config(path: String, serializer: KSerializer<T>): T =
	config.decodeFromString(serializer, File("config/${path.replace('.', '/')}.yaml").readText())


@Suppress("ClassName")
object Config {
	object secret {
		@Serializable
		class Discord(val token: String)
		
		val discord = config("secret.discord", Discord.serializer())
	}
}
