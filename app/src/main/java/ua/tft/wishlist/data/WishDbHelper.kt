package ua.tft.wishlist.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ua.tft.wishlist.model.WishItem

class WishDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "wishlist_app.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_WISHES = "wishes"
        private const val COL_ID = "id"
        private const val COL_TITLE = "title"
        private const val COL_DESC = "description"
        private const val COL_PRICE = "price"
        private const val COL_IMAGE = "image_uri"
        private const val COL_HOLIDAY = "holiday"
        private const val COL_FULFILLED = "is_fulfilled"
        private const val COL_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_WISHES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_DESC TEXT,
                $COL_PRICE REAL NOT NULL,
                $COL_IMAGE TEXT,
                $COL_HOLIDAY TEXT NOT NULL,
                $COL_FULFILLED INTEGER DEFAULT 0,
                $COL_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)

        // Стартові приклади, щоб список не був порожнім при першому запуску
        seedInitialData(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        val samples = listOf(
            WishItem(
                title = "Механічна клавіатура Keychron",
                description = "Компактна 75% з тихими світчами та підсвіткою для комфортного кодингу",
                price = 4800.0,
                imageUri = "https://images.unsplash.com/photo-1595225476474-87563907a212?w=500",
                holiday = "Новий рік"
            ),
            WishItem(
                title = "Навушники Sony WH-1000XM5",
                description = "З активним шумозаглушенням у чорному кольорі для роботи та подорожей",
                price = 13999.0,
                imageUri = "https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?w=500",
                holiday = "День народження"
            )
        )
        samples.forEach { item ->
            db.insert(TABLE_WISHES, null, item.toContentValues())
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WISHES")
        onCreate(db)
    }

    fun getAllWishes(): List<WishItem> {
        val wishes = mutableListOf<WishItem>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_WISHES,
            null,
            null,
            null,
            null,
            null,
            "$COL_FULFILLED ASC, $COL_CREATED_AT DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                wishes.add(
                    WishItem(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_TITLE)),
                        description = it.getString(it.getColumnIndexOrThrow(COL_DESC)) ?: "",
                        price = it.getDouble(it.getColumnIndexOrThrow(COL_PRICE)),
                        imageUri = it.getString(it.getColumnIndexOrThrow(COL_IMAGE)) ?: "",
                        holiday = it.getString(it.getColumnIndexOrThrow(COL_HOLIDAY)),
                        isFulfilled = it.getInt(it.getColumnIndexOrThrow(COL_FULFILLED)) == 1,
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COL_CREATED_AT))
                    )
                )
            }
        }
        return wishes
    }

    fun insertWish(wish: WishItem): Long {
        return writableDatabase.insert(TABLE_WISHES, null, wish.toContentValues())
    }

    fun updateWish(wish: WishItem): Int {
        return writableDatabase.update(
            TABLE_WISHES,
            wish.toContentValues(),
            "$COL_ID = ?",
            arrayOf(wish.id.toString())
        )
    }

    fun toggleFulfilled(id: Long, fulfilled: Boolean): Int {
        val values = ContentValues().apply {
            put(COL_FULFILLED, if (fulfilled) 1 else 0)
        }
        return writableDatabase.update(
            TABLE_WISHES,
            values,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun deleteWish(id: Long): Int {
        return writableDatabase.delete(
            TABLE_WISHES,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }

    private fun WishItem.toContentValues() = ContentValues().apply {
        put(COL_TITLE, title)
        put(COL_DESC, description)
        put(COL_PRICE, price)
        put(COL_IMAGE, imageUri)
        put(COL_HOLIDAY, holiday)
        put(COL_FULFILLED, if (isFulfilled) 1 else 0)
        put(COL_CREATED_AT, createdAt)
    }
}