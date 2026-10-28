plugins {
    id("com.android.application") version "8.6.1"
    id("org.jetbrains.kotlin.android") version "2.0.21"
}

android {
    namespace = "com.fonlisstars.game"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fonlisstars.game"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
