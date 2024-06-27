package com.example.magazyn.presentation.screen.AddDocument

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magazyn.presentation.screen.Contractor.ContractorsViewModel
import com.example.magazyn.domain.model.Contractor
import com.example.magazyn.domain.model.Item
import com.example.magazyn.presentation.dialogs.AddContractorDialog
import com.example.magazyn.presentation.dialogs.AddItemDialog
import com.example.magazyn.presentation.navigation.NavigationSupport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentScreen(
    navController: NavController,
    addDocumentViewModel: AddDocumentViewModel,
    viewModel: ContractorsViewModel,
) {
    var contractor by remember { mutableStateOf<Contractor?>(null) }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var isAddContractorDialogOpen by remember { mutableStateOf(false) }

    val defaultUnitOptions = listOf("szt.", "mb", "kg")

    var itemsList by remember { mutableStateOf<List<Item>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodawanie PZ") },
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
                            addDocumentViewModel.addDocument(contractor?.uid.toString(), itemsList)
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
            if (contractor == null) {
                Button(
                    onClick = { isAddContractorDialogOpen = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Dodaj kontrahenta")
                }
            } else {
                Text(
                    text = "${contractor?.name} (${contractor?.symbol})",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { isAddDialogOpen = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Dodaj towar")
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(itemsList) { item ->
                    ItemRow(item)
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            if (isAddDialogOpen) {
                AddItemDialog(
                    unitOptions = defaultUnitOptions,
                    onAddItem = { name, unit, amount ->
                        val newItem = Item(name = name, unit = unit, amount = amount)
                        itemsList = itemsList + newItem
                        isAddDialogOpen = false
                    },
                    onCloseDialog = { isAddDialogOpen = false },
                    addDocumentViewModel
                )
            }

            if (isAddContractorDialogOpen) {
                AddContractorDialog(
                    onAddContractor = { name, symbol, uid ->
                        contractor = Contractor(name = name, symbol = symbol, uid = uid)
                        isAddContractorDialogOpen = false
                    },
                    onCloseDialog = { isAddContractorDialogOpen = false },
                    viewModel
                )
            }
        }
    }
}

@Composable
fun ItemRow(item: Item) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
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
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(2.dp))

            Text(
                text = item.unit ?: "",
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
