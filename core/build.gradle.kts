plugins {
	id("org.jetbrains.kotlin.jvm") version "1.6.10"
	`java-library`
}

repositories {
	mavenCentral()
}

dependencies {
	compileOnly(platform("org.jetbrains.kotlin:kotlin-bom"))
	compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
	compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}


tasks.jar {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	
	from(*configurations.runtimeClasspath.files.map { if (it.isDirectory()) it else zipTree(it) }.toTypedArray())
}


/*
testing {
	suites {
		// Configure the built-in test suite
		val test by getting(JvmTestSuite::class) {
			// Use Kotlin Test test framework
			useKotlinTest()
		}
	}
}
*/
