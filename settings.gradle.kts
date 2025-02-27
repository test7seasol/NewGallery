pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
        maven("https://artifactory.img.ly/artifactory/imgly") // IMGLY repository
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://artifactory-external.vkpartner.ru/artifactory/maven") }
        maven("https://artifactory.img.ly/artifactory/imgly")
        maven("https://maven.wysaid.org/")
    }
}

rootProject.name = "Gallery Fenil"
include(":app")
//include(":patternlockview")
