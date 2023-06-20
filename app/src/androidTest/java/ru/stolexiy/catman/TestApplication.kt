package ru.stolexiy.catman

import dagger.hilt.android.testing.CustomTestApplication
import ru.stolexiy.catman.application.BaseApplication

@CustomTestApplication(BaseApplication::class)
interface TestApplication
