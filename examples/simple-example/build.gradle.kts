plugins {
	id("org.jetbrains.kotlin.jvm") version "1.6.10"
	`java-library`
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":core"))

	implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
	//implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks.jar {
	dependsOn(":core:jar")

	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	
	manifest {
		attributes["Main-Class"] = "com.github.mnemotechnician.lettui.examples.simple.MainKt"
	}
	
	from(*configurations.runtimeClasspath.files.map { if (it.isDirectory()) it else zipTree(it) }.toTypedArray())
}

