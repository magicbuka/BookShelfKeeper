package com.bookshelf.keeper.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookshelf.keeper.ui.common.clearFocusOnTapOutside


private const val MAX_TITLE_LENGTH = 255
private const val MAX_AUTHORS_LENGTH = 255
private const val MAX_LOCATION_LEVEL1_LENGTH = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    onBackClick: () -> Unit,
    viewModel: AddBookViewModel = viewModel()
) {
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
    var isRoomDropdownExpanded by remember { mutableStateOf(false) }
    val roomsState = viewModel.rooms.collectAsState()
    val allRooms = roomsState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить книгу") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
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
                modifier = Modifier.fillMaxWidth()
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
                    .padding(top = 8.dp)
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
                    singleLine = true
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
                        isRoomDropdownExpanded = roomSuggestions.isNotEmpty()
                    },
                    label = { Text("Комната") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
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
                                isRoomDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.saveBook(
                        title = title,
                        authors = authors,
                        locationLevel1 = locationLevel1,
                        language = selectedLanguageCode
                    )
                    onBackClick()
                },
                enabled = title.isNotBlank() &&
                        authors.isNotBlank() &&
                        locationLevel1.isNotBlank() &&
                        selectedLanguageCode.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Сохранить")
            }
        }
    }
}
