package com.ninehertz.applocker.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = arrayOf(PkgLocalData::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getPkgDao() : PkgLocalDataDao
}