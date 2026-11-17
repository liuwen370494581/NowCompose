package com.lw.nowcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lw.nowcompose.ui.NowComposeApp
import com.lw.nowcompose.ui.theme.NowComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NowComposeTheme {
                NowComposeApp()
            }
        }
    }
}
