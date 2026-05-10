package com.abhiumale.kartikmartapp.ui.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapAddressPicker(
    onLocationSelected: (String, LatLng) -> Unit,
    onDismiss: () -> Unit
) {
    val amravati = LatLng(20.9320, 77.7523) // Default center
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(amravati, 15f)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val selectedPos = cameraPositionState.position.target
                // Yahan aap Geocoder use karke address string nikal sakte hain
                onLocationSelected("Selected Location", selectedPos)
            }) { Text("Confirm Address") }
        },
        text = {
            Box(Modifier.size(400.dp)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                )
                // Center Marker Overlay
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center).size(40.dp),
                    tint = Color.Red
                )
            }
        }
    )
}