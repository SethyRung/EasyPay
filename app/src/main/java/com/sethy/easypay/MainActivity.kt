package com.sethy.easypay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sethy.easypay.navigation.EasyPayNavGraph
import com.sethy.easypay.ui.theme.EasyPayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyPayTheme {
                EasyPayNavGraph()
            }
        }
    }
}
