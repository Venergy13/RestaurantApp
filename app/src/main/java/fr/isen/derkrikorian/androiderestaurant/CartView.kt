package fr.isen.derkrikorian.androiderestaurant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun CartView(innerPadding: PaddingValues, forceUpdate: MutableState<Boolean>) {
    val file = File(LocalContext.current.filesDir, "cart.json")
    val gson = Gson()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val cartItems = remember { mutableStateOf<MutableList<CartItem>>(mutableListOf()) }

    LaunchedEffect(Unit, forceUpdate.value) {
        if (file.exists()) {
            val cartJson = file.readText()
            val cart = gson.fromJson(cartJson, Cart::class.java)
            cartItems.value = cart.items
        }
    }
    if (cartItems.value.isNotEmpty()) {
        Column(modifier = Modifier.padding(innerPadding)) {
            cartItems.value.forEach { cartItem ->
                val validImages = remember { mutableStateListOf<String>() }
                LaunchedEffect(cartItem.dish.images) {
                    cartItem.dish.images.forEach { imageUrl ->
                        if (isUrlValid(imageUrl)) {
                            validImages.add(imageUrl)
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .paddingFromBaseline(top = 8.dp)
                        .paddingFromBaseline(bottom = 8.dp)
                ) {
                    if (validImages.isNotEmpty()) {
                        val painter: Painter =
                            rememberImagePainter(
                                data = validImages.first())
                        Image(
                            painter = painter,
                            contentDescription = cartItem.dish.name,
                            modifier = Modifier.size(100.dp)
                        )
                    } else {
                        val painter: Painter = painterResource(id = R.drawable.logo)
                        Image(
                            painter = painter,
                            contentDescription = cartItem.dish.name,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                        Text(
                            text = cartItem.dish.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Text(
                            text = context.getString(R.string.quantity)+ ": ${cartItem.quantity}",
                            modifier = Modifier.padding(start = 15.dp)
                        )
                    }
                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                cartItems.value.remove(cartItem)
                                val cart = Cart(cartItems.value)
                                val updatedCartJson = gson.toJson(cart)
                                file.writeText(updatedCartJson)

                                cartItems.value = cart.items
                                forceUpdate.value = !forceUpdate.value

                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Remove item"
                            )
                        }
                    }
                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                        IconButton(onClick = {
                            if (cartItem.quantity > 1) {
                                cartItem.quantity--
                                val cart = Cart(cartItems.value)
                                val updatedCartJson = gson.toJson(cart)
                                GlobalScope.launch(Dispatchers.IO) {
                                    file.writeText(updatedCartJson)
                                    withContext(Dispatchers.Main) {
                                        forceUpdate.value = !forceUpdate.value
                                    }
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowLeft,
                                contentDescription = "Decrease quantity"
                            )
                        }
                    }
                }
                Divider(color = Color.Gray)
            }
            val totalPrice =
                cartItems.value.sumOf { it.dish.prices.first().price.toDouble() * it.quantity }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(Color(0xFFFFD580)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.total_price) +" : $totalPrice €",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    } else {
        Text(text = context.getString(R.string.cart_is_empty), modifier = Modifier.padding(innerPadding) , style = MaterialTheme.typography.titleLarge)
    }
}