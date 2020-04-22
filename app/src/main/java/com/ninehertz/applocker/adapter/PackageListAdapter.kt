package com.ninehertz.applocker.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mukesh.OnOtpCompletionListener
import com.mukesh.OtpView
import com.ninehertz.applocker.R
import com.ninehertz.applocker.database.AppDatabase
import com.ninehertz.applocker.database.PkgLocalData
import kotlinx.android.synthetic.main.itemview_packagelist.view.*


class PackageListAdapter(val context : Context,private val packageList : ArrayList<PkgLocalData>?,var database: AppDatabase) : RecyclerView.Adapter<PackageListAdapter.ViewHolder>() {


    val TAG = PackageListAdapter::class.java.simpleName
   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var txPackageName : TextView = itemView.txPackageName
         var switchIsLocked : Switch = itemView.switchIsLocked

        fun bindData(pkgLocalData: PkgLocalData ) {
           txPackageName.text = pkgLocalData.appPackage
            if (pkgLocalData.locked != null) {
                    switchIsLocked.isChecked = pkgLocalData.locked!!
                }

        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PackageListAdapter.ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.itemview_packagelist,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return packageList?.size ?:0
    }

    override fun onBindViewHolder(holder: PackageListAdapter.ViewHolder, position: Int) {
        packageList?.get(position)?.let { holder.bindData(it) }
        holder.switchIsLocked.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

                if (isChecked){
                     showAlertBox()

                }
                val thread = Thread{
                    val pkgLocalData = PkgLocalData()
                    pkgLocalData.appPackage = packageList?.get(position)!!.appPackage
                    pkgLocalData.locked = isChecked
                    database.getPkgDao().update(pkgLocalData)

                }
                thread.start()

            }

        })
    }

    private fun showAlertBox()  {


        val alertDialog =  AlertDialog.Builder(context);
        val inflater: LayoutInflater = (context as AppCompatActivity).layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialogbox_lockerpin,null)
        val otpView = dialogView.findViewById<OtpView>(R.id.otp_view)
        otpView.setOtpCompletionListener { otp ->

        }

        alertDialog.setView(dialogView)
        alertDialog.setTitle("Set DigiPass Code");
        alertDialog.setMessage("Select time range");
        alertDialog.setNegativeButton("Dismiss"
        ) { dialog, which -> dialog?.dismiss() }
        alertDialog.setPositiveButton("Confirmed"
        ) { dialog, which -> dialog?.dismiss() }
        val dialog = alertDialog.create();
        dialog.show();


    }
}