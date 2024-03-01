package fr.isen.derkrikorian.androiderestaurant

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopAppBarExample( dishType: DishType) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current as ListActivity
    val navController = rememberNavController()
    val gson = Gson()
    val file = File(context.filesDir, "cart.json")
    val cartItems = remember { mutableStateOf<MutableList<CartItem>>(mutableListOf()) }
    val forceUpdate = remember { mutableStateOf(false) }
    val totalItems = remember { mutableStateOf(0) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        if (file.exists()) {
            val cartJson = file.readText()
            val cart = gson.fromJson(cartJson, Cart::class.java)
            cartItems.value = cart.items
        }
    }

    totalItems.value = cartItems.value.sumOf { it.quantity }

    Scaffold(

        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFD580),
                    titleContentColor = Color.Black,
                ),
                title = {
                    Text(
                        text = if (currentRoute == "cart") context.getString(R.string.cart) else dishType.title(context),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController.currentDestination?.route == "menu") {
                            //finish the activity
                            context.finish()
                        }
                        else {
                            navController.popBackStack() } }

                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }

                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("cart")
                    }, modifier = Modifier.size(80.dp)) {
                        BadgedBox(badge = {
                            if (totalItems.value > 0) {
                                Badge(contentColor = Color.White) {
                                    Text(text = totalItems.value.toString(), fontSize = 13.sp)
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        NavHost(navController, startDestination = "menu") {
            composable("menu") { MenuView(dishType, innerPadding , navController) }
            composable("dishDetail/{dishId}") { backStackEntry ->
                val dishId = backStackEntry.arguments?.getString("dishId")
                val sharedPref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                val menuItemsJson = sharedPref.getString("menuItems", "")
                val menuItems = GsonBuilder().create().fromJson(menuItemsJson.toString(), MenuResult::class.java)
                Log.d("ListActivity", "onCreate: $menuItems")
                val dish = menuItems.data.flatMap { it.dishes }.find { it.id == dishId }
                dish?.let {
                    DishDetailView(it , innerPadding, forceUpdate)
                }
            }
            composable("cart") { CartView(innerPadding, forceUpdate) }
        }
    }

}
