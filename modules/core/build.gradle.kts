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
	
	implementation(libs.kord)
	implementation(libs.serialization.core)
	implementation(libs.serialization.yaml)
	implementation(libs.coroutines)
}
