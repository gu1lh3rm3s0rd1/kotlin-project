package com.example.rotina.etapa2

import android.app.Application
import androidx.room.Room
import com.example.rotina.etapa2.data.HabitoRepository
import com.example.rotina.etapa2.data.RotinaDatabase

/**
 * A Application existe uma única vez enquanto o app está aberto.
 * Por isso é um bom lugar para criar o banco e o Repository uma vez só
 * e reaproveitá-los em todas as telas.
 */
class RotinaApplication : Application() {

    private val database by lazy {
        Room.databaseBuilder(this, RotinaDatabase::class.java, "rotina.db").build()
    }

    val repository by lazy { HabitoRepository(database.habitoDao()) }
}
