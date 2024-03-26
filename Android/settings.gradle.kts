pluginManagement {
    repositories {
        maven(url = "https://repo.eclipse.org/content/repositories/paho-snapshots/")
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Iot"
include(":app")
