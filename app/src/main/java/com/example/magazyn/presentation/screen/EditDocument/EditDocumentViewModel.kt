package com.example.magazyn.presentation.screen.EditDocument

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magazyn.domain.model.Contractor
import com.example.magazyn.domain.model.Document
import com.example.magazyn.domain.model.Item
import com.example.magazyn.domain.service.DocumentService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditDocumentViewModel(
    private val documentService: DocumentService,
    private val uid: String,
    private val contractorUid: String,
) :
    ViewModel() {

    private val _document = MutableStateFlow<Document?>(null)
    val document: StateFlow<Document?> = _document

    private val _contractor = MutableStateFlow<Contractor?>(null)
    val contractor: StateFlow<Contractor?> = _contractor

    init {
        fetchDocument()
        fetchContractor()
    }

    private fun fetchDocument() {
        viewModelScope.launch {
            documentService.fetchDocument(uid).collect { document ->
                _document.value = document
            }
        }
    }

    private fun fetchContractor() {
        viewModelScope.launch {
            documentService.fetchContractor(contractorUid).collect { contractor ->
                _contractor.value = contractor
            }
        }
    }

    fun updateDocument(contractorUid: String?, itemList: List<Item>?) {
        viewModelScope.launch {
            try {
                documentService.updateDocument(uid, contractorUid, itemList)
            } catch (e: Exception) {
                Log.e("EditViewModel", "Error updating document", e)
            }
        }
    }
}