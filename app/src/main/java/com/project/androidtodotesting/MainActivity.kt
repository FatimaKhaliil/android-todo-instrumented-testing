package com.project.androidtodotesting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

data class TodoItem(
    val id: Int,
    val text: String,
    val done: Boolean = false
)

object TodoValidator {
    fun isValidTask(text: String): Boolean {
        return text.trim().isNotEmpty()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TodoScreen()
            }
        }
    }
}

@Composable
fun TodoScreen() {
    var input by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var nextId by remember { mutableIntStateOf(1) }
    val todos = remember { mutableStateListOf<TodoItem>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("todo_screen")
    ) {
        Text(
            text = "Android ToDo Testing",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.testTag("todo_title")
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = input,
            onValueChange = {
                input = it
                errorMessage = null
            },
            label = { Text("Neue Aufgabe") },
            isError = errorMessage != null,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("task_input")
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag("error_message")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (TodoValidator.isValidTask(input)) {
                    todos.add(
                        0,
                        TodoItem(
                            id = nextId,
                            text = input.trim(),
                            done = false
                        )
                    )
                    nextId++
                    input = ""
                    errorMessage = null
                } else {
                    errorMessage = "Bitte Aufgabe eingeben"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_button")
        ) {
            Text("Aufgabe hinzufügen")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (todos.isEmpty()) {
            Text(
                text = "Keine Aufgaben vorhanden",
                modifier = Modifier.testTag("empty_state")
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.testTag("todo_list")
        ) {
            items(todos, key = { it.id }) { todo ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("todo_item_${todo.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Checkbox(
                            checked = todo.done,
                            onCheckedChange = { checked ->
                                val index = todos.indexOfFirst { it.id == todo.id }
                                if (index != -1) {
                                    todos[index] = todo.copy(done = checked)
                                }
                            },
                            modifier = Modifier.testTag("done_checkbox_${todo.id}")
                        )

                        Text(
                            text = todo.text,
                            textDecoration = if (todo.done) {
                                TextDecoration.LineThrough
                            } else {
                                TextDecoration.None
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                                .testTag("task_text_${todo.id}")
                        )

                        TextButton(
                            onClick = {
                                todos.remove(todo)
                            },
                            modifier = Modifier.testTag("delete_button_${todo.id}")
                        ) {
                            Text("Löschen")
                        }
                    }
                }
            }
        }
    }
}