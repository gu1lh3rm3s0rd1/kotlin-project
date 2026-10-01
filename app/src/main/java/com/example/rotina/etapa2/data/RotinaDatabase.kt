package com.example.rotina.etapa2.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Habito::class], version = 1, exportSchema = false)
abstract class RotinaDatabase : RoomDatabase() {
    abstract fun habitoDao(): HabitoDao
}
