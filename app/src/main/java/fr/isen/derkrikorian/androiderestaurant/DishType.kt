package fr.isen.derkrikorian.androiderestaurant

import android.content.Context
import androidx.compose.runtime.Composable

enum class DishType {
    STARTER,
    MAIN,
    DESSERT;

    @Composable
    fun title(context: Context) = when (this) {
        STARTER -> context.getString(R.string.starters)
        MAIN -> context.getString(R.string.main_courses)
        DESSERT -> context.getString(R.string.desserts)
    }
}