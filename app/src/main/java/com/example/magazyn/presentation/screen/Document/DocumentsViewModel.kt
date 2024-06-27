package com.example.magazyn.presentation.screen.Document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magazyn.domain.model.Document
import com.example.magazyn.domain.service.DocumentService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DocumentsViewModel(private val firebaseService: DocumentService) : ViewModel() {

    private val _documentList = MutableStateFlow<List<Document>>(emptyList())
    val documentList: StateFlow<List<Document>> = _documentList

    init {
        fetchDocuments()
    }

    private fun fetchDocuments() {
        viewModelScope.launch {
            firebaseService.fetchDocuments()
                .collect { documents ->
                    _documentList.value = documents.sortedBy { parsePzNumber(it.number) }
                }
        }
    }

    private fun parsePzNumber(number: String?): Int {
        if (number != null && number.startsWith("PZ ")) {
            val numberStr =
                number.substringAfter("PZ ").substringBefore("/") // Pobierz liczbę po "PZ "
            return numberStr.toIntOrNull()
                ?: Int.MAX_VALUE
        }
        return Int.MAX_VALUE
    }

    fun deleteDocument(uid: String) {
        viewModelScope.launch {
            firebaseService.deleteDocument(uid)
            fetchDocuments()
        }
    }
}
