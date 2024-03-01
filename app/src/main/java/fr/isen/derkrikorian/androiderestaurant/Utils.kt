package fr.isen.derkrikorian.androiderestaurant

import android.content.Context
import androidx.compose.runtime.MutableState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

suspend fun isUrlValid(urlString: String): Boolean = withContext(Dispatchers.IO) {
    try {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "HEAD"
        connection.connect()
        val code = connection.responseCode
        connection.disconnect()
        return@withContext code == HttpURLConnection.HTTP_OK
    } catch (e: Exception) {
        return@withContext false
    }
}

fun handleOnBuyClick(context: Context, dish: Dish, quantity: Int, forceUpdate: MutableState<Boolean>) {
    val gson = Gson()
    val file = File(context.filesDir, "cart.json")
    val cart = if (file.exists()) {
        val existingCartJson = file.readText()
        gson.fromJson(existingCartJson, Cart::class.java)
    } else {
        Cart()
    }

    cart.addItem(CartItem(dish, quantity))

    val updatedCartJson = gson.toJson(cart)

    GlobalScope.launch(Dispatchers.IO) {
        file.writeText(updatedCartJson)
        withContext(Dispatchers.Main) {
            forceUpdate.value = !forceUpdate.value
        }
    }
}