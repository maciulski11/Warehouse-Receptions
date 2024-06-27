package com.example.magazyn.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.magazyn.data.FirebaseService
import com.example.magazyn.presentation.navigation.NavigationScreens
import com.example.magazyn.ui.theme.MagazynTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseService: FirebaseService = FirebaseService(firebaseFirestore)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MagazynTheme {
                NavigationScreens(firebaseService)
            }
        }
    }
}