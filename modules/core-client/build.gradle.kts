import com.lhwdev.build.*


plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	
	id("common-plugin")
}

kotlin {
	setup()
}


dependencies {
	implementation(project(":core-loader"))
	
	implementation(libs.serialization.core)
	implementation(libs.serialization.json)
	implementation(libs.coroutines)
}
