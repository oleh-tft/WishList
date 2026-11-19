package ua.tft.wishlist.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ua.tft.wishlist.model.Holidays
import ua.tft.wishlist.model.WishItem
import ua.tft.wishlist.util.ImageStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWishSheet(
    editingItem: WishItem?,
    onDismiss: () -> Unit,
    onSave: (WishItem) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(editingItem?.title ?: "") }
    var description by remember { mutableStateOf(editingItem?.description ?: "") }
    var priceText by remember {
        mutableStateOf(
            editingItem?.price?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: ""
        )
    }
    var imageUri by remember { mutableStateOf(editingItem?.imageUri ?: "") }
    var selectedHoliday by remember { mutableStateOf(editingItem?.holiday ?: Holidays.list.first()) }
    var showError by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedPath = ImageStorage.saveImageToInternalStorage(context, uri)
            if (savedPath.isNotBlank()) {
                imageUri = savedPath
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = WishColors.SurfaceCard,
        contentColor = WishColors.TextPrimary,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = if (editingItem == null) "Нове бажання ✨" else "Редагувати бажання",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = WishColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Прев'ю зображення + вибір з галереї
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(WishColors.SurfaceElevated)
                    .clickable {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri.isNotBlank()) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Прев'ю",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Змінити фото",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.AddPhotoAlternate,
                            contentDescription = null,
                            tint = WishColors.GoldAccent,
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Натисни, щоб обрати фото з галереї",
                            color = WishColors.TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Поле для посилання на зображення (опціонально)
            WishTextField(
                value = imageUri,
                onValueChange = { imageUri = it },
                label = "Або встав URL картинки (необов'язково)",
                leadingIcon = {
                    Icon(Icons.Rounded.Link, contentDescription = null, tint = WishColors.TextSecondary)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Назва
            WishTextField(
                value = title,
                onValueChange = {
                    title = it
                    showError = false
                },
                label = "Назва бажання *",
                isError = showError && title.isBlank()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Опис
            WishTextField(
                value = description,
                onValueChange = { description = it },
                label = "Опис (колір, модель, посилання на магазин)",
                singleLine = false,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Вартість
            WishTextField(
                value = priceText,
                onValueChange = { input ->
                    if (input.all { it.isDigit() || it == '.' }) {
                        priceText = input
                        showError = false
                    }
                },
                label = "Орієнтовна вартість (₴) *",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = showError && (priceText.toDoubleOrNull() == null)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Вибір свята
            Text(
                text = "До якого свята?",
                color = WishColors.TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Holidays.list.forEach { holiday ->
                    val isSelected = selectedHoliday == holiday
                    Surface(
                        onClick = { selectedHoliday = holiday },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) WishColors.GoldAccent else WishColors.SurfaceElevated,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) WishColors.GoldAccent else WishColors.BorderSubtle
                        )
                    ) {
                        Text(
                            text = "${Holidays.emojiFor(holiday)} $holiday",
                            color = if (isSelected) WishColors.Background else WishColors.TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (showError) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Заповни назву та коректну вартість",
                    color = WishColors.DangerRed,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Кнопка збереження
            Button(
                onClick = {
                    val parsedPrice = priceText.toDoubleOrNull()
                    if (title.isBlank() || parsedPrice == null) {
                        showError = true
                    } else {
                        val result = WishItem(
                            id = editingItem?.id ?: 0L,
                            title = title.trim(),
                            description = description.trim(),
                            price = parsedPrice,
                            imageUri = imageUri.trim(),
                            holiday = selectedHoliday,
                            isFulfilled = editingItem?.isFulfilled ?: false,
                            createdAt = editingItem?.createdAt ?: System.currentTimeMillis()
                        )
                        onSave(result)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WishColors.GoldAccent,
                    contentColor = WishColors.Background
                )
            ) {
                Text(
                    text = if (editingItem == null) "Додати в список" else "Зберегти зміни",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun WishTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        singleLine = singleLine,
        minLines = minLines,
        isError = isError,
        keyboardOptions = keyboardOptions,
        leadingIcon = leadingIcon,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = WishColors.GoldAccent,
            unfocusedBorderColor = WishColors.BorderSubtle,
            focusedLabelColor = WishColors.GoldAccent,
            unfocusedLabelColor = WishColors.TextSecondary,
            focusedTextColor = WishColors.TextPrimary,
            unfocusedTextColor = WishColors.TextPrimary,
            cursorColor = WishColors.GoldAccent,
            focusedContainerColor = WishColors.SurfaceElevated,
            unfocusedContainerColor = WishColors.SurfaceElevated
        )
    )
}