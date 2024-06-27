package com.example.magazyn.presentation.screen.Contractor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magazyn.domain.model.Contractor
import com.example.magazyn.presentation.dialogs.AddNewContractorDialog
import com.example.magazyn.presentation.dialogs.EditContractorDialog
import com.example.magazyn.presentation.navigation.NavigationSupport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorsScreen(navController: NavController, viewModel: ContractorsViewModel) {
    var searchText by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var isAddContractorDialogOpen by remember { mutableStateOf(false) }
    var isEditContractorDialogOpen by remember { mutableStateOf(false) }
    var selectedContractor by remember { mutableStateOf<Contractor?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kontrahenci") },
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
            FloatingActionButton(onClick = { isAddContractorDialogOpen = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Contractor")
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
                    placeholder = { Text("Wyszukaj kontrahenta...") },
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
                items(viewModel.contractorList.value.filter { contractor ->
                    contractor.name?.contains(searchText, ignoreCase = true) == true ||
                            contractor.symbol?.contains(searchText, ignoreCase = true) == true
                }) { contractor ->
                    ContractorItem(contractor, onClickEdit = {
                        selectedContractor = it
                        isEditContractorDialogOpen = true
                    })
                }
            }

            if (isAddContractorDialogOpen) {
                AddNewContractorDialog(
                    onAddContractor = { name, symbol ->
                        viewModel.addContractor(name, symbol)
                        isAddContractorDialogOpen = false
                    },
                    onCloseDialog = { isAddContractorDialogOpen = false }
                )
            }

            if (isEditContractorDialogOpen && selectedContractor != null) {
                EditContractorDialog(
                    contractor = selectedContractor!!,
                    onEditContractor = { uid, name, symbol ->
                        viewModel.updateContractor(uid, name, symbol)
                        isEditContractorDialogOpen = false
                    },
                    onCloseDialog = { isEditContractorDialogOpen = false }
                )
            }
        }
    }
}

@Composable
fun ContractorItem(
    contractor: Contractor,
    onClickEdit: (Contractor) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClickEdit(contractor) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = contractor.name ?: "",
                fontSize = 20.sp,
                color = LocalContentColor.current,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Symbol: ${contractor.symbol}",
                fontSize = 16.sp,
                color = LocalContentColor.current
            )
        }
    }
}
