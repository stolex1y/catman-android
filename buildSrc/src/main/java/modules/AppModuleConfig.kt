package modules

import org.gradle.api.JavaVersion

object AppModuleConfig : BaseModuleConfig() {
    override val namespace = "ru.stolexiy.catman"
    override val versionCode = 1
    override val versionName = "1.0.0"
}