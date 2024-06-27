package com.example.magazyn.data

import android.util.Log
import com.example.magazyn.domain.model.Contractor
import com.example.magazyn.domain.model.Document
import com.example.magazyn.domain.model.Item
import com.google.firebase.firestore.FirebaseFirestore
import com.example.magazyn.domain.service.DocumentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseService(private val firestore: FirebaseFirestore) : DocumentService  {

    companion object {
        const val FIREBASE_SERVICE = "FirebaseService"
        const val CONTRACTOR = "contractor"
        const val ITEM = "item"
        const val DOCUMENT = "document"
    }

    /**
     * Adds a new contractor to the Firestore database if it does not already exist.
     * Checks the existence of a contractor by querying the Firestore collection for
     * documents with matching 'name' field. If no match is found, generates a new UID,
     * creates a new Contractor object, and adds it to the collection.
     *
     * @param contractor The Contractor object containing name and symbol to be added.
     */
    override suspend fun addContractor(contractor: Contractor) {
        firestore.collection(CONTRACTOR)
            .whereEqualTo("name", contractor.name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    val uid = UUID.randomUUID().toString()
                    val data = Contractor(
                        name = contractor.name,
                        symbol = contractor.symbol,
                        uid = uid
                    )

                    firestore.collection(CONTRACTOR).document(uid)
                        .set(data)
                        .addOnSuccessListener {
                            Log.d(FIREBASE_SERVICE, "Contractor added successfully: $contractor")
                        }
                        .addOnFailureListener { e ->
                            Log.w(FIREBASE_SERVICE, "Error adding contractor", e)
                        }
                } else {
                    Log.d(FIREBASE_SERVICE, "Contractor already exists: $contractor")
                }
            }
            .addOnFailureListener { e ->
                Log.w(FIREBASE_SERVICE, "Error checking contractor existence", e)
            }
    }

    /**
     * Updates the details of an existing contractor in the Firestore database.
     * Modifies the 'name' and 'symbol' fields of the contractor document identified by the given UID.
     *
     * @param uid The unique identifier of the contractor document to update.
     * @param name The updated name of the contractor.
     * @param symbol The updated symbol of the contractor.
     */
    override suspend fun updateContractor(uid: String, name: String, symbol: String) {
        try {
            val updateData = mutableMapOf<String, Any?>()

            updateData["name"] = name
            updateData["symbol"] = symbol

            firestore.collection(CONTRACTOR)
                .document(uid)
                .update(updateData)
                .await()

            Log.d("FirebaseService", "Contractor document successfully updated: $uid")

        } catch (e: Exception) {
            Log.e(FIREBASE_SERVICE, "Error updating document", e)
            throw e
        }
    }

    /**
     * Fetches the list of contractors from Firestore and returns them as a flow of lists.
     * The flow emits updated lists whenever there are changes in the Firestore collection.
     */
    override suspend fun fetchContractors(): Flow<List<Contractor>> {
        return callbackFlow {
            val listenerRegistration = firestore.collection(CONTRACTOR)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val tempList = mutableListOf<Contractor>()
                    for (document in querySnapshot!!) {
                        val box = document.toObject(Contractor::class.java)
                        tempList.add(box)
                    }
                    trySend(tempList).isSuccess
                }

            awaitClose {
                listenerRegistration.remove()
                Log.d(FIREBASE_SERVICE, "Listener for fetching contractors closed")
            }
        }
    }

    /**
     * Fetches a flow of Contractor data from Firestore based on the provided UID.
     * Uses callbackFlow to listen for changes in the CONTRACTOR collection.
     *
     * @param uid String UID of the contractor to fetch.
     */
    override suspend fun fetchContractor(uid: String): Flow<Contractor?> {
        return callbackFlow {
            val docRef = firestore.collection(CONTRACTOR).document(uid)
            val subscription = docRef.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val contractor = snapshot?.toObject(Contractor::class.java)
                trySend(contractor).isSuccess
            }
            awaitClose {
                subscription.remove()
                Log.d(FIREBASE_SERVICE, "Listener for fetching contractor closed")
            }
        }
    }

    /**
     * Adds an item to the Firestore collection named 'ITEM' if it does not already exist.
     * The item's uniqueness is checked based on its name.
     *
     * @param item The item object to be added.
     */
    override suspend fun addItem(item: Item) {
        firestore.collection(ITEM)
            .whereEqualTo("name", item.name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // Jeśli nie ma już takiego przedmiotu, dodaj nowy
                    firestore.collection(ITEM)
                        .document()
                        .set(item.copy(name = item.name)) // Ustaw nowy identyfikator
                        .addOnSuccessListener {
                            Log.d(FIREBASE_SERVICE, "Item added successfully: $item")
                        }
                        .addOnFailureListener { e ->
                            Log.w(FIREBASE_SERVICE, "Error adding item", e)
                        }
                } else {
                    Log.d(FIREBASE_SERVICE, "Item already exists: $item")
                }
            }
            .addOnFailureListener { e ->
                Log.e(FIREBASE_SERVICE, "Error checking item existence", e)
            }
    }

    /**
     * Fetches a list of items from the Firestore collection named 'ITEM'.
     * The function returns a flow that emits updates whenever the collection changes.
     */
    override suspend fun fetchItems(): Flow<List<Item>> {
        return callbackFlow {
            val listenerRegistration = firestore.collection(ITEM)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val tempList = mutableListOf<Item>()
                    for (document in querySnapshot!!) {
                        val item = document.toObject(Item::class.java)
                        tempList.add(item)
                    }
                    trySend(tempList).isSuccess
                }

            awaitClose {
                listenerRegistration.remove()
                Log.d(FIREBASE_SERVICE, "Listener for fetching items closed")
            }
        }
    }

    /**
     * Retrieves the next available PZ number from the Firestore collection named 'DOCUMENT'.
     * This function calculates the highest PZ number currently in use and returns the next sequential number.
     *
     * @return The next available PZ number as an integer.
     */
    override suspend fun getNextPzNumber(): Int {
        return try {
            val querySnapshot = firestore.collection(DOCUMENT)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val numbers = querySnapshot.documents.mapNotNull { document ->
                    val numberStr = document.getString("number") ?: return@mapNotNull null
                    val number = numberStr.substringAfter("PZ ").substringBefore("/").trim()
                    number.toIntOrNull()
                }
                if (numbers.isNotEmpty()) {
                    numbers.maxOrNull() ?: 0
                } else {
                    0
                }
            } else {
                0
            }
        } catch (e: Exception) {
            Log.e(FIREBASE_SERVICE, "Error fetching last PZ number", e)
            0
        }
    }

    /**
     * Adds a new document to the Firestore collection named 'DOCUMENT'.
     * This function sets the document using the provided UID as its identifier.
     *
     * @param document The document data to be added to Firestore.
     * @param uid The unique identifier (UID) for the document.
     */
    override suspend fun addDocument(document: Document, uid: String) {
        try {
            firestore.collection(DOCUMENT)
                .document(uid)
                .set(document)
                .await()

            Log.d("FirebaseService", "Successfully add document: $document")

        } catch (e: Exception) {
            Log.e(FIREBASE_SERVICE, "Error adding document", e)
        }
    }

    /**
     * Fetches a flow of documents from Firestore along with their associated contractors.
     * Uses callbackFlow to listen for changes in the DOCUMENT collection and fetches contractors
     * from the CONTRACTOR collection based on the contractorUid present in each document.
     */
    override suspend fun fetchDocuments(): Flow<List<Document>> {
        return callbackFlow {
            val docRef = firestore.collection(DOCUMENT)
            val subscription = docRef.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                launch(Dispatchers.IO) {
                    val documents = mutableListOf<Document>()
                    snapshot?.documents?.forEach { docSnapshot ->
                        val document = docSnapshot.toObject(Document::class.java)
                        document?.let { doc ->
                            val contractorUid = doc.contractorUid
                            if (contractorUid != null) {
                                try {
                                    val contractorSnapshot = firestore.collection(CONTRACTOR)
                                        .document(contractorUid).get().await()
                                    val updatedContractor = contractorSnapshot.toObject(Contractor::class.java)
                                    doc.contractor = updatedContractor
                                } catch (e: Exception) {
                                    Log.e(FIREBASE_SERVICE, "Error fetching contractor data", e)
                                }
                            }
                            documents.add(doc)
                        }
                    }
                    trySend(documents.toList()).isSuccess
                }
            }
            awaitClose {
                subscription.remove()
                Log.d(FIREBASE_SERVICE, "Listener for fetching documents closed")
            }
        }
    }

    /**
     * Fetches a single document from the Firestore collection 'DOCUMENT' based on the provided UID.
     * This function returns a flow that emits the document when it is fetched or updated in Firestore.
     *
     * @param uid String The unique identifier of the document to fetch.
     */
    override suspend fun fetchDocument(uid: String): Flow<Document?> {
        return callbackFlow {
            val docRef = firestore.collection(DOCUMENT).document(uid)
            val subscription = docRef.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val document = snapshot?.toObject(Document::class.java)
                trySend(document).isSuccess
            }
            awaitClose {
                subscription.remove()
                Log.d(FIREBASE_SERVICE, "Listener for fetching document closed")
            }
        }
    }

    /**
     * Updates a document in the Firestore collection 'DOCUMENT' with the provided UID.
     * This function updates specific fields such as contractor name, contractor symbol,
     * and item list if provided, ensuring atomicity of updates.
     *
     * @param uid String The unique identifier of the document to update.
     * @param contractorName String? The updated contractor name. Pass null to skip update.
     * @param contractorSymbol String? The updated contractor symbol. Pass null to skip update.
     * @param itemList List<Item>? The updated list of items. Pass null to skip update.
     */
    override suspend fun updateDocument(
        uid: String,
        contractorUid: String?,
        itemList: List<Item>?,
    ) {
        try {
            val updateData = mutableMapOf<String, Any?>()

            if (contractorUid != null) {
                updateData["contractorUid"] = contractorUid
            }
            if (itemList != null) {
                updateData["item"] = itemList
            }

            firestore.collection(DOCUMENT)
                .document(uid)
                .update(updateData)
                .await()

            Log.d("FirebaseService", "Document successfully updated: $uid")

        } catch (e: Exception) {
            Log.e(FIREBASE_SERVICE, "Error updating document", e)
        }
    }

    /**
     * Deletes a document from the Firestore collection 'DOCUMENT' with the specified document ID.
     * This function performs an atomic deletion operation on the document.
     *
     * @param documentId String The ID of the document to delete from Firestore.
     */
    override suspend fun deleteDocument(documentId: String) {
        try {
            firestore.collection(DOCUMENT)
                .document(documentId)
                .delete()
                .await()
            Log.d("FirebaseService", "DocumentSnapshot successfully deleted!")
        } catch (e: Exception) {
            Log.w("FirebaseService", "Error deleting document", e)
        }
    }
}