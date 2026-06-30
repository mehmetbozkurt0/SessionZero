package com.sessionzero.sessionzero

import androidx.compose.ui.window.ComposeUIViewController
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.sessionzero.sessionzero.data.character.CharacterRepositoryImpl
import com.sessionzero.sessionzero.db.SessionZeroDb

fun MainViewController() = ComposeUIViewController {
    val driver = NativeSqliteDriver(
        schema = SessionZeroDb.Schema,
        name = "sessionzero.db",
    )
    val database = SessionZeroDb(driver)
    val characterRepository = CharacterRepositoryImpl(database)

    App(characterRepository = characterRepository)
}
