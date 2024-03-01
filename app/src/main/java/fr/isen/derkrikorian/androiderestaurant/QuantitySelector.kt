package fr.isen.derkrikorian.androiderestaurant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuantitySelector(quantity: MutableState<Int>) {
    Row(modifier = Modifier
        .padding(top = 20.dp, start = 10.dp)
    ) {
        Button(
            onClick = { if (quantity.value > 1) quantity.value-- },
            shape = CircleShape, // Make the button circular
            modifier = Modifier.size(50.dp) // Set a fixed size for the button
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "-", style = MaterialTheme.typography.titleLarge, modifier = Modifier.offset(x = (-2).dp))
            }
        }
        Text(text = quantity.value.toString(), style = MaterialTheme.typography.titleLarge, modifier = Modifier
            .padding(horizontal = 40.dp)
            .align(Alignment.CenterVertically))
        Button(
            onClick = { quantity.value ++},
            shape = CircleShape, // Make the button circular
            modifier = Modifier.size(50.dp) // Set a fixed size for the button
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "+", style = MaterialTheme.typography.titleLarge, modifier = Modifier.offset(x = (-4).dp))
            }
        }
    }
}
