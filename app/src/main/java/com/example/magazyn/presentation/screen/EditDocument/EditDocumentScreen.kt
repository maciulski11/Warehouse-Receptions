package com.example.magazyn.presentation.screen.EditDocument

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magazyn.presentation.screen.AddDocument.AddDocumentViewModel
import com.example.magazyn.presentation.screen.Contractor.ContractorsViewModel
import com.example.magazyn.domain.model.Contractor
import com.example.magazyn.domain.model.Item
import com.example.magazyn.presentation.dialogs.AddItemDialog
import com.example.magazyn.presentation.dialogs.EditItemDialog
import com.example.magazyn.presentation.navigation.NavigationSupport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDocumentScreen(
    navController: NavController,
    viewModel: EditDocumentViewModel,
    contractorsViewModel: ContractorsViewModel,
    addDocumentViewModel: AddDocumentViewModel,
) {
    var contractor by remember { mutableStateOf<Contractor?>(null) }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var isEditDialogOpen by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<Item?>(null) }

    val context = LocalContext.current

    val unitOptions = listOf("szt", "kg", "mb")

    val document by viewModel.document.collectAsState()
    val contractorState by viewModel.contractor.collectAsState()

    // Local mutable state for items
    var itemList by remember { mutableStateOf<List<Item>>(emptyList()) }

    // Load document and contractor initially
    LaunchedEffect(viewModel.document.value) {
        document?.let {
            itemList = it.item ?: emptyList()
        }
    }

    LaunchedEffect(contractorState) {
        contractorState?.let {
            contractor = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.document.value?.number ?: "") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {

                            viewModel.updateDocument(
                                contractor?.uid,
                                itemList
                            )
                            navController.navigate(NavigationSupport.DocumentsScreen)
                        }
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Zatwierdź",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            contractor?.let { contractor ->
                Text(
                    text = "${contractor.name} (${contractor.symbol})",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                        .clickable { showSearchDialog = true }
                )

                Text(
                    text = (document?.date ?: ""),
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { isAddDialogOpen = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Dodaj towar")
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(itemList.size) { index ->
                    val item = itemList[index]
                    Item1(item) { clickedItem ->
                        itemToEdit = clickedItem
                        isEditDialogOpen = true
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            if (isAddDialogOpen) {
                AddItemDialog(
                    unitOptions = unitOptions,
                    onAddItem = { name, unit, amount ->
                        val newItem = Item(name = name, unit = unit, amount = amount)
                        if (itemList.any { it.name == newItem.name }) {
                            Toast.makeText(
                                context,
                                "Ten towar już istnieje w dokumencie",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            itemList = itemList + newItem
                        }
                        isAddDialogOpen = false
                    },
                    onCloseDialog = { isAddDialogOpen = false },
                    addDocumentViewModel
                )
            }

            if (isEditDialogOpen && itemToEdit != null) {
                EditItemDialog(
                    item = itemToEdit!!,
                    unitOptions = unitOptions,
                    onEditItem = { name, unit, amount ->
                        val updatedItem =
                            itemToEdit!!.copy(name = name, unit = unit, amount = amount)
                        itemList = itemList.map { if (it == itemToEdit) updatedItem else it }
                        isEditDialogOpen = false
                    },
                    onDeleteItem = {
                        itemToEdit?.let { item ->
                            itemList = itemList.filter { it != item }
                        }
                        isEditDialogOpen = false
                    },
                    onCloseDialog = { isEditDialogOpen = false },
                    addDocumentViewModel
                )
            }
        }
    }

    if (showSearchDialog) {
        AddItemDialog(
            onSelectContractor = { name, symbol, uid->
                contractor?.name = name
                contractor?.symbol = symbol
                contractor?.uid = uid
                showSearchDialog = false
            },
            onCloseDialog = { showSearchDialog = false },
            contractorsViewModel
        )
    }
}

@Composable
fun Item1(item: Item, onClickEdit: (Item) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            .clickable {
                onClickEdit(item)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.name ?: "",
                fontSize = 22.sp,
                maxLines = 2,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item.amount.toString(),
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(2.dp))

            Text(
                text = item.unit ?: "",
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}