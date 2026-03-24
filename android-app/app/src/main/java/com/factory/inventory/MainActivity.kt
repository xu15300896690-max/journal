package com.factory.inventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.factory.inventory.ui.screens.*
import com.factory.inventory.ui.theme.FactoryInventoryTheme

sealed class Screen {
    object Login : Screen()
    object Home : Screen()
    object Inbound : Screen()
    object Outbound : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FactoryInventoryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
                    
                    when (currentScreen) {
                        is Screen.Login -> {
                            LoginScreen(
                                onLoginSuccess = { currentScreen = Screen.Home }
                            )
                        }
                        is Screen.Home -> {
                            HomeScreen(
                                onNavigateToInbound = { currentScreen = Screen.Inbound },
                                onNavigateToOutbound = { currentScreen = Screen.Outbound },
                                onLogout = { currentScreen = Screen.Login }
                            )
                        }
                        is Screen.Inbound -> {
                            InboundScreen(
                                onNavigateBack = { currentScreen = Screen.Home }
                            )
                        }
                        is Screen.Outbound -> {
                            OutboundScreen(
                                onNavigateBack = { currentScreen = Screen.Home }
                            )
                        }
                    }
                }
            }
        }
    }
}
