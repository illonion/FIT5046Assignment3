package com.example.todolist.Analytics

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todolist.ui.theme.Purple40
import com.example.todolist.ui.theme.Purple80

@Composable
fun AnalyticsButton(navController: NavHostController, route: String, text: String) {
    Button(
        onClick = { navController.navigate(route) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Purple80,
        )
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 17.sp),
            color = Purple40
        )
    }
}