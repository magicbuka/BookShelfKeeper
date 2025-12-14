package com.bookshelf.keeper.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bookshelf.keeper.ui.common.clearFocusOnTapOutside
import androidx.lifecycle.viewmodel.compose.viewModel

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
    var locationLevel1 by remember { mutableStateOf("") }  // Комната

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
                    if (newValue.length <= MAX_TITLE_LENGTH) {
                        title = newValue
                    }
                },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = authors,
                onValueChange = { newValue ->
                    if (newValue.length <= MAX_AUTHORS_LENGTH) {
                        authors = newValue
                    }
                },
                label = { Text("Автор(ы)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            OutlinedTextField(
                value = locationLevel1,
                onValueChange = { newValue ->
                    if (newValue.length <= MAX_LOCATION_LEVEL1_LENGTH) {
                        locationLevel1 = newValue
                    }
                },
                label = { Text("Комната *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Button(
                onClick = {
                    viewModel.saveBook(title, authors, locationLevel1)
                    onBackClick()
                },
                enabled = title.isNotBlank() &&
                        authors.isNotBlank() &&
                        locationLevel1.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Сохранить")
            }
        }
    }
}
