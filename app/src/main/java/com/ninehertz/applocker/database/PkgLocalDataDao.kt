package com.ninehertz.applocker.database

import androidx.room.*


@Dao
interface PkgLocalDataDao {
    @Insert
    fun insert(vararg pkgLocalData: PkgLocalData)

    @Update
    fun update(vararg pkgLocalData: PkgLocalData)

    @Delete
    fun delete(pkgLocalData: PkgLocalData)

    @Query("SELECT * FROM app_usage WHERE pkgName=:pkgName")
    fun getAppUsages(pkgName : String): PkgLocalData


}