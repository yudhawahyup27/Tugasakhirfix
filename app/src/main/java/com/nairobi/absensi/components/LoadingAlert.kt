package com.nairobi.absensi.components

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog

fun loadingAlert(context: Context): SweetAlertDialog {
    val loading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
    loading.show()
    loading.setCancelable(false)
    loading.setCanceledOnTouchOutside(false)
    return loading
}