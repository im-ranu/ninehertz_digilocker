package com.ninehertz.applocker.viewmodel

import android.app.Application
import android.content.pm.PackageInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ninehertz.applocker.repository.PackageRepository


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var pkgListObservable: LiveData<List<PackageInfo?>>? = null

    init {
        pkgListObservable = PackageRepository.getInstance().getPackageList(application)
    }

    fun getPackageListObservable(): LiveData<List<PackageInfo?>>? {
        return pkgListObservable
    }
}