package com.ninehertz.applocker.extension

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ninehertz.applocker.interfaces.PermissionInterface
import com.ninehertz.applocker.R

fun confirmedPermissionAlert(context: Context,permissionInterface: PermissionInterface) {
    var granted : Boolean? = false
    val alertDialog =  AlertDialog.Builder(context);
    val inflater: LayoutInflater = (context as AppCompatActivity).layoutInflater
    val dialogView: View = inflater.inflate(R.layout.dialogbox_permissionalert,null)
    alertDialog.setView(dialogView)
    Log.d("TAG","DialogBox")
    alertDialog.setNegativeButton("Dismiss"
    ) { dialog, which ->
        granted = false
        permissionInterface.onGranted(granted!!)
        dialog?.dismiss() }
    alertDialog.setPositiveButton("Confirmed"
    ) { dialog, which ->
        granted = true
        permissionInterface.onGranted(granted!!)
        dialog?.dismiss()

    }
    val dialog = alertDialog.create();
    dialog.show()
}