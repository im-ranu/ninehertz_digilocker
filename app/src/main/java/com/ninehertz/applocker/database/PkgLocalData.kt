package com.ninehertz.applocker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "app_usage")
data class PkgLocalData(

    @PrimaryKey
    @ColumnInfo(name = "pkgName")
    var appPackage: String = "",
    @ColumnInfo(name = "isLocked")
    var locked: Boolean? = null,
    @ColumnInfo(name = "passcode")
    var passcode: String? = "",
    @ColumnInfo(name = "fromTime")
    var fromTime: Long? = 0,
    @ColumnInfo(name = "toTime")
    var toTime: Long? = 0
)