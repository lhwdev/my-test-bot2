package com.lhwdev.discord.bot.core

import com.lhwdev.discord.bot.core.config.Config
import dev.kord.core.Kord
import dev.kord.core.event.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.File
import java.net.*
import java.util.concurrent.Executors


private val botDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
private val botScope = CoroutineScope(botDispatcher)

private val botScopes = ThreadLocal<BotInstance>()


private class ServerData(val kord: Kord) {
	val bots = mutableMapOf<String, BotInstance>()
}

private class BotInstance(val bot: Bot, val classLoader: URLClassLoader, val kord: Kord)


suspend fun main() {
	val kord = Kord(token = Config.secret.discord.token)
	val data = ServerData(kord = kord)
	
	kord.launch(Dispatchers.IO) {
		val server = openServer()
		while(true) {
			val client = server.accept()
			kord.launch { clientHandler(data, client) }
		}
	}
	
	kord.login()
}


private fun openServer(): ServerSocket {
	for(port in BotCoreConstants.portRange) {
		try {
			return ServerSocket(port)
		} catch(_: PortUnreachableException) {}
	}
	error("all reserved ports are busy")
}

@OptIn(ExperimentalStdlibApi::class)
private suspend fun clientHandler(data: ServerData, socket: Socket) = withContext(Dispatchers.IO) {
	val session = BotSocketSession(
		inputStream = socket.getInputStream(),
		outputStream = socket.getOutputStream(),
		scope = this
	)
	
	println("new client: ${socket.localSocketAddress}")
	
	session.write(BotCoreConstants.hello)
	require(session.read() == BotCoreConstants.hello)
	
	println("Connected to ${socket.localSocketAddress}: ${BotCoreConstants.hello}")
	
	while(true) {
		val command = session.read()
		println("Received command $command")
		
		if(command !is Packet.Command) {
			println("drop packet $command: not Packet.Command")
			continue // ?
		}
		
		val result = when(command) {
			is Packet.Command.LoadBot -> data.loadBot(command)
		}
		session.write(result)
	}
}


@OptIn(ExperimentalStdlibApi::class)
private fun ServerData.loadBot(command: Packet.Command.LoadBot): Packet.Result.LoadBotResult {
	val path = command.path
	@Suppress("UnnecessaryVariable")
	val key = path
	
	if(command.autoReload) {
		val previous = bots[key]
		
		if(previous != null) {
			previous.kord.cancel(CancellationException("reload bot"))
			previous.classLoader.close()
		}
		
		val loader = URLClassLoader(arrayOf(File(path).toURI().toURL().also { println(it) }))
		val bot = try {
			loader.loadClass(command.botClass).kotlin.objectInstance as Bot
		} catch(e: Throwable) {
			println("Error occurred while loading ${command.path}!${command.botClass}")
			e.printStackTrace()
			return Packet.Result.LoadBotResult(success = false, loadedAlready = previous != null)
		}
		
		val scopedKord = Kord(
			resources = kord.resources,
			cache = kord.cache,
			gateway = kord.gateway,
			rest = kord.rest,
			selfId = kord.selfId,
			eventFlow = kord.events as MutableSharedFlow<Event>,
			dispatcher = kord.coroutineContext[CoroutineDispatcher]!!
		)
		scopedKord.coroutineContext.job.invokeOnCompletion { scopedKord.cancel() }
		
		val new = BotInstance(bot = bot, classLoader = loader, kord = scopedKord)
		bots[key] = new
		
		botScope.launch {
			new.runInBotScope {
				with(bot) {
					scopedKord.initialize()
				}
			}
		}
		
		return Packet.Result.LoadBotResult(success = true, loadedAlready = previous != null)
	} else {
		return Packet.Result.LoadBotResult(success = true, loadedAlready = key in bots)
	}
}


private inline fun <R> BotInstance.runInBotScope(block: () -> R): R = botScopes.withValue(this, block)
