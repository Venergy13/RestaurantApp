package fr.isen.derkrikorian.androiderestaurant

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.derkrikorian.androiderestaurant.ui.theme.AndroidERestaurantTheme
import android.widget.Toast
import android.content.Intent

interface RestaurantMenu {

    @Composable
    fun DishButton(dishType: DishType, context: Context) {
        Button(
            onClick = { moveToDetailActivity(dishType) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White
            )
        ) {
            Text(
                text = dishType.title(context),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                color = Color.White
            )
        }
    }

    fun showToast(message: String)

    fun moveToDetailActivity(dishType: DishType)
}

class MainActivity : ComponentActivity(), RestaurantMenu {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this
        setContent {
            AndroidERestaurantTheme {
                // A surface container white background
                Surface(color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(400.dp).clip(MaterialTheme.shapes.medium).padding(top = 50.dp)
                        )
                        ListElements(menu = activity)
                    }
                }
            }
        }
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun moveToDetailActivity(dishType: DishType) {
        val intent = Intent(this, ListActivity::class.java)
        intent.putExtra("dishType", dishType.name)
        this.startActivity(intent)
    }

    @Composable
    fun ListElements(menu: RestaurantMenu) {
        Column(
            // Make each element take the maximum width
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            menu.DishButton(DishType.STARTER, this@MainActivity)
            Spacer(modifier = Modifier.height(16.dp))
            menu.DishButton(DishType.MAIN, this@MainActivity)
            Spacer(modifier = Modifier.height(16.dp))
            menu.DishButton(DishType.DESSERT, this@MainActivity)
        }
    }
}

