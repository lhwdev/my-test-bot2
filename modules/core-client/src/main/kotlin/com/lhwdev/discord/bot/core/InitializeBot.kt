package com.lhwdev.discord.bot.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.*



/**
 * This can be run from bot implementation, not from core.
 */
suspend fun initializeBot(
	scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
	startIfNotFound: Boolean = true
): BotClient = withContext(Dispatchers.IO) {
	var error: IOException? = null
	
	for(port in BotCoreConstants.portRange) {
		val address = InetSocketAddress(InetAddress.getLocalHost(), port)
		try {
			return@withContext connectToBot(address, scope)
		} catch(e: IOException) {
			error = e
		}
	}
	
	if(!startIfNotFound) throw IOException("could not find open port for BotCore", error)
	// val serverPath = "modules/core-server/build/libs/core-server.jar"
	//
	// val javaHome = System.getProperty("java.home")
	// val javaBin = "$javaHome/bin/java"
	// val classpath = System.getProperty("java.class.path")
	// val className = "com.lhwdev.discord.bot.core.MainKt"
	//
	// val command = listOf(
	// 	javaBin,
	// 	"-cp",
	// 	classpath,
	// 	"-jar",
	// 	File(serverPath).canonicalPath,
	// 	className,
	// )
	// println(command)
	// val builder = ProcessBuilder(command)
	// val process = builder.inheritIO().start()
	//
	// println("Couldn't find server; started core-server and waiting it start")
	// delay(1000)
	TODO()
	
	initializeBot(scope, startIfNotFound = false)
}


suspend fun connectToBot(address: SocketAddress, scope: CoroutineScope): BotClient {
	val socket = Socket()
	
	val session = withContext(Dispatchers.IO) {
		socket.connect(address)
		BotSocketSession(
			inputStream = socket.getInputStream(),
			outputStream = socket.getOutputStream(),
			scope = scope
		)
	}
	val hello = session.readHello()
	session.write(hello)
	
	println("connected to bot client $address")
	
	return BotClient(session = session)
}
