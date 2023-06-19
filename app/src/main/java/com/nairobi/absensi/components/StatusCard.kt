package com.nairobi.absensi.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun StatusCard(
    modifier: Modifier = Modifier,
    title: String = "Status Card",
    color: Color = Color.Red,
    content: @Composable () -> Unit = {},
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = color,
            contentColor = Color.White,
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            )
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                pressedElevation = 8.dp,
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
                contentColor = Color.Black,
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            content()
        }
    }
}