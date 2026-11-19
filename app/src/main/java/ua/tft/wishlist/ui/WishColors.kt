package ua.tft.wishlist.ui

import androidx.compose.ui.graphics.Color

object WishColors {
    val Background = Color(0xFF0B0C10)
    val SurfaceCard = Color(0xFF15171F)
    val SurfaceElevated = Color(0xFF1E212B)
    val GoldAccent = Color(0xFFE5B85C)
    val GoldSoft = Color(0x26E5B85C)
    val TextPrimary = Color(0xFFF5F6FA)
    val TextSecondary = Color(0xFF8C92A4)
    val BorderSubtle = Color(0xFF262A36)
    val SuccessGreen = Color(0xFF34D399)
    val DangerRed = Color(0xFFFF5C5C)
}

enum class HolidayMotion {
    FALL_DOWN,   // Новий рік (сніг падає вниз)
    FLOAT_UP,    // ДН (кульки летять вгору)
    PULSE,       // Різдво (мерехтіння зірок)
    DRIFT        // Інше (діагональний дрейф)
}

data class HolidayTheme(
    val cardBg: Color,
    val accent: Color,
    val border: Color,
    val emojis: List<String>,
    val motion: HolidayMotion
)

fun holidayThemeFor(holiday: String): HolidayTheme = when (holiday) {
    "Новий рік" -> HolidayTheme(
        cardBg = Color(0xFF111B1D),
        accent = Color(0xFF38BDF8),
        border = Color(0xFF1E363B),
        emojis = listOf("❄️", "🎄", "🥂", "⛄"),
        motion = HolidayMotion.FALL_DOWN
    )
    "День народження" -> HolidayTheme(
        cardBg = Color(0xFF1C1422),
        accent = Color(0xFFE879F9),
        border = Color(0xFF362244),
        emojis = listOf("🎈", "🎂", "🎉", "🎊"),
        motion = HolidayMotion.FLOAT_UP
    )
    "Різдво" -> HolidayTheme(
        cardBg = Color(0xFF1E1615),
        accent = Color(0xFFFBBF24),
        border = Color(0xFF3D2B24),
        emojis = listOf("⭐", "🕯️", "✨", "🔔"),
        motion = HolidayMotion.PULSE
    )
    else -> HolidayTheme(
        cardBg = Color(0xFF161820),
        accent = Color(0xFFE5B85C),
        border = Color(0xFF282C3A),
        emojis = listOf("🎁", "💎", "⚡", "✨"),
        motion = HolidayMotion.DRIFT
    )
}