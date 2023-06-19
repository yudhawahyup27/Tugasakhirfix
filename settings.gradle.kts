pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox"
                password =
                    "sk.eyJ1Ijoic2NyaXB0c29yY2VyZXIiLCJhIjoiY2xpOXpyaTRiMmx5NDNybzN0ZGk2Y3d6ayJ9.uvAqkmBdRVRHZUbOpH6zWw"
            }
        }
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "Absensi"
include(":app")
