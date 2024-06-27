package com.example.magazyn.presentation.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.magazyn.presentation.screen.AddDocument.AddDocumentViewModel

@Composable
fun AddItemDialog(
    unitOptions: List<String>,
    onAddItem: (String, String, String) -> Unit,
    onCloseDialog: () -> Unit,
    viewModel: AddDocumentViewModel,
) {
    var itemName by remember { mutableStateOf("") }
    var itemUnit by remember { mutableStateOf("") }
    var itemAmount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var showItemDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onCloseDialog,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(16.dp)
                .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dodaj Towar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    IconButton(onClick = { showItemDialog = true }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                }

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    label = { Text("Nazwa towaru") },
                    singleLine = true
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = itemUnit,
                        onValueChange = { /* No-op */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = false) { /* No-op */ },
                        label = { Text("Jednostka miary") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand",
                                modifier = Modifier.clickable { expanded = true }
                            )
                        }
                    )

                    DropdownMenu(
                        modifier = Modifier.width(256.dp),
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        unitOptions.forEach { unit ->
                            DropdownMenuItem(
                                onClick = {
                                    itemUnit = unit
                                    expanded = false
                                },
                                text = {
                                    Text(text = unit)
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = itemAmount,
                    onValueChange = { itemAmount = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text("Ilość") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onCloseDialog,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Anuluj")
                    }
                    Button(
                        onClick = {
                            if (itemName.isNotBlank() && itemUnit.isNotBlank() && itemAmount.isNotBlank()) {
                                onAddItem(itemName, itemUnit, itemAmount)
                                viewModel.addItem(itemName, itemUnit, itemAmount)
                                onCloseDialog()
                            }
                        }
                    ) {
                        Text("Dodaj")
                    }
                }
            }
        }
    }

    if (showItemDialog) {
        SearchItemDialog(
            onSelectItem = { name, unit ->
                itemName = name
                itemUnit = unit
                showItemDialog = false
            },
            onCloseDialog = { showItemDialog = false },
            viewModel
        )
    }
}