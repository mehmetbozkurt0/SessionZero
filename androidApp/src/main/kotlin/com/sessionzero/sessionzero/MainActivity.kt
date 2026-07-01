package com.sessionzero.sessionzero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.sessionzero.sessionzero.data.ai.AiRepositoryImpl
import com.sessionzero.sessionzero.data.character.CharacterRepositoryImpl
import com.sessionzero.sessionzero.db.SessionZeroDb

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val driver = AndroidSqliteDriver(
            schema = SessionZeroDb.Schema,
            context = this,
            name = "sessionzero.db",
        )
        val database = SessionZeroDb(driver)
        val characterRepository = CharacterRepositoryImpl(database)
        val aiRepository = AiRepositoryImpl()

        setContent {
            App(
                characterRepository = characterRepository,
                aiRepository = aiRepository,
            )
        }
    }
}
