import com.lhwdev.build.*


plugins {
	application
	
	kotlin("jvm")
	kotlin("plugin.serialization")
	
	id("common-plugin")
}

kotlin {
	setup()
}


dependencies {
	implementation(project(":core"))
	implementation(project(":core-loader"))
	implementation(project(":core-client"))
	
	implementation(libs.kord)
	implementation(libs.serialization.core)
	implementation(libs.serialization.yaml)
	implementation(libs.coroutines)
	implementation(kotlin("reflect"))
}

tasks.named<Jar>("jar") {
	manifest.attributes(
		"Mainfest-Version" to "1.0",
		"Main-Class" to "com.lhwdev.discord.bot.core.MainKt",
		"Class-Path" to configurations.runtimeClasspath.get().joinToString(separator = " ") { it.name }
	)
}

tasks.named<JavaExec>("run") {
	mainClass.set("com.lhwdev.discord.bot.core.MainKt")
	workingDir = rootProject.file(".")
}
