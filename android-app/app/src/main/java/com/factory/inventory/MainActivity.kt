package com.factory.inventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import com.factory.inventory.ui.screens.LoginScreen
import com.factory.inventory.ui.screens.HomeScreen
import com.factory.inventory.ui.theme.FactoryInventoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FactoryInventoryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isLoggedIn by remember { mutableStateOf(false) }
                    
                    if (isLoggedIn) {
                        HomeScreen(
                            onLogout = { isLoggedIn = false }
                        )
                    } else {
                        LoginScreen(
                            onLoginSuccess = { isLoggedIn = true }
                        )
                    }
                }
            }
        }
    }
}
