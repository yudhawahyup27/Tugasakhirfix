package com.nairobi.absensi.components

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog

fun successAlert(
    context: Context,
    title: String = "",
    text: String = "",
    action: () -> Unit = {},
    actionTitle: String = "OK",
    showAction: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    val alert = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
    alert.titleText = title
    alert.contentText = text
    alert.setConfirmClickListener {
        onDismiss()
        it.dismissWithAnimation()
    }
    if (showAction) {
        alert.setConfirmText(actionTitle)
        alert.setConfirmClickListener {
            action()
            it.dismissWithAnimation()
        }
    }
    alert.show()
}