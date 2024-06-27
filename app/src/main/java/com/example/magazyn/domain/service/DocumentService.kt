package com.example.magazyn.domain.service

import com.example.magazyn.domain.model.Contractor
import com.example.magazyn.domain.model.Document
import com.example.magazyn.domain.model.Item
import kotlinx.coroutines.flow.Flow

interface DocumentService {

    suspend fun addContractor(contractor: Contractor)

    suspend fun updateContractor(uid: String, name: String, symbol: String)

    suspend fun fetchContractors(): Flow<List<Contractor>>

    suspend fun fetchContractor(uid: String): Flow<Contractor?>

    suspend fun addItem(item: Item)

    suspend fun fetchItems(): Flow<List<Item>>

    suspend fun getNextPzNumber(): Int

    suspend fun addDocument(document: Document, uid: String)

    suspend fun fetchDocuments(): Flow<List<Document>>

    suspend fun fetchDocument(uid: String): Flow<Document?>

    suspend fun updateDocument(
        uid: String,
        contractorUid: String?,
        itemList: List<Item>?,
    )

    suspend fun deleteDocument(documentId: String)
}