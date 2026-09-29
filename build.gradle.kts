import java.net.URI

plugins {
	id("net.fabricmc.fabric-loom")
	`maven-publish`
}

version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("maven_group").get()

base {
	archivesName = providers.gradleProperty("archives_base_name")
}

loom {
	accessWidenerPath = file("src/main/resources/kaleidoscope_cookery.accessWidener")
}

repositories {
	maven {
		name = "Fuzs Mod Resources"
		url = URI("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
	}
	maven { url = URI("https://api.modrinth.com/maven") }
	maven {
		// location of the maven that hosts JEI files since January 2023
		name = "Jared's maven"
		url = URI("https://maven.blamejared.com/")
	}
	maven {
		// location of a maven mirror for JEI files, as a fallback
		name = "ModMaven"
		url = URI("https://modmaven.dev")
	}
	maven {
		name = "Nucleoid"
		url = URI("https://maven.nucleoid.xyz/releases")
	}

	maven { url = URI("https://maven.shedaniel.me") }
}

dependencies {
	testImplementation(platform("org.junit:junit-bom:5.14.2"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
	implementation("maven.modrinth:farmers-delight-refabricated:${providers.gradleProperty("fdrf_version").get()}") {
		exclude(group = "net.fabricmc")
	}
	compileOnly("maven.modrinth:rrv:${providers.gradleProperty("rrv_version").get()}") {
		exclude(group = "net.fabricmc.fabric-api")
		exclude(group = "eu.pb4")
	}
	compileOnly("me.shedaniel:RoughlyEnoughItems-fabric:${providers.gradleProperty("rei_version").get()}")
	compileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${providers.gradleProperty("rei_version").get()}")
	compileOnly ("me.shedaniel.cloth:cloth-config-fabric:26.1.154")
	compileOnly ("dev.architectury:architectury-fabric:20.0.7")
	implementation ("maven.modrinth:create-fly:${providers.gradleProperty("create_version").get()}")
	implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")
	implementation("maven.modrinth:jade:${providers.gradleProperty("jade_version").get()}")
	// Fabric API. This is technically optional, but you probably want it anyway.
	implementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")
	implementation ("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:${providers.gradleProperty("forge_config_api_version").get()}")
	implementation("mezz.jei:jei-${providers.gradleProperty("jei_version").get()}")
	implementation("eu.pb4:trinkets:${providers.gradleProperty("trinkets_version").get()}")
	implementation("maven.modrinth:EsAfCjCV:zLlqqiTA")
}

tasks.test {
	useJUnitPlatform()
}

fabricApi {
	configureTests {
		createSourceSet = true
		modId = "kaleidoscope_cookery_test"
		enableClientGameTests = true
		clearRunDirectory = false
	}
}

tasks.named("runClientGameTest") {
	doFirst {
		// Create Flywheel's worker threads otherwise keep the test JVM alive after shutdown.
		// This is an isolated test directory; normal client configuration is unaffected.
		val flywheelConfig = file("build/run/clientGameTest/config/flywheel-client.json")
		flywheelConfig.parentFile.mkdirs()
		flywheelConfig.writeText("""{"workerThreads":{"value":0}}""")
	}
}

tasks.processResources {
	val version = version
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand("version" to version)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 25
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
	val projectName = project.name
	inputs.property("projectName", projectName)

	from("LICENSE") {
		rename { "${it}_$projectName" }
	}
}

val animationTest by sourceSets.creating {
	compileClasspath += sourceSets.main.get().output + configurations.testCompileClasspath.get()
	runtimeClasspath += sourceSets.main.get().output + configurations.testRuntimeClasspath.get()
}

val testBlockEntityAnimationIsolation by tasks.registering(JavaExec::class) {
	group = "verification"
	description = "Checks shared block entity models for animation pose leakage."
	dependsOn(animationTest.classesTaskName)
	classpath = animationTest.runtimeClasspath
	mainClass.set("com.github.ysbbbbbb.kaleidoscopecookery.client.model.BlockEntityAnimationIsolationTest")
}

tasks.check {
	dependsOn(testBlockEntityAnimationIsolation)
}

// configure the maven publication
publishing {
	publications {
		register<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}
