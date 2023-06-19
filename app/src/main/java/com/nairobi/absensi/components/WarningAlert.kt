package com.nairobi.absensi.components

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog

fun warningAlert(
    context: Context,
    title: String = "Peringatan",
    text: String = "Apakah anda yakin?",
    positiveText: String = "Ya",
    negativeText: String = "Tidak",
    positiveAction: () -> Unit = {},
    negativeAction: () -> Unit = {},
) {
    val alert = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
    alert.titleText = title
    alert.contentText = text
    alert.confirmText = positiveText
    alert.cancelText = negativeText
    alert.setConfirmClickListener {
        positiveAction()
        alert.dismissWithAnimation()
    }
    alert.setCancelClickListener {
        negativeAction()
        alert.dismissWithAnimation()
    }
    alert.show()
}