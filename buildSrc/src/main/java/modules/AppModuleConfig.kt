package modules

object AppModuleConfig : BaseModuleConfig() {
    override val namespace = "ru.stolexiy.catman"
    override val versionCode = 1
    override val versionName = "1.0.0"
    override val testInstrumentationRunner: String = "ru.stolexiy.catman.AndroidJUnitRunnerWithHilt"
}