package com.nairobi.absensi.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nairobi.absensi.data.Office
import com.nairobi.absensi.repo.OfficeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfficeViewModel @Inject constructor(
        private val officeRepository: OfficeRepository
) : ViewModel() {
    val office: MutableState<Office?> = mutableStateOf(null)
    val error: MutableState<String?> = mutableStateOf(null)
    val loading: MutableState<Boolean> = mutableStateOf(false)

    fun getOffice(onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = officeRepository.getOffice()
            office.value = result
            error.value = null
            loading.value = false
            onFinish()
        }
    }

    fun setOffice(office: Office, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            officeRepository.setOffice(office)
            error.value = null
            loading.value = false
            onFinish()
        }
    }
}