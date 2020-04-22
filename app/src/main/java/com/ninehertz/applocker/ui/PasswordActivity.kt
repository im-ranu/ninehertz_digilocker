package com.ninehertz.applocker.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ninehertz.applocker.R
import kotlinx.android.synthetic.main.activity_password.*

class PasswordActivity : AppCompatActivity() {


    override fun onStart() {
        super.onStart()
        val TAG = PasswordActivity::class.java.simpleName
        Log.d(TAG,"PasswordActivity")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)


        btUnlocked.setOnClickListener {
            finish()
        }
    }


}