package com.example.magazyn.presentation.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.magazyn.domain.model.Document

@Composable
fun DeleteDocumentDialog(
    document: Document,
    onDelete: () -> Unit,
    onClose: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Czy chcesz usunąć:\n ${document.number}?") },
        confirmButton = {
            Button(
                onClick = {
                    onDelete()
                },
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text("Usuń")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onClose()
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text(
                    text = "Anuluj",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}
