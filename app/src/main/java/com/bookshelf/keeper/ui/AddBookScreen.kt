package com.bookshelf.keeper.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookshelf.keeper.ui.common.clearFocusOnTapOutside
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.foundation.text.KeyboardActions


private const val MAX_TITLE_LENGTH = 255
private const val MAX_AUTHORS_LENGTH = 255
private const val MAX_LOCATION_LEVEL1_LENGTH = 100
private const val MAX_LEVEL2_SUGGESTIONS = 7

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    onBackClick: () -> Unit,
    bookId: Long? = null,
    viewModel: AddBookViewModel = viewModel()
) {

    val loadedBookState = viewModel.loadedBook.collectAsState()
    val loadedBook = loadedBookState.value

    var title by remember { mutableStateOf("") }
    var authors by remember { mutableStateOf("") }

    // язык, введённый пользователем
    var languageInput by remember { mutableStateOf("") }          // строка в поле
    var selectedLanguageCode by remember { mutableStateOf("") }   // код для БД
    var isLanguageExpanded by remember { mutableStateOf(false) }

    // подсказки из VM: сначала использованные, потом остальные
    val languageSuggestionsState = viewModel.languageSuggestions.collectAsState()
    val languageSuggestions = languageSuggestionsState.value

    var locationLevel1 by remember { mutableStateOf("") }
    var locationLevel2 by remember { mutableStateOf("") }
    var isRoomDropdownExpanded by remember { mutableStateOf(false) }
    val roomsState = viewModel.rooms.collectAsState()
    val allRooms = roomsState.value
    val level2SuggestionsState = viewModel.level2Suggestions.collectAsState()
    val allLevel2 = level2SuggestionsState.value

    val isEditMode = bookId != null
    val focusManager = LocalFocusManager.current

    // если пришёл bookId и книга ещё не загружена — инициируем загрузку
    LaunchedEffect(bookId) {
        if (bookId != null) {
            viewModel.loadBook(bookId)
        }
    }

    // когда loadedBook меняется впервые — заполняем поля
    LaunchedEffect(loadedBook) {
        loadedBook?.let { book ->
            title = book.title
            authors = book.authors
            locationLevel1 = book.locationLevel1
            viewModel.onRoomChanged(book.locationLevel1)
            locationLevel2 = book.locationLevel2 ?: ""
            selectedLanguageCode = book.language

            // Найти LanguageItem по коду (из существующих подсказок или общего списка)
            val allLanguages = viewModel.languageSuggestions.value
            val match = allLanguages.firstOrNull { it.code.equals(book.language, ignoreCase = true) }

            languageInput = if (match != null) {
                "${match.name} (${match.code})"
            } else {
                book.language.uppercase()  // fallback, если что-то пойдёт не так
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (bookId != null) "Редактировать книгу" else "Добавить книгу") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (isEditMode) {
                        viewModel.updateBook(
                            title = title,
                            authors = authors,
                            locationLevel1 = locationLevel1,
                            language = selectedLanguageCode,
                            locationLevel2 = locationLevel2.ifBlank { null }
                        )
                    } else {
                        viewModel.saveBook(
                            title = title,
                            authors = authors,
                            locationLevel1 = locationLevel1,
                            language = selectedLanguageCode,
                            locationLevel2 = locationLevel2.ifBlank { null }
                        )
                    }
                    onBackClick()
                },
                enabled = title.isNotBlank() &&
                        authors.isNotBlank() &&
                        locationLevel1.isNotBlank() &&
                        selectedLanguageCode.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Text(if (isEditMode) "Сохранить изменения" else "Добавить книгу")
            }
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .clearFocusOnTapOutside()
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { newValue ->
                    title = if (newValue.length <= MAX_TITLE_LENGTH) {
                        newValue
                    } else {
                        newValue.take(MAX_TITLE_LENGTH)
                    }
                },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            OutlinedTextField(
                value = authors,
                onValueChange = { newValue ->
                    authors = if (newValue.length <= MAX_AUTHORS_LENGTH) {
                        newValue
                    } else {
                        newValue.take(MAX_AUTHORS_LENGTH)
                    }
                },
                label = { Text("Автор(ы)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            // --- Автодополнение языка ---
            ExposedDropdownMenuBox(
                expanded = isLanguageExpanded,
                onExpandedChange = { isLanguageExpanded = !isLanguageExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                TextField(
                    value = languageInput,
                    onValueChange = { newValue ->
                        languageInput = newValue
                        isLanguageExpanded = true     // открываем список при вводе
                    },
                    label = { Text("Язык") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isLanguageExpanded)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            // если список открыт — закрываем
                            if (isLanguageExpanded) {
                                isLanguageExpanded = false
                            }
                            // переносим фокус на следующее поле (Комната)
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )

                val query = languageInput.trim()

                // 1) если поле пустое ИЛИ содержит полное значение "Name (code)" — показываем ВЕСЬ список;
                // 2) иначе фильтруем по коду/названию.
                val filteredSuggestions =
                    if (query.isEmpty() || query.endsWith(")")) {
                        languageSuggestions
                    } else {
                        val exact = languageSuggestions.filter { item ->
                            item.code.equals(query, ignoreCase = true) ||
                                    item.name.equals(query, ignoreCase = true)
                        }
                        val partial = languageSuggestions.filter { item ->
                            item.code.contains(query, ignoreCase = true) ||
                                    item.name.contains(query, ignoreCase = true)
                        }
                        (exact + partial).distinctBy { it.code }
                    }

                ExposedDropdownMenu(
                    expanded = isLanguageExpanded && filteredSuggestions.isNotEmpty(),
                    onDismissRequest = { isLanguageExpanded = false }
                ) {
                    filteredSuggestions.forEach { item ->
                        val displayText = "${item.name} (${item.code})"
                        DropdownMenuItem(
                            text = { Text(displayText) },
                            onClick = {
                                selectedLanguageCode = item.code           // в БД идёт код
                                languageInput = displayText                // в поле показываем Name (code)
                                isLanguageExpanded = false
                            },
                            leadingIcon = {
                                if (item.code.equals(selectedLanguageCode, ignoreCase = true)) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                }
            }
            // --- конец блока языка ---


            val roomSuggestions = allRooms
                .filter { it.contains(locationLevel1, ignoreCase = true) && it.isNotBlank() }
                .sorted()

            ExposedDropdownMenuBox(
                expanded = isRoomDropdownExpanded && roomSuggestions.isNotEmpty(),
                onExpandedChange = {
                    if (roomSuggestions.isNotEmpty()) {
                        isRoomDropdownExpanded = !isRoomDropdownExpanded
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = locationLevel1,
                    onValueChange = { newValue ->
                        val limited = if (newValue.length <= MAX_LOCATION_LEVEL1_LENGTH) {
                            newValue
                        } else {
                            newValue.take(MAX_LOCATION_LEVEL1_LENGTH)
                        }
                        locationLevel1 = limited
                        viewModel.onRoomChanged(locationLevel1)
                        isRoomDropdownExpanded = roomSuggestions.isNotEmpty()
                    },
                    label = { Text("Комната") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            // если меню открыто — закрываем, чтобы не мешало
                            if (isRoomDropdownExpanded) {
                                isRoomDropdownExpanded = false
                            }
                            // переносим фокус на следующее поле
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )

                ExposedDropdownMenu(
                    expanded = isRoomDropdownExpanded && roomSuggestions.isNotEmpty(),
                    onDismissRequest = { isRoomDropdownExpanded = false }
                ) {
                    roomSuggestions.forEach { room ->
                        DropdownMenuItem(
                            text = { Text(room) },
                            onClick = {
                                locationLevel1 = room
                                viewModel.onRoomChanged(room)
                                isRoomDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            val level2Filtered = allLevel2
                .filter { it.contains(locationLevel2, ignoreCase = true) && it.isNotBlank() }
                .sorted()
                .take(MAX_LEVEL2_SUGGESTIONS)

            var isLevel2DropdownExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = isLevel2DropdownExpanded && level2Filtered.isNotEmpty(),
                onExpandedChange = {
                    if (level2Filtered.isNotEmpty()) {
                        isLevel2DropdownExpanded = !isLevel2DropdownExpanded
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = locationLevel2,
                    onValueChange = { newValue ->
                        locationLevel2 = newValue.take(100)
                        isLevel2DropdownExpanded = level2Filtered.isNotEmpty()
                    },
                    label = { Text("Шкаф / полка") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (title.isNotBlank() &&
                                authors.isNotBlank() &&
                                locationLevel1.isNotBlank() &&
                                selectedLanguageCode.isNotBlank()
                            ) {
                                if (isEditMode) {
                                    viewModel.updateBook(
                                        title = title,
                                        authors = authors,
                                        locationLevel1 = locationLevel1,
                                        language = selectedLanguageCode,
                                        locationLevel2 = locationLevel2.ifBlank { null }
                                    )
                                } else {
                                    viewModel.saveBook(
                                        title = title,
                                        authors = authors,
                                        locationLevel1 = locationLevel1,
                                        language = selectedLanguageCode,
                                        locationLevel2 = locationLevel2.ifBlank { null }
                                    )
                                }
                                onBackClick()
                            } else {
                                // если что‑то не заполнено — просто убираем фокус
                                focusManager.clearFocus()
                            }
                        }
                    )
                )

                ExposedDropdownMenu(
                    expanded = isLevel2DropdownExpanded && level2Filtered.isNotEmpty(),
                    onDismissRequest = { isLevel2DropdownExpanded = false }
                ) {
                    level2Filtered.forEach { shelf ->
                        DropdownMenuItem(
                            text = { Text(shelf) },
                            onClick = {
                                locationLevel2 = shelf
                                isLevel2DropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))

        }
    }
}
