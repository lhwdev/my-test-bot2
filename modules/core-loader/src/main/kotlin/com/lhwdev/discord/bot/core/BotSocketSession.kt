package com.lhwdev.discord.bot.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

@Suppress("BlockingMethodInNonBlockingContext")
@OptIn(ExperimentalSerializationApi::class, ExperimentalCoroutinesApi::class)
class BotSocketSession(inputStream: InputStream, outputStream: OutputStream, val scope: CoroutineScope) {
	val input = Channel<Packet>()
	val output = Channel<Packet>()
	
	private fun io(block: suspend CoroutineScope.() -> Unit) {
		scope.launch {
			withContext(Dispatchers.IO, block)
		}
	}
	
	init {
		io {
			val stream = LineEofInputStream(inputStream)
			while(!input.isClosedForSend) {
					try {
					val packet = runInterruptible {
						BotCoreConstants.json.decodeFromStream(Packet.serializer(), stream)
					}
					input.send(packet)
					stream.resetToNextLine()
				} catch(_: Exception) {
					try {
						if(!input.isClosedForSend) input.close()
					} catch(_: Throwable) {}
				}
			}
		}
		
		io {
			val stream = outputStream
			while(!output.isClosedForReceive) {
				val packet = output.receive()
				runInterruptible {
					BotCoreConstants.json.encodeToStream(Packet.serializer(), packet, stream)
					stream.write(sNewLineByte)
					stream.flush()
				}
			}
		}
		
		input.invokeOnClose {
			try {
				if(!output.isClosedForSend) output.close()
			} catch(_: Throwable) {}
		}
	}
	
	
	suspend fun read(): Packet = if(input.isClosedForReceive) {
		Packet.Disconnected
	} else {
		input.receive()
	}
	
	suspend fun write(packet: Packet) {
		output.send(packet)
	}
	
	suspend fun readHello(): Packet.Hello {
		val packet = read() as? Packet.Hello ?: error("expected packet Hello")
		require(packet == BotCoreConstants.hello)
		return packet
	}
}
