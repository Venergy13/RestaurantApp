package fr.isen.derkrikorian.androiderestaurant

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import org.json.JSONObject

@Composable
fun MenuView(dishType: DishType, innerPadding: PaddingValues, navController: NavController){
    val context = LocalContext.current as ListActivity
    val queue = Volley.newRequestQueue(context)
    val params = JSONObject()
    params.put("id_shop", "1")
    val url = "http://test.api.catering.bluecodegames.com/menu"
    val menuItems = remember { mutableStateOf<MenuResult?>(null) }
    val request = JsonObjectRequest(
        Request.Method.POST,
        url,
        params,
        { response ->
            val sharedPref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("menuItems", response.toString())
            editor.apply()
            Log.d("MenuView", "onCreate: $response")
            menuItems.value = GsonBuilder().create().fromJson(response.toString(), MenuResult::class.java)
        },
        { error ->
            Log.d("MenuView", "onCreate: $error")
        }
    )
    queue.add(request)

    menuItems.value?.let {
        DishView(it, dishType , innerPadding , navController)
    }
}