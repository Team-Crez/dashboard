dependencies {
    api(core)
}

// pascal case hello-world -> HelloWorld
val pluginName = rootProject.name.split('-').joinToString("") { it.capitalize() }
// remove dash hello-world -> helloworld
val packageName = rootProject.name.replace("-", "")
// for processResources
extra.set("pluginName", pluginName)
extra.set("packageName", packageName)

tasks {
    // generate plugin.yml
    processResources {
        outputs.upToDateWhen { false }

        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    fun registerPluginJar(name: String, vararg outputs: Project?, configuration: Jar.() -> Unit) = register<Jar>(name) {
        // spigot 의 업데이트 기능을 사용하기 위해 버전을 제거하고 플러그인 이름만 사용
        archiveBaseName.set(pluginName)
        archiveVersion.set("")

        outputs.filterNotNull().forEach { project ->
            from(project.sourceSets["main"].output)
        }

        configuration()

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug-server/plugins/")
                // .debug-server/plugin 폴더로 복사, 파일이 이미 있을 경우 .debug-server/plugin/update 폴더로 복사
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }

    // jar 안에 의존하는 프로젝트를 포함 (standalone)
    registerPluginJar("pluginJar", api, core, project) {
        outputs.upToDateWhen { false }

        findProject(":${rootProject.name}-dongle")?.let {
            val dongleJar = dongle.tasks.jar

            dependsOn(dongleJar)
            from(zipTree(dongleJar.get().archiveFile))
        }

        exclude("test.yml")
    }

    // 라이브러리 테스트용
    // publish 프로젝트가 있을 때 생성됨
    // 의존하는 프로젝트를 jar 에 포함키지 않고 library 폴더에 배포
    findProject(":${rootProject.name}-dongle")?.let {
        registerPluginJar("testPluginJar", project) {
            exclude("plugin.yml")
            rename("test-plugin.yml", "plugin.yml")

            pub.tasks.let { tasks ->
                dependsOn(tasks.named("publishApiPublicationToDebugRepository"))
                dependsOn(tasks.named("publishCorePublicationToDebugRepository"))
            }
        }
    }

}
