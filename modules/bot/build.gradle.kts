import com.lhwdev.build.*


plugins {
	application
	id("com.github.johnrengelman.shadow") version "7.1.1"
	
	kotlin("jvm")
	kotlin("plugin.serialization")
	
	id("common-plugin")
}

kotlin {
	setup()
}


dependencies {
	// this bot is loaded by core
	compileOnly(project(":core"))
	implementation(project(":core-client"))
	
	implementation(libs.kord)
	implementation(libs.serialization.core)
	implementation(libs.serialization.yaml)
	implementation(libs.coroutines)
}

application {
	mainClass.set("com.lhwdev.discord.mytestbot.MainKt")
}

tasks.named<JavaExec>("run") {
	dependsOn("shadowJar", ":core-server:jar")
	
	workingDir = rootProject.file(".")
}
