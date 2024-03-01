package fr.isen.derkrikorian.androiderestaurant

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Dish(@SerializedName("name_fr") val name: String, val prices: List<Price>, val id: String, val ingredients: List<Ingredient>, val images: List<String>) : Serializable
data class Price(val price: String) : Serializable
data class Ingredient(@SerializedName("name_fr") val ingredient: String) : Serializable
data class Category(@SerializedName("name_fr") val categoryName: String, @SerializedName("items")val dishes: List<Dish>) : Serializable
data class MenuResult(val data: List<Category>) : Serializable

data class CartItem(val dish: Dish, var quantity: Int)
data class Cart(val items: MutableList<CartItem> = mutableListOf()) {
    fun addItem(item: CartItem) {
        val existingItem = items.find { it.dish.id == item.dish.id }
        if (existingItem != null) {
            existingItem.quantity += item.quantity
        } else {
            items.add(item)
        }
    }
}