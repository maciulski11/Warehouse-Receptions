package com.example.magazyn.presentation.screen.Contractor

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magazyn.domain.model.Contractor
import com.example.magazyn.domain.service.DocumentService
import kotlinx.coroutines.launch

class ContractorsViewModel(private val documentService: DocumentService) : ViewModel() {

    val contractorList: MutableState<List<Contractor>> = mutableStateOf(emptyList())

    init {
        fetchContractors()
    }

    fun addContractor(name: String, symbol: String) {
        val data = Contractor(
            name = name,
            symbol = symbol
        )
        viewModelScope.launch {
            documentService.addContractor(data)
        }
    }

    private fun fetchContractors() {
        viewModelScope.launch {
            documentService.fetchContractors()
                .collect { contractor ->
                    contractorList.value = contractor
                }
        }
    }

    fun updateContractor(uid: String, name: String, symbol: String) {
        viewModelScope.launch {
            try {
                documentService.updateContractor(uid, name, symbol)
            } catch (e: Exception) {
                Log.e("ContractorViewModel", "Error updating document", e)
            }
        }
    }
}