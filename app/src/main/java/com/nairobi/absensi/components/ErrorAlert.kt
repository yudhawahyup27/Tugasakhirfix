package com.nairobi.absensi.components

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog

fun errorAlert(context: Context, title: String = "", text: String = "", callback: () -> Unit = {}) {
    val alert = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
    alert.titleText = title
    alert.contentText = text
    alert.setConfirmClickListener {
        callback()
        alert.dismissWithAnimation()
    }
    alert.show()
}