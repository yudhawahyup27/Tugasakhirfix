package com.nairobi.absensi.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun Fab(
    icon: ImageVector? = null,
    onClick: () -> Unit = {},
) {
    FloatingActionButton(
        onClick,
        containerColor = Purple,
        contentColor = Color.White,
        shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 100)),
        modifier = Modifier
            .padding(16.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = "Add")
        }
    }
}

@Composable
fun FabAdd(onClick: () -> Unit = {}) {
    Fab(
        icon = Icons.Default.Add,
        onClick = onClick,
    )
}