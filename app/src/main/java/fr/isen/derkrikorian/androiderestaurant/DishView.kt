package fr.isen.derkrikorian.androiderestaurant

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@Composable
fun DishView(menuItems: MenuResult, dishType: DishType, innerPadding: PaddingValues, navController: NavController){
    val category = menuItems.data.find {
        when (dishType) {
            DishType.STARTER -> it.categoryName == "Entrées"
            DishType.MAIN -> it.categoryName == "Plats"
            DishType.DESSERT -> it.categoryName == "Desserts"
        }
    }
    category?.dishes?.let { dishes ->
        Column(modifier = Modifier.padding(innerPadding)) {
            dishes.forEach { dish ->
                val validImages = remember { mutableStateListOf<String>() }
                LaunchedEffect(dish.images) {
                    dish.images.forEach { imageUrl ->
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
                        .clickable {
                            navController.navigate("dishDetail/${dish.id}")
                        }
                        .fillMaxWidth()
                ) {
                    if (validImages.isNotEmpty()) {
                        val painter: Painter = rememberImagePainter(data = validImages.first())
                        Image(painter = painter, contentDescription = dish.name, modifier = Modifier.size(100.dp))
                    }
                    else {
                        val painter: Painter = painterResource(id = R.drawable.logo)
                        Image(painter = painter, contentDescription = dish.name, modifier = Modifier.size(100.dp))
                    }
                    Column (modifier = Modifier.align(Alignment.CenterVertically)) {
                        Text(
                            text = dish.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Text(
                            text = "Price: ${dish.prices.first().price} €",
                            modifier = Modifier.padding(start = 15.dp)
                        )
                    }
                }
                Divider(color = Color.Gray)
            }
        }
    }
}