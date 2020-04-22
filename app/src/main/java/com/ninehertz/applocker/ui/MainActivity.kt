package com.ninehertz.applocker.ui

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.mukesh.OnOtpCompletionListener
import com.mukesh.OtpView
import com.ninehertz.applocker.R
import com.ninehertz.applocker.adapter.PackageListAdapter
import com.ninehertz.applocker.constant.Constants
import com.ninehertz.applocker.database.AppDatabase
import com.ninehertz.applocker.database.PkgLocalData
import com.ninehertz.applocker.services.AppLockService
import com.ninehertz.applocker.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import com.ninehertz.applocker.extension.confirmedPermissionAlert


class MainActivity : AppCompatActivity() {

    val data = ArrayList<PkgLocalData>()
    val TAG = MainActivity::class.java.simpleName
    lateinit var adapter : PackageListAdapter
    var mBoundService: AppLockService? = null
    var mServiceBound = false


    lateinit var viewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpToolbar()
        val checkUsagePermission = checkUsagePermission()

        if (!checkUsagePermission) {
            getUsageStatsPermissionsStatus(this,true)
        }else{
            val database = setDatabase()
            initViews(database)

            getInstalledPackageData(viewModel,database)
            setPackageData()
        }



    }

    private fun checkUsagePermission(): Boolean {
        return try {
            val packageManager: PackageManager = this.getPackageManager()
            val applicationInfo =
                packageManager.getApplicationInfo(this.getPackageName(), 0)
            val appOpsManager =
                this.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                applicationInfo.uid,
                applicationInfo.packageName
            )
            mode == AppOpsManager.MODE_ALLOWED
        }catch (e: PackageManager.NameNotFoundException){
            false
        }

    }

    private fun setDatabase(): AppDatabase {
        var db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "pkgLockerData"
        ).build()

        return db

    }

    private fun getUsageStatsPermissionsStatus(context: Context,granted : Boolean){

        Log.d(TAG,"$granted")
        if (!granted){
            if (VERSION.SDK_INT >= 23) {
                val mUsageStatsManager =
                    context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val time = System.currentTimeMillis()
                val stats: List<*>? = mUsageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 10,
                    time
                )
                if (stats == null || stats.isEmpty()) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_USAGE_ACCESS_SETTINGS
                    context.startActivity(intent)
                }
            }
        }else{
            Toast.makeText(context,getString(R.string.failure_msg),Toast.LENGTH_LONG).show()
        }


    }




    override fun onStart() {
        super.onStart()
        AppLockService().start(baseContext)
    }

    @Synchronized
    private fun getInstalledPackageData(
        viewModel: MainActivityViewModel,
        database: AppDatabase
    ) {
        viewModel.getPackageListObservable()?.observe(this,object : Observer<List<PackageInfo?>>{
            override fun onChanged(t: List<PackageInfo?>?) {
                t?.forEach {
                    if (it?.applicationInfo?.flags!! and ApplicationInfo.FLAG_SYSTEM == 0) {
                        val appName =
                            it.applicationInfo?.loadLabel(this@MainActivity.packageManager).toString()

                        val thread = Thread {
                            val pkgEntity = PkgLocalData()
                            val pkgData = database.getPkgDao().getAppUsages(appName)
                            try {
                                if (pkgData==null){
                                    pkgEntity.appPackage = appName
                                    pkgEntity.locked = false
                                    pkgEntity.passcode = ""
                                    pkgEntity.fromTime = 0
                                    pkgEntity.toTime = 0
                                   database.getPkgDao().insert(pkgEntity)
                                }else{
                                    Log.d(TAG,
                                            "already Inserted ${pkgData.passcode}")
                                }
                                data.add(pkgData)

                            }catch (e: Exception){

                            }

                        }
                        thread.start()

                    }
                }
            }
        })

    }

    private fun setPackageData() {
        adapter.notifyDataSetChanged()
    }

    private fun initViews(database: AppDatabase) {
        adapter = PackageListAdapter(this,data,database = database)
        recyclerViewPackageList.adapter = adapter
         viewModel = ViewModelProviders.of(this).get(
            MainActivityViewModel::class.java
        )

    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }

}
