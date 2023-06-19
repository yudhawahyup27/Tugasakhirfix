package com.nairobi.absensi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AppBar(
    navController: NavController? = null,
    title: String = "",
    trailing: @Composable () -> Unit = {},
    trailingOnClick: () -> Unit = {},
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Purple)
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { navController?.popBackStack() },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White,
                ),
                modifier = Modifier
                    .width(30.dp)
            ) {
                if (navController != null) {
                    Icon(
                        Icons.Default.ArrowBackIos,
                        contentDescription = "Back",
                    )
                }
            }
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    // take all remaining space
                    .weight(1f)
            )
            IconButton(
                onClick = { trailingOnClick() },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White,
                ),
                modifier = Modifier
                    .width(30.dp)
            ) {
                trailing()
            }
        }
    }
}