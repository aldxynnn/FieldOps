package com.example.fieldops

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fieldops.data.remote.RetrofitClient
import com.example.fieldops.navigation.AppNavigation
import com.example.fieldops.ui.theme.FieldOpsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RetrofitClient.initialize(this)

        enableEdgeToEdge()

        setContent {
            FieldOpsTheme {
                AppNavigation()
            }
        }
    }
}
