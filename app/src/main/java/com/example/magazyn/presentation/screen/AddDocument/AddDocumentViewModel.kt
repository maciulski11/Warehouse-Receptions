package com.example.magazyn.presentation.screen.AddDocument

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magazyn.domain.model.Contractor
import com.example.magazyn.domain.model.Document
import com.example.magazyn.domain.model.Item
import com.example.magazyn.domain.service.DocumentService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class AddDocumentViewModel(private val documentService: DocumentService) : ViewModel() {

    val itemList: MutableState<List<Item>> = mutableStateOf(emptyList())

    private val _contractor = MutableStateFlow<Contractor?>(null)
    val contractor: StateFlow<Contractor?> = _contractor

    private val _nextPzNumber = MutableStateFlow(1)
    private val nextPzNumber: StateFlow<Int> = _nextPzNumber.asStateFlow()

    private val currentDate = LocalDateTime.now()
    private val formattedPZDate = currentDate.format(DateTimeFormatter.ofPattern("MM/yyyy"))
    private val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    init {
        fetchItems()
    }

    fun addItem(name: String, unit: String, amount: String) {

        val data = Item(
            name = name,
            unit = unit,
            amount = amount
        )

        viewModelScope.launch {
            documentService.addItem(data)
        }
    }

    private fun fetchItems() {
        viewModelScope.launch {
            documentService.fetchItems()
                .collect { item ->
                    itemList.value = item
                }
        }
    }

    fun addDocument(uidContractor: String, itemList: List<Item>) {
        val uid = UUID.randomUUID().toString()

        viewModelScope.launch {
            try {
                // Download the current PZ number
                val nextNumber = documentService.getNextPzNumber()
                _nextPzNumber.value = nextNumber + 1

                // Create a document using the current PZ number
                val document = createDocument(uidContractor, itemList, uid)

                documentService.addDocument(document, uid)
            } catch (e: Exception) {
                Log.e(TAG, "Error adding document", e)
            }
        }
    }

    private fun createDocument(
        uidContractor: String,
        itemList: List<Item>,
        uid: String,
    ): Document {
        val items = itemList.map { item ->
            Item(name = item.name, unit = item.unit, amount = item.amount)
        }

        return Document(
            date = formattedDate,
            number = "PZ ${nextPzNumber.value}/$formattedPZDate",
            uidContractor,
            item = items,
            uid = uid
        )
    }
}