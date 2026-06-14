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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class TodoItem(
    val id: String = "",
    val text: String = "",
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
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val auth = remember { FirebaseAuth.getInstance() }
    var userId by remember { mutableStateOf(auth.currentUser?.uid) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            userId = firebaseAuth.currentUser?.uid
        }
        auth.addAuthStateListener(listener)

        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }

    if (userId == null) {
        LoginScreen(auth = auth)
    } else {
        TodoScreen(
            userId = userId ?: "",
            onLogout = { auth.signOut() }
        )
    }
}

@Composable
fun LoginScreen(auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("login_screen"),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag("login_title")
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-Mail") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("email_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Passwort") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("password_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                message = ""
                auth.signInWithEmailAndPassword(email.trim(), password)
                    .addOnCompleteListener { task ->
                        message = if (task.isSuccessful) {
                            "Login erfolgreich"
                        } else {
                            task.exception?.message ?: "Login fehlgeschlagen"
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_button")
        ) {
            Text("Einloggen")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                message = ""
                auth.createUserWithEmailAndPassword(email.trim(), password)
                    .addOnCompleteListener { task ->
                        message = if (task.isSuccessful) {
                            "Registrierung erfolgreich"
                        } else {
                            task.exception?.message ?: "Registrierung fehlgeschlagen"
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_button")
        ) {
            Text("Registrieren")
        }

        if (message.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                modifier = Modifier.testTag("auth_message")
            )
        }
    }
}

@Composable
fun TodoScreen(
    userId: String,
    onLogout: () -> Unit
) {
    val db = remember { FirebaseFirestore.getInstance() }
    val todos = remember { mutableStateListOf<TodoItem>() }
    var input by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val tasksRef = remember(userId) {
        db.collection("users")
            .document(userId)
            .collection("tasks")
    }

    DisposableEffect(userId) {
        val registration = tasksRef
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    errorMessage = "Fehler beim Laden der Aufgaben"
                    return@addSnapshotListener
                }

                todos.clear()

                snapshot?.documents?.forEach { document ->
                    todos.add(
                        TodoItem(
                            id = document.id,
                            text = document.getString("text") ?: "",
                            done = document.getBoolean("done") ?: false
                        )
                    )
                }
            }

        onDispose {
            registration.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("todo_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Android ToDo Testing",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("todo_title")
            )

            TextButton(
                onClick = onLogout,
                modifier = Modifier.testTag("logout_button")
            ) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
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
                    val task = hashMapOf(
                        "text" to input.trim(),
                        "done" to false,
                        "createdAt" to System.currentTimeMillis()
                    )

                    tasksRef.add(task)
                        .addOnSuccessListener {
                            input = ""
                            errorMessage = null
                        }
                        .addOnFailureListener {
                            errorMessage = "Aufgabe konnte nicht gespeichert werden"
                        }
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
                                tasksRef.document(todo.id)
                                    .update("done", checked)
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
                                tasksRef.document(todo.id).delete()
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