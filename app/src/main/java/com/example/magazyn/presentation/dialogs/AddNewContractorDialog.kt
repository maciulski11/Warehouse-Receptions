package com.example.magazyn.presentation.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun AddNewContractorDialog(
    onAddContractor: (String, String) -> Unit,
    onCloseDialog: () -> Unit,
) {
    var contractorName by remember { mutableStateOf("") }
    var contractorSymbol by remember { mutableStateOf("") }

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
                        text = "Dodaj kontrahenta",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                OutlinedTextField(
                    value = contractorName,
                    onValueChange = { contractorName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    label = { Text("Nazwa kontrahenta") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = contractorSymbol,
                    onValueChange = { contractorSymbol = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text("Symbol kontrahenta") },
                    singleLine = true
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
                            if (contractorName.isNotBlank() && contractorSymbol.isNotBlank()) {
                                onAddContractor(contractorName, contractorSymbol)
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
}