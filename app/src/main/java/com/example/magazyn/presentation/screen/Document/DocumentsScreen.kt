package com.example.magazyn.presentation.screen.Document

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magazyn.domain.model.Document
import com.example.magazyn.presentation.dialogs.DeleteDocumentDialog
import com.example.magazyn.presentation.navigation.NavigationSupport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScreen(navController: NavController, viewModel: DocumentsViewModel) {
    var searchText by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val documents by viewModel.documentList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dokumenty Przyjęć") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton({ navController.navigate(NavigationSupport.MainScreen) }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchExpanded = !isSearchExpanded }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(NavigationSupport.AddDocumentScreen)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Document")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LaunchedEffect(isSearchExpanded) {
                if (!isSearchExpanded) {
                    searchText = ""
                }
            }
            AnimatedVisibility(visible = isSearchExpanded) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Wyszukaj dokument...") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(documents.filter {
                    (it.number?.contains(searchText, ignoreCase = true) ?: false) ||
                            (it.contractor?.name?.contains(searchText, ignoreCase = true) ?: false)
                }) { document ->
                    DocumentItem(
                        document,
                        onClick = {
                            navController
                                .navigate("${NavigationSupport.EditDocumentScreen}/${document.uid}/${document.contractorUid}")
                        },
                        onDelete = { viewModel.deleteDocument(document.uid.toString()) }
                    )
                }
            }
        }
    }
}

@Composable
fun DocumentItem(
    document: Document,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        showDialog = true
                    },
                    onTap = {
                        onClick()
                    }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = document.number ?: "",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = document.contractor?.name ?: "",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Text(
                text = document.date ?: "",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(end = 8.dp, bottom = 0.dp)
            )
        }
    }

    if (showDialog) {
        DeleteDocumentDialog(
            document = document,
            onDelete = {
                onDelete()
                showDialog = false
            },
            onClose = {
                showDialog = false
            }
        )
    }
}