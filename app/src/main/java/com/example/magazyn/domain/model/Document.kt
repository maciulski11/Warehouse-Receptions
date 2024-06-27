package com.example.magazyn.domain.model

data class Document(
    val date: String? = null,
    val number: String? = null,
    var contractorUid: String? = null,
    val item: List<Item>? = null,
    val uid: String? = null,
    var contractor: Contractor? = null
)