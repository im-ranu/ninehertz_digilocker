package com.ninehertz.applocker.services

import android.app.ActivityManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.ninehertz.applocker.ui.MainActivity
import com.ninehertz.applocker.ui.PasswordActivity
import java.util.*


class CheckAppLauncherThread(mainHandler : Handler, context : Context)  : Thread() {

    private var context: Context? = null
    private var handler: Handler? = null
    private var actMan: ActivityManager? = null
    private val timer = 100
    val TAG = "App Thread"
    var lastUnlocked: String? = null


        init {
            this.context = context
            handler = mainHandler
            actMan = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            this.priority = MAX_PRIORITY
        }


    override fun run() {
//        context!!.startService(Intent(context, AppLockService::class.java))
        Looper.prepare()
        var prevTasks: String
        var recentTasks = ""
        prevTasks = recentTasks
        Log.d("Thread", "Inside Thread")
        while (true) {
            try {
                var topPackageName = ""
                if (Build.VERSION.SDK_INT >= 23) {
                    val mUsageStatsManager =
                        context!!.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                    val time = System.currentTimeMillis()
                    // We get usage stats for the last 10 seconds
                    val stats =
                        mUsageStatsManager.queryUsageStats(
                            UsageStatsManager.INTERVAL_DAILY,
                            time - 1000 * 5,
                            time
                        )

                    if (stats != null) {
                        val mySortedMap: SortedMap<Long, UsageStats> =
                            TreeMap<Long, UsageStats>()
                        for (usageStats in stats) {
                            mySortedMap.put(usageStats.lastTimeUsed, usageStats)
                        }

                        if (!mySortedMap.isNullOrEmpty() && !mySortedMap.isEmpty()) {
                            topPackageName = mySortedMap[mySortedMap.lastKey()]!!.packageName
                        }
                    }
                } else {
                    topPackageName = actMan!!.runningAppProcesses[0].processName
                }
                recentTasks = topPackageName
                sleep(timer.toLong())
                if (recentTasks.isEmpty() || recentTasks ==
                    prevTasks
                ) {
                } else {
                    if (isAppLocked(recentTasks)) {
                        Log.d(TAG, "Locked $recentTasks")
                        handler!!.post(RequestPassword(context!!, recentTasks))

                    }
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            prevTasks = recentTasks
        }

    }

    inner class ToastRunnable(var message: String) : Runnable {
        override fun run() {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

    }

    inner class RequestPassword(
        private val mContext: Context,
        private val pkgName: String
    ) :
        Runnable {
        override fun run() {
            Log.d(TAG, "RunningPasswordActivity")
            Log.d(TAG, mContext.packageName.toString())
//            val passwordAct = Intent(mContext, PasswordActivity::class.java)

//            if (!mContext.packageName.equals("com.ninehertz.applocker")){
                val i = Intent(mContext, PasswordActivity::class.java)
                i.putExtra("package", pkgName)
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mContext.startActivity(i)
//            }

            Toast.makeText(mContext, pkgName, Toast.LENGTH_LONG).show()


        }
    }

        private fun isAppLocked(packageName: String): Boolean {
            //todo : need to check whether packagename is locked or not from database
            return true
        }


}