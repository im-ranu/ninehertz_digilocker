package com.ninehertz.applocker.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class PackageRepository {

    companion object{
        fun getInstance() : PackageRepository{
            return PackageRepository()
        }
    }

    fun getPackageList(context: Context): LiveData<List<PackageInfo?>>? {
        val data: MutableLiveData<List<PackageInfo?>> =
            MutableLiveData<List<PackageInfo?>>()
        val packList =
            context.packageManager.getInstalledPackages(0)
        data.value = packList
        return data
    }
}