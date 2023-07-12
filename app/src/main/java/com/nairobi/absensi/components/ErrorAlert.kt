package com.nairobi.absensi.components

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog

fun errorAlert(context: Context, title: String = "", text: String = "", action: () -> Unit = {}, actionTitle: String = "", enableAction: Boolean = false, callback: () -> Unit = {}) {
    val alert = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
    alert.titleText = title
    alert.contentText = text
    alert.setConfirmClickListener {
        callback()
        alert.dismissWithAnimation()
    }
    if (enableAction) {
        alert.setCancelButton(actionTitle) {
            action()
            alert.dismissWithAnimation()
        }
    }
    alert.show()
}