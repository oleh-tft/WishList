package ua.tft.wishlist.model

data class WishItem(
    val id: Long = 0,
    val title: String,
    val description: String,
    val price: Double,
    val imageUri: String = "",
    val holiday: String = "Новий рік",
    val isFulfilled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

object Holidays {
    val list = listOf(
        "Новий рік",
        "День народження",
        "Різдво",
        "Інше"
    )

    fun emojiFor(holiday: String): String = when (holiday) {
        "Новий рік" -> "🎄"
        "День народження" -> "🎂"
        "Різдво" -> "✨"
        else -> "🎁"
    }
}