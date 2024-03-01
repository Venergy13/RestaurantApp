package fr.isen.derkrikorian.androiderestaurant

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch

@SuppressLint("StringFormatInvalid")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DishDetailView(dish: Dish, innerPadding: PaddingValues, forceUpdate: MutableState<Boolean>) {
    val coroutineScope = rememberCoroutineScope()
    val validImages = remember { mutableStateListOf<String>() }

    LaunchedEffect(dish) {
        coroutineScope.launch {
            dish.images.forEach { imageUrl ->
                if (isUrlValid(imageUrl)) {
                    validImages.add(imageUrl)
                }
            }
        }
    }

    val pagerState = rememberPagerState {
        validImages.size.takeIf { it > 0 } ?: 1
    }



    Box(modifier = Modifier.padding(innerPadding)) {
        var quantity = remember { mutableIntStateOf(1) }
        var context = LocalContext.current
        var showSnackbar by remember { mutableStateOf(false) }
        var snackbarMessage by remember { mutableStateOf("") }

        Column {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
                val painter: Painter = if (validImages.isNotEmpty()) {
                    rememberImagePainter(data = validImages[page])
                } else {
                    painterResource(id = R.drawable.logo) // Replace with your logo resource ID
                }
                Image(
                    painter = painter,
                    contentDescription = dish.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.FillBounds // Distort the image to fill the size
                )
            }
            Text(
                text = dish.name,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
                modifier = Modifier.padding(top = 25.dp , start = 10.dp)
            )
            Text(
                text =  context.getString(R.string.ingredients) + " : ${dish.ingredients.joinToString { it.ingredient }}",
                modifier = Modifier.padding(top = 10.dp , start = 10.dp)
            )
            Row(modifier = Modifier
                .padding(top = 30.dp)
                .align(Alignment.CenterHorizontally)
            ) {
                QuantitySelector(quantity)
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFFD580), CircleShape)
                        .border(2.dp, Color.Black, CircleShape)
                        .clickable {
                            coroutineScope.launch {
                                handleOnBuyClick(context, dish, quantity.value, forceUpdate)
                                snackbarMessage = context.getString(R.string.added_to_cart)
                                showSnackbar = true
                            }
                        }
                ) {
                    val price = dish.prices.first().price.toFloat() * quantity.value
                    Text(text = context.getString(R.string.total_price) + " : $price €", style = MaterialTheme.typography.titleLarge, modifier = Modifier
                        .align(Alignment.Center)
                        .padding(20.dp))
                }
            }
        }
        if (showSnackbar) {
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter),
                action = {
                    TextButton(onClick = { showSnackbar = false }) {
                        Text(text = "OK")
                    }
                }
            ) {
                Text(text = snackbarMessage)
            }
        }
    }
}